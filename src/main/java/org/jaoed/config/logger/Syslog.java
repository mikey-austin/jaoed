package org.jaoed.config.logger;

import org.jaoed.config.Logger;

public class Syslog extends Logger {
    private int level;
    private int facility;

    public Syslog() {}

    public void setLevel(int level) {
        this.level = level;
    }

    public void setFacility(int facility) {
        this.facility = facility;
    }

    public int getLevel() {
        return level;
    }

    public int getFacility() {
        return facility;
    }

    @Override
    public String toString() {
        String out = "Logger<Syslog>[" + super.getName() + "]:\n"
            + " -> level = " + level + "\n"
            + " -> facility = " + facility + "\n";
        return out;
    }
}
