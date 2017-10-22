package org.jaoed.target;

import static java.lang.Math.round;
import static java.lang.Math.pow;

public class TargetUtils {
    public static final int MAX_MAJOR_NUMBER = ((int) round(pow(2, 16))) - 1;
    public static final int MAX_MINOR_NUMBER = ((int) round(pow(2, 8))) - 1;

    public static boolean validMajor(short major) {
        return validMajor(decodeMajor(major));
    }

    public static boolean validMajor(int major) {
        return major >= 0 && major <= MAX_MAJOR_NUMBER;
    }

    public static boolean validMinor(byte minor) {
        return validMinor(decodeMinor(minor));
    }

    public static boolean validMinor(int minor) {
        return minor >= 0 && minor <= MAX_MINOR_NUMBER;
    }

    public static int decodeMajor(short major) {
        int result = 0xFFFF;
        return result & major;
    }

    public static int decodeMinor(byte minor) {
        int result = 0xFF;
        return result & minor;
    }

    public static short encodeMajor(int major) {
        return (short) (0xFFFF & major);
    }

    public static byte encodeMinor(int minor) {
        return (byte) (0xFF & minor);
    }

    public static long combineMajorMinor(short major, byte minor) {
        return combineMajorMinor(
            decodeMajor(major), decodeMinor(minor));
    }

    public static long combineMajorMinor(int major, int minor) {
        long result = minor;
        result |= (((long) major) << Integer.SIZE);
        return result;
    }

    public static int extractMajor(long combined) {
        return (int) (combined >>> Integer.SIZE);
    }

    public static int extractMinor(long combined) {
        return (int) (0xFFFFFFFF & combined);
    }
}
