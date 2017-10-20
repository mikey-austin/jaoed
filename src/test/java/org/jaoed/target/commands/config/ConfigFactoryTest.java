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
import org.jaoed.packet.QueryConfig;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.DeviceConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

@RunWith(MockitoJUnitRunner.class)
public class ConfigFactoryTest {
    @Mock DeviceTarget target;
    @Mock AoeFrame a1;
    @Mock RequestContext ctx1;
    @Mock ResponseBuilder responseBuilder;
    @Mock BiFunction<RequestContext, DeviceTarget, ResponseBuilder> responseBuilderFactory;

    @Test
    public void testReadConfig() {
        DeviceConfigArea emptyArea = new DeviceConfigArea();
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.READ_CONFIG)
            .build();

        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        // Test empty config string.
        byte[] configStr = new byte[] { 'a', 'b', 'c' };
        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(responseBuilder, never()).setPayload(configStr);

        // Test non-empty config string.
        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        nonEmptyArea.setConfig(configStr);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(responseBuilder).setPayload(configStr);
    }

    @Test
    public void testFullMatch() {
        byte[] toMatch = new byte[] { 'a', 'b', 'c' };
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.TEST_FULL_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMatch))
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        byte[] configStr = new byte[] { 'a', 'b', 'c' };
        nonEmptyArea.setConfig(configStr);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        // Test that the full payload is returned in the response.
        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        assertEquals(responseBuilder, cmd.execute(target));
        verify(responseBuilder, times(1)).setPayload(configStr);

        // Test mismatch.
        byte[] toMismatch = new byte[] { '1', '2', '3' };
        query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.TEST_FULL_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMismatch))
            .build();
        when(a1.getPayload()).thenReturn(query);
        cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        assertNull(cmd.execute(target));
        verify(responseBuilder, times(1)).setPayload(configStr); // ie was not called again.
    }

    @Test
    public void testPrefixMatch() {
        byte[] toMatch = new byte[] { 'a', 'b' };
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.TEST_PREFIX_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMatch))
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        byte[] configStr = new byte[] { 'a', 'b', 'c', 'd' };
        nonEmptyArea.setConfig(configStr);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        // Test that the full payload is returned in the response.
        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        assertEquals(responseBuilder, cmd.execute(target));
        verify(responseBuilder, times(1)).setPayload(configStr);

        // Test mismatch.
        byte[] toMismatch = new byte[] { '1', '2', '3' };
        query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.TEST_PREFIX_MATCH)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toMismatch))
            .build();
        when(a1.getPayload()).thenReturn(query);
        cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        assertNull(cmd.execute(target));
        verify(responseBuilder, times(1)).setPayload(configStr); // ie was not called again.
    }

    @Test
    public void testSetIfEmpty() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.SET_IF_EMPTY)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .build();

        DeviceConfigArea emptyArea = new DeviceConfigArea();
        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(responseBuilder).setPayload(toSet);
        assertTrue(Arrays.equals(toSet, emptyArea.getConfig()));
    }

    @Test
    public void testSetIfEmptyFail() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.SET_IF_EMPTY)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .build();

        DeviceConfigArea nonEmptyArea = new DeviceConfigArea();
        nonEmptyArea.setConfig(toSet);
        when(target.getConfigArea()).thenReturn(nonEmptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(responseBuilder, never()).setPayload(toSet);
        verify(responseBuilder).setError(AoeError.CANNOT_SET_CONFIG);
    }

    @Test
    public void testForceSet() {
        byte[] toSet = new byte[] { '1', '2', '3' };
        QueryConfig query = new QueryConfig.Builder()
            .subCommand(QueryConfigCommand.SET_FORCE)
            .payloadBuilder(
                new UnknownPacket.Builder().rawData(toSet))
            .build();

        DeviceConfigArea emptyArea = new DeviceConfigArea();
        when(target.getConfigArea()).thenReturn(emptyArea);
        when(a1.getPayload()).thenReturn(query);
        when(ctx1.getAoeFrame()).thenReturn(a1);
        when(responseBuilderFactory.apply(ctx1, target)).thenReturn(responseBuilder);

        ConfigFactory configFactory = new ConfigFactory(responseBuilderFactory);
        TargetCommand cmd = configFactory.makeCommand(ctx1);
        assertNotNull(cmd);
        cmd.execute(target);
        verify(responseBuilder).setPayload(toSet);
        assertTrue(Arrays.equals(toSet, emptyArea.getConfig()));
    }
}
