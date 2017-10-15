package org.jaoed.target;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigArea {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigArea.class);

    private byte[] config;

    public ConfigArea() {
        this.config = null;
    }

    public void setConfig(byte[] bytes) {
        this.config = Arrays.copyOf(bytes, bytes.length);
    }

    public boolean isEmpty() {
        return config == null;
    }

    public boolean isCompleteMatch(byte[] bytes) {
        if (isEmpty() || config.length != bytes.length) {
            return false;
        }
        return Arrays.equals(config, bytes);
    }

    public boolean isPrefixMatch(byte[] prefix) {
        if (isEmpty() || config.length < prefix.length) {
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
