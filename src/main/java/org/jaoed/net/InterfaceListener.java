package org.jaoed.net;

import java.io.EOFException;
import java.lang.Runnable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.pcap4j.core.BpfProgram;
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
    private final int ifacePollMs;
    private volatile boolean running;

    public InterfaceListener(Interface iface, ProcessorRegistry processorRegistry, int ifacePollMs) throws Exception {
        this(iface, processorRegistry, ifacePollMs, _iface -> {
                try {
                    PcapHandle handle = new PcapHandle.Builder(_iface.getName())
                        .timeoutMillis(ifacePollMs)
                        .build();
                    handle.setFilter("ether proto 0x88A2", BpfProgram.BpfCompileMode.OPTIMIZE);
                    return handle;
                } catch (Exception e) {
                    LOG.error("could not create pcap handle", e);
                    return null;
                }
            });
    }

    public InterfaceListener(Interface iface, ProcessorRegistry processorRegistry, int ifacePollMs,
                             Function<Interface, PcapHandle> handleFactory) throws Exception {

        if ((this.handle = handleFactory.apply(iface)) == null) {
            throw new Exception("could not create pcap handle");
        }
        this.iface = iface;
        this.ifacePollMs = ifacePollMs;
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
        LOG.info("stopping {} listener thread", this);
        this.running = false;
        try {
            listenerThread.join();
        } catch (Exception e) {
            LOG.error("an error occured whilst exiting listener thread", e);
        }
    }

    @Override
    public void run() {
        LOG.info("starting {} listener thread [poll = {}ms]", this, ifacePollMs);
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

                List<PacketProcessor> processors = processorRegistry
                    .lookup(ctx)
                    .orElseThrow(
                        () -> new Exception("no processor for " + ctx.toString()));
                processors.forEach(
                    p -> p.enqueue(ctx));
            } catch (TimeoutException e) {
                // Nothing received, carry on.
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
