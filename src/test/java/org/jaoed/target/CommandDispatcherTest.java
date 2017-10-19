package org.jaoed.target;

import java.util.HashMap;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Test;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;

public class CommandDispatcherTest {
    @Test
    public void testDispatcher() {
        HashMap<String, String> results = new HashMap<>();

        CommandDispatcher dispatcher = CommandDispatcher
            .newBuilder()
            .addCommandFactory(
                AoeCommand.ISSUE_ATA, ctx -> {
                    results.put("first", "called");
                    return null;
                })
            .addCommandFactory(
                AoeCommand.QUERY_CONFIG, ctx -> {
                    results.put("second", "called next");
                    return null;
                })
            .build();
        assertNotNull(dispatcher);

        // Test first.
        dispatcher.makeCommand(wrap(AoeCommand.ISSUE_ATA));
        assertEquals("called", results.get("first"));

        // Test second.
        dispatcher.makeCommand(wrap(AoeCommand.QUERY_CONFIG));
        assertEquals("called next", results.get("second"));

        // Test non-existant command.
        assertNull(dispatcher.makeCommand(wrap(AoeCommand.RESERVE_RELEASE)));
    }

    private static RequestContext wrap(AoeCommand cmd) {
        AoeFrame frame = new AoeFrame.Builder()
            .command(cmd)
            .build();
        RequestContext ctx = mock(RequestContext.class);
        when(ctx.getAoeFrame()).thenReturn(frame);
        return ctx;
    }
}
