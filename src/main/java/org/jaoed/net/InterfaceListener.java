package org.jaoed.net;

import java.io.EOFException;
import java.lang.Runnable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.config.Interface;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;
import org.jaoed.service.Service;

public class InterfaceListener implements Runnable, Service {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceListener.class);

    private final Thread listenerThread;
    private final Interface iface;
    private final PcapHandle handle;
    private final ProcessorRegistry processorRegistry;
    private final Consumer<Packet> sender;
    private volatile boolean running;

    public InterfaceListener(Interface iface, ProcessorRegistry processorRegistry) {
        this.iface = iface;
        this.handle = iface.getPcapHandle();
        this.running = false;
        this.processorRegistry = processorRegistry;
        this.listenerThread = new Thread(this);
        this.sender = packet -> {
            try {
                handle.sendPacket(packet);
            } catch (Exception e) {
                LOG.error("could not send packet via {} interface", this, e);
            }
        };
    }

    @Override
    public void start() {
        listenerThread.start();
    }

    @Override
    public void stop() {
        this.running = false;
        try {
            listenerThread.join();
        } catch (Exception e) {
            LOG.error("an error occured whilst exiting listener thread", e);
        }
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

                RequestContext ctx = new RequestContext(packet, sender);
                LOG.trace("received {} frame at {}: {}",
                    handle, handle.getTimestamp(), ctx);

                PacketProcessor processor = processorRegistry
                    .lookup(ctx)
                    .orElseThrow(
                        () -> new Exception("no processor for " + ctx.toString()));
                processor.enqueue(ctx);
            } catch (EOFException e) {
                LOG.info("received EOF in {} listener", this);
                running = false;
            } catch (Exception e) {
                LOG.error("could not process {} packet", this, e);
            }
        }
    }

    @Override
    public String toString() {
        return iface.getName();
    }
}
