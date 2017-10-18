package org.jaoed.target;

import java.util.Arrays;

import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MockitoJUnitRunner.class)
public class ConfigAreaTest {
    @Test
    public void testConfigArea() {
        ConfigArea area = new DeviceConfigArea();
        assertTrue(area.isEmpty());

        byte[] bytes = new byte[] { 'h', 'e', 'l', 'l', 'o' };
        assertFalse(area.isCompleteMatch(bytes));
        assertFalse(area.isPrefixMatch(bytes));

        byte[] bytes2 = Arrays.copyOf(bytes, bytes.length);
        area.setConfig(bytes);
        assertFalse(area.isEmpty());
        assertTrue(area.isCompleteMatch(bytes2));

        byte[] bytes3 = new byte[] { 'h', 'e' };
        assertFalse(area.isCompleteMatch(bytes3));
        assertTrue(area.isPrefixMatch(bytes3));
        assertTrue(area.isPrefixMatch(bytes2));
        assertFalse(area.isPrefixMatch(new byte[] { 'z', 'z' }));
    }
}
