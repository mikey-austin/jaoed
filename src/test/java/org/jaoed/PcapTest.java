package org.jaoed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;

import org.jaoed.pcap4j.AoeFrame;

public class PcapTest extends TestCase {

    public PcapTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(PcapTest.class);
    }

    public void testPcapReader()
        throws PcapNativeException, NotOpenException, IllegalRawDataException {

        String file = getClass()
            .getClassLoader()
            .getResource("sampleAoeFrame.pcap")
            .getFile();
        assertNotNull(file);

        PcapHandle handle = Pcaps.openOffline(file);
        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                EthernetPacket frame = packet.get(EthernetPacket.class);
                assertNotNull(frame);

                EthernetPacket.EthernetHeader header = frame.getHeader();
                assertNotNull(header);

                assertEquals("de:ad:be:ef:00:02", header.getSrcAddr().toString());
                assertEquals("de:ad:be:ef:00:01", header.getDstAddr().toString());

                // TODO: make an AoE packet so we can call methods on it...
                Packet payload = packet.getPayload();
                assertNotNull(payload);
                byte[] rawPayload = payload.getRawData();
                AoeFrame aoeFrame = AoeFrame.newPacket(
                    rawPayload, 0, rawPayload.length);
                assertNotNull(aoeFrame);
            } catch (TimeoutException e) {
            } catch (EOFException e) {
                break;
            }
        }

        handle.close();
    }
}
