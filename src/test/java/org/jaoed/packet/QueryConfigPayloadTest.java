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

public class QueryConfigPayloadTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueryConfigPayloadTest.class);

    @Test
    public void testBuilder() throws IllegalRawDataException {
        QueryConfigPayload.Builder builder = new QueryConfigPayload.Builder()
            .bufferCount((short) 1)
            .firmwareVersion((short) 2)
            .sectorCount((byte) 3)
            .aoeProtocolVersion((byte) 4)
            .subCommand((byte) 2)
            .configStringLength((short) 13);

        QueryConfigPayload queryConfig = builder.build();
        assertNotNull(queryConfig);
        LOG.info("built query config: {}", queryConfig);

        // Now parse the built packet.
        byte[] rawPacket = queryConfig.getRawData();
        QueryConfigPayload queryConfig2 = QueryConfigPayload.newPacket(rawPacket, 0, rawPacket.length);
        assertNotNull(queryConfig2);
        LOG.info("parsed query config: {}", queryConfig2);

        QueryConfigPayload.QueryConfigPayloadHeader header = queryConfig.getHeader();
        QueryConfigPayload.QueryConfigPayloadHeader header2 = queryConfig2.getHeader();
        assertTrue(header.equals(header2));

        QueryConfigPayload.Builder builder2 = queryConfig.getBuilder();
        QueryConfigPayload queryConfig3 = builder2.build();
        LOG.info("3rd built query config: {}", queryConfig3);
        QueryConfigPayload.QueryConfigPayloadHeader header3 = queryConfig3.getHeader();
        assertTrue(header.equals(header3));
    }
}
