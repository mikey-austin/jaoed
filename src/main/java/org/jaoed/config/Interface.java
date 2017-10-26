package org.jaoed.config;

public class Interface implements Section {
    private String name;
    private String hwAddr;
    private Logger logger;
    private Logger.Level logLevel;

    public Interface() {
        this.logLevel = Logger.Level.INFO;
    }

    @Override
    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitInterface(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHwAddr() {
        return hwAddr;
    }

    public void setHwAddr(String hwAddr) {
        this.hwAddr = hwAddr;
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
        String out = "Interface<" + name + ">:\n";
        if (hwAddr != null) {
            out += " -> hwaddr = " + hwAddr + "\n";
        }
        if (logger != null) {
            out += " -> logger = " + logger.getName() + "\n"
                + " -> log-level = " + logLevel + "\n";
        }
        return out;
    }
}
