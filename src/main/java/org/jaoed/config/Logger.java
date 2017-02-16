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

    public static Level makeLevel(String level) {
        if (level.equals("debug")) {
            return Level.DEBUG;
        } else if (level.equals("trace")) {
            return Level.TRACE;
        }

        return Level.INFO;
    }

    public void validate() throws ValidationException {
        if (name == null)
            throw new ValidationException("Logger name required");
    }

    public enum Level {
        INFO,
        DEBUG,
        TRACE
    }
}
