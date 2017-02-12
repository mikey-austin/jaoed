package org.jaoed.config;

import org.jaoed.config.Section;
import org.jaoed.config.ConfigVisitor;

public class Logger implements Section {
    private String name;

    public Logger() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitLogger(this);
    }

    public enum Level {
        INFO,
        DEBUG,
        TRACE
    }
}
