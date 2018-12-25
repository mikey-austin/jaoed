package org.jaoed.target;

import static java.lang.Math.min;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceConfigArea implements ConfigArea {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceConfigArea.class);

    private byte[] config;

    public DeviceConfigArea() {
        this.config = null;
    }

    @Override
    public void setConfig(byte[] bytes, int length) {
        int lengthToCopy = min(min(length, ConfigArea.MAX_LENGTH), bytes.length);
        if (lengthToCopy < length) {
            LOG.warn("truncating config string to {} bytes", lengthToCopy);
        }
        this.config = Arrays.copyOf(bytes, lengthToCopy);
    }

    @Override
    public byte[] getConfig() {
        return config;
    }

    @Override
    public boolean isEmpty() {
        return config == null;
    }

    @Override
    public boolean isCompleteMatch(byte[] bytes, int length) {
        byte[] toMatch = Arrays.copyOf(bytes, min(bytes.length, length));
        if (toMatch.length == 0 && isEmpty()) {
            return true;
        } else if (isEmpty() || config.length != toMatch.length) {
            return false;
        }
        return Arrays.equals(config, toMatch);
    }

    @Override
    public boolean isPrefixMatch(byte[] prefix, int length) {
        int prefixLength = min(prefix.length, length);
        if (isCompleteMatch(prefix, length)) {
            return true;
        }
        if (isEmpty() || config.length < prefixLength) {
            return false;
        }

        byte[] toMatch = Arrays.copyOf(prefix, prefixLength);
        for (int i = 0; i < toMatch.length; i++) {
            if ((config[i] & 0xFF) != (toMatch[i] & 0xFF)) {
                return false;
            }
        }

        return true;
    }
}
