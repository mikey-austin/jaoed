package org.jaoed.packet;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.pcap4j.util.ByteArrays;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.packet.namednumber.*;

public class AtaPayloadTest {
    private static final Logger LOG = LoggerFactory.getLogger(AtaPayloadTest.class);

    @Test
    public void testBuilder() throws IllegalRawDataException {
        AtaPayload.Builder builder = new AtaPayload.Builder()
            .isAsync(true)
            .isWrite(true)
            .errFeature(210)
            .sectorCount(145)
            .cmdStatus(204)
            .lba(new int[] { 232, 2, 3, 4, 5, 6 });

        AtaPayload queryConfig = builder.build();
        assertNotNull(queryConfig);
        LOG.info("built ata: {}", queryConfig);

        // Now parse the built packet.
        byte[] rawPacket = queryConfig.getRawData();
        AtaPayload queryConfig2 = AtaPayload.newPacket(rawPacket, 0, rawPacket.length);
        assertNotNull(queryConfig2);
        LOG.info("parsed ata: {}", queryConfig2);

        AtaPayload.Header header = queryConfig.getHeader();
        AtaPayload.Header header2 = queryConfig2.getHeader();
        assertTrue(header.equals(header2));

        AtaPayload.Builder builder2 = queryConfig.getBuilder();
        AtaPayload queryConfig3 = builder2.build();
        LOG.info("3rd built ata: {}", queryConfig3);
        AtaPayload.Header header3 = queryConfig3.getHeader();
        assertTrue(header.equals(header3));
    }
}
