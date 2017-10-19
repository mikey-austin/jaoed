package org.jaoed.net;

import java.lang.Thread;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;

import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.*;
import org.jaoed.packet.PacketProcessor;

public class InterfaceListenerTest extends TestCase {

    public InterfaceListenerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(InterfaceListenerTest.class);
    }

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

        PcapHandle handle = Pcaps.openOffline(file);
        InterfaceListener listener = new InterfaceListener(handle)
            .addProcessor("de:ad:be:ef:00:01", processor);

        Thread thread = new Thread(listener);
        thread.run();
        listener.stop();
        thread.join();

        assertEquals(1, captured.size());
    }
}
