package org.jaoed.target;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Optional;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    @Mock TargetCommand dummyCommand;

    @Test
    public void testDispatcher() {
        HashMap<String, String> results = new HashMap<>();

        CommandDispatcher dispatcher =
                CommandDispatcher.newBuilder()
                        .addCommandFactory(
                                AoeCommand.ISSUE_ATA,
                                ctx -> {
                                    results.put("first", "called");
                                    return Optional.of(dummyCommand);
                                })
                        .addCommandFactory(
                                AoeCommand.QUERY_CONFIG,
                                ctx -> {
                                    results.put("second", "called next");
                                    return Optional.of(dummyCommand);
                                })
                        .build();
        assertNotNull(dispatcher);

        // Test first.
        assertTrue(dispatcher.makeCommand(wrap(AoeCommand.ISSUE_ATA)).isPresent());
        assertEquals("called", results.get("first"));

        // Test second.
        assertTrue(dispatcher.makeCommand(wrap(AoeCommand.QUERY_CONFIG)).isPresent());
        assertEquals("called next", results.get("second"));

        // Test non-existant command.
        assertFalse(dispatcher.makeCommand(wrap(AoeCommand.RESERVE_RELEASE)).isPresent());
    }

    private static RequestContext wrap(AoeCommand cmd) {
        AoeFrame frame = new AoeFrame.Builder().command(cmd).build();
        RequestContext ctx = mock(RequestContext.class);
        when(ctx.getAoeFrame()).thenReturn(frame);
        return ctx;
    }
}
