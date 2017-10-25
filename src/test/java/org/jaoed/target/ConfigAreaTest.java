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
        assertTrue(area.isCompleteMatch(new byte[] {}, 100));
        assertTrue(area.isPrefixMatch(new byte[] {}, 0));
        assertTrue(area.isEmpty());

        byte[] bytes = new byte[] { 'h', 'e', 'l', 'l', 'o' };
        assertFalse(area.isCompleteMatch(bytes, bytes.length));
        assertFalse(area.isPrefixMatch(bytes, bytes.length));

        byte[] bytes2 = Arrays.copyOf(bytes, bytes.length);
        area.setConfig(bytes, bytes.length);
        assertFalse(area.isEmpty());
        assertTrue(area.isCompleteMatch(bytes2, bytes2.length));

        byte[] bytes3 = new byte[] { 'h', 'e' };
        assertFalse(area.isCompleteMatch(bytes3, bytes3.length));
        assertTrue(area.isPrefixMatch(bytes3, bytes3.length));
        assertTrue(area.isPrefixMatch(bytes2, bytes2.length));
        assertFalse(area.isPrefixMatch(new byte[] { 'z', 'z' }, 100));

        // Test truncation.
        byte[] tooLong = new byte[ConfigArea.MAX_LENGTH + 100];
        area.setConfig(tooLong, tooLong.length);
        assertEquals(ConfigArea.MAX_LENGTH, area.getConfig().length);

        // Test out-of-bounds handling.
        byte[] justRight = new byte[10];
        area.setConfig(justRight, 1000);
        assertEquals(justRight.length, area.getConfig().length);
    }
}
