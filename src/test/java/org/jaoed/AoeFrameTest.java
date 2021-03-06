package org.jaoed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;
import org.jaoed.packet.namednumber.AoeError;
import org.junit.Test;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AoeFrameTest {
    private static final Logger LOG = LoggerFactory.getLogger(AoeFrameTest.class);

    @Test
    public void testPcapReader()
            throws PcapNativeException, NotOpenException, IllegalRawDataException {

        String file = getClass().getClassLoader().getResource("sampleAoeFrame.pcap").getFile();
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

                Packet payload = frame.getPayload();
                assertNotNull(payload);
                byte[] rawPayload = payload.getRawData();
                AoeFrame aoeFrame = AoeFrame.newPacket(rawPayload, 0, rawPayload.length);
                assertNotNull(aoeFrame);
                LOG.info("received aoe frame {}", aoeFrame);

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

    @Test
    public void testAoeFrameBuilder() throws IllegalRawDataException {
        AoeFrame.Builder builder = new AoeFrame.Builder();

        builder.version((byte) 1)
                .responseFlag(true)
                .responseErrorFlag(true)
                .error((byte) 1)
                .majorNumber((short) 222)
                .minorNumber((byte) 33)
                .command((byte) 3)
                .tag(new byte[] {0x11, 0x22, 0x33, 0x44});

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
