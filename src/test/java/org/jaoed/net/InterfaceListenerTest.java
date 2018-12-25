package org.jaoed.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import org.jaoed.config.Interface;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pcap4j.core.Pcaps;
import org.pcap4j.util.MacAddress;

@RunWith(MockitoJUnitRunner.class)
public class InterfaceListenerTest {
    @Mock ProcessorRegistry processorRegistry;
    @Mock Interface iface;

    @Test
    public void testInterfaceListener() throws Exception {
        String file = getClass().getClassLoader().getResource("sampleAoeFrame.pcap").getFile();
        assertNotNull(file);

        LinkedList<RequestContext> captured = new LinkedList<>();
        PacketProcessor processor =
                ctx -> {
                    captured.add(ctx);
                    return true;
                };
        when(processorRegistry.lookup(any()))
                .thenReturn(Optional.of(Collections.singletonList(processor)));
        when(iface.getName()).thenReturn("eth0");

        InterfaceListener listener =
                new InterfaceListener(
                        iface,
                        processorRegistry,
                        1000,
                        iface -> {
                            try {
                                return Pcaps.openOffline(file);
                            } catch (Exception e) {
                                return null;
                            }
                        },
                        MacAddress.getByName("de:ad:be:ef:00:01"));

        listener.start();
        Thread.sleep(1000);
        listener.stop();

        assertEquals(1, captured.size());
    }
}
