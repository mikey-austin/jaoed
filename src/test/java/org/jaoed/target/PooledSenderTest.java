package org.jaoed.target;

import static org.jaoed.target.TargetUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.*;

@RunWith(MockitoJUnitRunner.class)
public class PooledSenderTest {
    @Test
    public void testPooledSender() throws Exception {
        ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();
        TargetResponse response1 = () -> results.put(1, "response 1 sent");
        TargetResponse response2 = () -> results.put(2, "response 2 sent");

        PooledSender sender = new PooledSender(5, 1000, 5);
        sender.enqueue(response1);
        sender.enqueue(response2);

        sender.start();
        Thread.sleep(1000);
        sender.stop();
        assertEquals(2, results.size());
    }
}
