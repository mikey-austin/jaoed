package org.jaoed.target;

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
    public void setConfig(byte[] bytes) {
        this.config = Arrays.copyOf(bytes, bytes.length);
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
    public boolean isCompleteMatch(byte[] bytes) {
        if (bytes.length == 0 && isEmpty()) {
            return true;
        } else if (isEmpty() || config.length != bytes.length) {
            return false;
        }
        return Arrays.equals(config, bytes);
    }

    @Override
    public boolean isPrefixMatch(byte[] prefix) {
        if (isCompleteMatch(prefix)) {
            return true;
        } if (isEmpty() || config.length < prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if ((config[i] & 0xFF) != (prefix[i] & 0xFF)) {
                return false;
            }
        }

        return true;
    }
}
