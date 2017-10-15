package org.jaoed.target;

import java.util.HashMap;

import static org.junit.Assert.*;
import org.junit.Test;

import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;

public class CommandDispatcherTest {
    @Test
    public void testDispatcher() {
        HashMap<String, String> results = new HashMap<>();

        CommandDispatcher dispatcher = CommandDispatcher
            .newBuilder()
            .addCommandFactory(
                AoeCommand.ISSUE_ATA, (h, f) -> {
                    results.put("first", "called");
                    return null;
                })
            .addCommandFactory(
                AoeCommand.QUERY_CONFIG, (h, f) -> {
                    results.put("second", "called next");
                    return null;
                })
            .build();
        assertNotNull(dispatcher);

        // Test first.
        AoeFrame frame1 = new AoeFrame.Builder()
            .command(AoeCommand.ISSUE_ATA)
            .build();
        dispatcher.makeCommand(null, frame1);
        assertEquals("called", results.get("first"));

        // Test second.
        AoeFrame frame2 = new AoeFrame.Builder()
            .command(AoeCommand.QUERY_CONFIG)
            .build();
        dispatcher.makeCommand(null, frame2);
        assertEquals("called next", results.get("second"));
    }
}
