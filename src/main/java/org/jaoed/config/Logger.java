package org.jaoed.config;

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

    public enum Level {
        INFO,
        DEBUG,
        TRACE
    }
}
