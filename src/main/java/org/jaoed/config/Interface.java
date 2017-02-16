package org.jaoed.config;

import java.util.List;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Logger;

public class Interface implements Section {
    private String name;
    private Mtu mtu;
    private Logger logger;
    private Logger.Level logLevel;

    public Interface() {
        logLevel = Logger.Level.INFO;
    }

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitInterface(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mtu getMtu() {
        return mtu;
    }

    public void setMtu(Mtu mtu) {
        this.mtu = mtu;
    }

    public void setMtu(String mtu) {
        this.mtu = new Mtu(mtu);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger.Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Logger.Level logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        String out = "Interface<" + name + ">:\n"
            + " -> mtu = " + mtu + "\n";
        if (logger != null) {
            out += " -> logger = " + logger.getName() + "\n"
                + " -> log-level = " + logLevel + "\n";
        }

        return out;
    }

    public void validate() throws ValidationException {
        if (name == null)
            throw new ValidationException("Interface name required");
    }

    public class Mtu {
        private int mtu;
        private boolean auto;

        public Mtu() {
            this.mtu = 0;
            this.auto = true;
        }

        public Mtu(String mtu) {
            if (mtu.equals("auto")) {
                this.mtu = 0;
                this.auto = true;
            } else {
                this.mtu = new Integer(mtu);
                this.auto = false;
            }
        }

        public Mtu(int mtu) {
            this.mtu = mtu;
            this.auto = false;
        }

        public boolean isAuto() {
            return this.auto;
        }

        public int getMtu() {
            return this.mtu;
        }

        @Override
        public String toString() {
            String out = this.auto ? "auto" : Integer.toString(mtu);
            return out;
        }
    }
}
