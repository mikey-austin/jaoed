package org.jaoed.net;

import java.lang.Thread;
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

import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.*;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;

@RunWith(MockitoJUnitRunner.class)
public class InterfaceListenerTest {
    @Mock ProcessorRegistry processorRegistry;

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
            .thenReturn(Optional.of(processor));

        PcapHandle handle = Pcaps.openOffline(file);
        InterfaceListener listener = new InterfaceListener(
            handle, processorRegistry);

        Thread thread = new Thread(listener);
        thread.run();
        listener.stop();
        thread.join();

        assertEquals(1, captured.size());
    }
}
