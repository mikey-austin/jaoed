package org.jaoed.target;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jaoed.target.TargetUtils.*;

public class TargetUtilsTest {
    @Test
    public void testUtils() throws Exception {
        // Major numbers.
        assertTrue(validMajor(0));
        assertTrue(validMajor(200));
        assertTrue(validMajor(65500));
        assertFalse(validMajor(66000));
        assertFalse(validMajor(-1));

        assertEquals((short) 32767, encodeMajor(32767));
        assertEquals((short) -32768, encodeMajor(32768));
        assertEquals(32768, decodeMajor((short) -32768));
        assertEquals(65500, decodeMajor(encodeMajor(65500)));

        assertTrue(validMajor(encodeMajor(0)));
        assertTrue(validMajor(encodeMajor(200)));
        assertTrue(validMajor(encodeMajor(65500)));

        // Minor numbers.
        assertTrue(validMinor(0));
        assertTrue(validMinor(100));
        assertTrue(validMinor(250));
        assertFalse(validMinor(1000));
        assertFalse(validMinor(-1));

        assertEquals((byte) 127, encodeMinor(127));
        assertEquals((byte) -128, encodeMinor(128));
        assertEquals(128, decodeMinor((byte) -128));
        assertEquals(254, decodeMinor(encodeMinor(254)));

        assertTrue(validMinor(encodeMinor(0)));
        assertTrue(validMinor(encodeMinor(128)));
        assertTrue(validMinor(encodeMinor(254)));

        // Combined encodings.
        short major = encodeMajor(65000);
        byte minor = encodeMinor(254);
        long combined = combineMajorMinor(major, minor);
        assertEquals(major, encodeMajor(extractMajor(combined)));
        assertEquals(minor, encodeMinor(extractMinor(combined)));
    }
}
