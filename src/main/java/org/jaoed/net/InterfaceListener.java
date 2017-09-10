package org.jaoed.net;

import java.io.EOFException;
import java.lang.Runnable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;

public class InterfaceListener implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceListener.class);

    private final PcapHandle handle;
    private final Map<String, PacketProcessor> processors;
    private volatile boolean running;

    public InterfaceListener(PcapHandle handle) {
        this.handle = handle;
        this.running = false;
        this.processors = new HashMap<>();
    }

    public void stop() {
        this.running = false;
    }

    public InterfaceListener addProcessor(String srcAddr, PacketProcessor processor) {
        processors.put(srcAddr, processor);
        return this;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                EthernetPacket packet = Optional
                    .ofNullable(handle.getNextPacketEx())
                    .map(p -> p.get(EthernetPacket.class))
                    .orElseThrow(() -> new Exception("received an invalid frame"));

                EthernetPacket.EthernetHeader header = packet.getHeader();
                AoeFrame aoeFrame = AoeFrame.newPacket(packet.getPayload());
                LOG.trace("received {} frame at {}: {} {}",
                    handle, handle.getTimestamp(), header, aoeFrame);

                // Evaluate ACLs, whether to drop the packet or not.
                PacketProcessor processor;
                if ((processor = processors.get(header.getDstAddr().toString())) != null) {
                    processor.enqueue(header, aoeFrame);
                } else {
                    throw new Exception("no processor for " + header.getDstAddr().toString());
                }
            } catch (EOFException e) {
                LOG.info("received EOF in {} listener", handle);
                stop();
            } catch (Exception e) {
                LOG.error("could not process {} packet", handle, e);
            }
        }
    }
}
