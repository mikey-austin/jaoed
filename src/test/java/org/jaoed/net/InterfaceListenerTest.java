package org.jaoed.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;

import org.jaoed.config.Interface;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.*;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;

@RunWith(MockitoJUnitRunner.class)
public class InterfaceListenerTest {
    @Mock ProcessorRegistry processorRegistry;
    @Mock Interface iface;

    @Test
    public void testInterfaceListener() throws Exception {
        String file = getClass()
            .getClassLoader()
            .getResource("sampleAoeFrame.pcap")
            .getFile();
        assertNotNull(file);

        LinkedList<RequestContext> captured = new LinkedList<>();
        PacketProcessor processor = ctx -> {
            captured.add(ctx);
            return true;
        };
        when(processorRegistry.lookup(any()))
            .thenReturn(Optional.of(Collections.singletonList(processor)));
        when(iface.getName()).thenReturn("eth0");

        InterfaceListener listener = new InterfaceListener(
            iface, processorRegistry, 1000, iface -> {
                try {
                    return Pcaps.openOffline(file);
                } catch (Exception e) {
                    return null;
                }
            });

        listener.start();
        Thread.sleep(1000);
        listener.stop();

        assertEquals(1, captured.size());
    }
}
