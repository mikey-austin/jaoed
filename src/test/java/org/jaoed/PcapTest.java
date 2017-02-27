package org.jaoed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;

import org.jaoed.pcap4j.AoeFrame;
import org.jaoed.pcap4j.namednumber.*;

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

                EthernetPacket.EthernetHeader ethHeader = frame.getHeader();
                assertNotNull(ethHeader);

                assertEquals("de:ad:be:ef:00:02", ethHeader.getSrcAddr().toString());
                assertEquals("de:ad:be:ef:00:01", ethHeader.getDstAddr().toString());

                Packet payload = packet.getPayload();
                assertNotNull(payload);
                byte[] rawPayload = payload.getRawData();
                AoeFrame aoeFrame = AoeFrame.newPacket(
                    rawPayload, 0, rawPayload.length);
                assertNotNull(aoeFrame);

                // Test the header parsing.
                AoeFrame.AoeHeader header = aoeFrame.getHeader();
                assertEquals(1, header.getVersion());
                assertEquals(false, header.getResponseFlag());
                assertEquals(false, header.getResponseErrorFlag());
                assertEquals(100, header.getMajorNumber());
                assertEquals(3, header.getMinorNumber());
                assertEquals(new Integer(1), new Integer(header.getCommand().value()));
                assertEquals(new Integer(0), new Integer(header.getError().value()));
                assertEquals("00000000", ByteArrays.toHexString(header.getTag(), ""));
            } catch (TimeoutException e) {
            } catch (EOFException e) {
                break;
            }
        }

        handle.close();
    }

    public void testAoeFrameBuilder() throws IllegalRawDataException {

        AoeFrame.Builder builder = new AoeFrame.Builder();

        builder.version((byte) 1)
            .responseFlag(true)
            .responseErrorFlag(true)
            .error((byte) 1)
            .majorNumber((short) 222)
            .minorNumber((byte) 33)
            .command((byte) 3)
            .tag(new byte[] { 0x11, 0x22, 0x33, 0x44 });

        AoeFrame aoeFrame = builder.build();
        assertNotNull(aoeFrame);

        // Now parse the built packet.
        byte[] rawPacket = aoeFrame.getRawData();
        aoeFrame = AoeFrame.newPacket(rawPacket, 0, rawPacket.length);
        assertNotNull(aoeFrame);

        AoeFrame.AoeHeader header = aoeFrame.getHeader();
        assertEquals(1, header.getVersion());
        assertEquals(true, header.getResponseFlag());
        assertEquals(true, header.getResponseErrorFlag());
        assertEquals(222, header.getMajorNumber());
        assertEquals(33, header.getMinorNumber());
        assertEquals(AoeCommand.RESERVE_RELEASE, header.getCommand());
        assertEquals(AoeError.CMD_UNKNOWN, header.getError());
        assertEquals("11223344", ByteArrays.toHexString(header.getTag(), ""));
    }
}
