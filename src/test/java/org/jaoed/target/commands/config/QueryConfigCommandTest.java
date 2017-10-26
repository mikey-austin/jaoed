package org.jaoed.target.commands.config;

import java.util.Arrays;
import java.util.function.BiFunction;

import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.pcap4j.packet.UnknownPacket;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.QueryConfigPayload;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigSubCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.DeviceConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;

@RunWith(MockitoJUnitRunner.class)
public class QueryConfigCommandTest {
    @Mock DeviceTarget target;
    @Mock AoeFrame a1;
    @Mock RequestContext ctx1;
    @Mock QueryConfigResponse queryConfigResponse;
    @Mock BiFunction<RequestContext, DeviceTarget, QueryConfigResponse> queryConfigResponseFactory;

    @Test
    public void testReadConfig() {
        DeviceConfigArea emptyArea = new DeviceConfigArea();
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.READ_CONFIG)
            .build();

        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        // Test empty config string.
        byte[] configStr = new byte[] { 'a', 'b', 'c' };
        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(queryConfigResponse, never()).setPayload(configStr);

        // Test non-empty config string.
        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        nonEmptyArea.setConfig(configStr, configStr.length);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(queryConfigResponse).setPayload(configStr);
    }

    @Test
    public void testFullMatch() {
        byte[] toMatch = new byte[] { 'a', 'b', 'c' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.TEST_FULL_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMatch))
            .configStringLength((short) toMatch.length)
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        byte[] configStr = new byte[] { 'a', 'b', 'c' };
        nonEmptyArea.setConfig(configStr, configStr.length);

        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setError(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        // Test that the full payload is returned in the response.
        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        assertEquals(queryConfigResponse, cmd.execute(target).orElse(null));
        verify(queryConfigResponse, times(1)).setPayload(configStr);

        // Test mismatch.
        byte[] toMismatch = new byte[] { '1', '2', '3' };
        query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.TEST_FULL_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMismatch))
            .build();
        when(a1.getPayload()).thenReturn(query);
        cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        assertFalse(cmd.execute(target).isPresent());
        verify(queryConfigResponse, times(1)).setPayload(configStr);
    }

    @Test
    public void testPrefixMatch() {
        byte[] toMatch = new byte[] { 'a', 'b' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.TEST_PREFIX_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMatch))
            .configStringLength((short) toMatch.length)
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        byte[] configStr = new byte[] { 'a', 'b', 'c', 'd' };
        nonEmptyArea.setConfig(configStr, configStr.length);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setError(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        // Test that the full payload is returned in the response.
        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        assertEquals(queryConfigResponse, cmd.execute(target).orElse(null));
        verify(queryConfigResponse, times(1)).setPayload(configStr);
    }

    @Test
    public void testPrefixMismatch() {
        byte[] toMatch = new byte[] { 'z', 'z' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.TEST_PREFIX_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMatch))
            .configStringLength((short) toMatch.length)
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        byte[] configStr = new byte[] { 'a', 'b', 'c', 'd' };
        nonEmptyArea.setConfig(configStr, configStr.length);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setError(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        // Test that the full payload is returned in the response.
        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        assertFalse(cmd.execute(target).isPresent());
    }

    @Test
    public void testSetIfEmpty() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.SET_IF_EMPTY)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .configStringLength((short) toSet.length)
            .build();

        DeviceConfigArea emptyArea = new DeviceConfigArea();
        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setPayload(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(queryConfigResponse).setPayload(toSet);
        assertTrue(Arrays.equals(toSet, emptyArea.getConfig()));
    }

    @Test
    public void testSetIfEmptyFail() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.SET_IF_EMPTY)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .configStringLength((short) toSet.length)
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        nonEmptyArea.setConfig(toSet, toSet.length);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setError(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(queryConfigResponse).setError(AoeError.CANNOT_SET_CONFIG);
    }

    @Test
    public void testForceSet() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfigPayload query = new QueryConfigPayload.Builder()
            .subCommand(QueryConfigSubCommand.SET_FORCE)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .configStringLength((short) toSet.length)
            .build();

        DeviceConfigArea emptyArea = new DeviceConfigArea();
        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(queryConfigResponse.setPayload(any())).thenReturn(queryConfigResponse);
        when(queryConfigResponseFactory.apply(ctx1, target)).thenReturn(queryConfigResponse);

        QueryConfigCommand queryConfigCommand = new QueryConfigCommand(queryConfigResponseFactory);
        TargetCommand cmd = queryConfigCommand.makeCommand(ctx1).orElse(null);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(queryConfigResponse).setPayload(toSet);
        assertTrue(Arrays.equals(toSet, emptyArea.getConfig()));
    }
}
