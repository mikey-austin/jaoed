package org.jaoed.target.commands.config;

import java.util.Arrays;
import java.util.function.BiFunction;

import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.QueryConfigPayload;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigSubCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;
import static org.jaoed.target.TargetUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class QueryConfigResponseTest {
    @Mock DeviceTarget target;
    @Mock RequestContext ctx;

    @Test
    public void testQueryConfigResponse() throws Exception {
        QueryConfigPayload.Builder query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.READ_CONFIG); ;
        AoeFrame aoeFrame = new AoeFrame.Builder()
            .version((byte) 1)
            .responseFlag(false)
            .responseErrorFlag(false)
            .majorNumber((short) 222)
            .minorNumber((byte) 33)
            .command((byte) 1)
            .tag(new byte[] { 0x11, 0x22, 0x33, 0x44 })
            .payloadBuilder(query)
            .build();
        EthernetPacket ethFrame = new EthernetPacket.Builder()
            .paddingAtBuild(true)
            .type(
                new EtherType((short) 0x88A2, "AOE"))
            .srcAddr(
                MacAddress.getByName("de:ad:be:ef:00:01"))
            .dstAddr(
                MacAddress.getByName("de:ad:be:ef:00:02"))
            .build();

        int shelf = 123, slot = 37;
        Device device = new Device()
            .setShelf(shelf)
            .setSlot(slot);

        when(target.getFirmwareVersion()).thenReturn((short) 1);
        when(target.getSectorCount()).thenReturn((byte) 2);
        when(target.getBufferCount()).thenReturn((short) 3);
        when(target.getDevice()).thenReturn(device);
        when(ctx.getAoeFrame()).thenReturn(aoeFrame);
        when(ctx.getEthernetFrame()).thenReturn(ethFrame);

        byte[] payload = new byte[] { 'a', 'b', 'c' };
        QueryConfigResponse queryConfigResponse = new QueryConfigResponse(ctx, target);
        queryConfigResponse.setPayload(payload);

        EthernetPacket response = (EthernetPacket) queryConfigResponse.makeResponse();
        assertNotNull(response);

        // Make sure addresses are swapped.
        EthernetPacket.EthernetHeader ethHeader = response.getHeader();
        assertEquals("de:ad:be:ef:00:02", ethHeader.getSrcAddr().toString());
        assertEquals("de:ad:be:ef:00:01", ethHeader.getDstAddr().toString());

        // Pull out the wrapped packets to check the frame was built
        // correctly.
        AoeFrame innerAoe = AoeFrame.newPacket(response.getPayload());
        assertNotNull(innerAoe);
        assertTrue(innerAoe.getHeader().getResponseFlag());
        assertFalse(innerAoe.getHeader().getResponseErrorFlag());
        assertEquals(shelf, encodeMajor(innerAoe.getHeader().getMajorNumber()));
        assertEquals(slot, encodeMinor(innerAoe.getHeader().getMinorNumber()));

        QueryConfigPayload innerCmd = QueryConfigPayload.newPacket(innerAoe.getPayload());
        assertNotNull(innerCmd);
        assertEquals((short) 1, innerCmd.getHeader().getFirmwareVersion());
        assertEquals((byte) 2, innerCmd.getHeader().getSectorCount());
        assertEquals((short) 3, innerCmd.getHeader().getBufferCount());
        assertEquals(payload.length, innerCmd.getHeader().getConfigStringLength());
    }
}
