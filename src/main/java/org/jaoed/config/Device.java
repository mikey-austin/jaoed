package org.jaoed.config;

import java.util.List;

public class Device implements Section {
    private int shelf;
    private int slot;
    private String target;
    private Interface iface;
    private boolean writeCache;
    private boolean broadcast;
    private DeviceAcl acls;
    private Logger logger;
    private Logger.Level logLevel;

    public Device() {
        writeCache = false;
        broadcast = false;
        slot = 0;
        shelf = 0;
    }

    public int getShelf() {
        return shelf;
    }

    public Device setShelf(int shelf) {
        this.shelf = shelf;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Device setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public Device setTarget(String target) {
        this.target = target;
        return this;
    }

    public Interface getInterface() {
        return iface;
    }

    public Device setInterface(Interface iface) {
        this.iface = iface;
        return this;
    }

    public boolean isWriteCacheOn() {
        return writeCache;
    }

    public Device setWriteCache(boolean writeCache) {
        this.writeCache = writeCache;
        return this;
    }

    public boolean isBroadcastOn() {
        return broadcast;
    }

    public Device setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    public DeviceAcl getAcls() {
        return acls;
    }

    public Device setAcls(DeviceAcl acls) {
        this.acls = acls;
        return this;
    }

    public Logger getLogger() {
        return logger;
    }

    public Device setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public Logger.Level getLogLevel() {
        return logLevel;
    }

    public Device setLogLevel(Logger.Level logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitDevice(this);
    }

    public long getSizeInBytes() {
        // TODO: actually do something.
        return 0;
    }

    @Override
    public String toString() {
        String out = "Device<" + target + ">:\n"
            + " -> shelf = " + Integer.toString(shelf) + "\n"
            + " -> slot = " + Integer.toString(slot) + "\n"
            + " -> write-cache = " + Boolean.toString(writeCache) + "\n"
            + " -> broadcast = " + Boolean.toString(broadcast) + "\n"
            + " -> iface = " + iface.getName() + "\n";

        if (logger != null) {
            out += " -> logger = " + logger.getName() + "\n"
                + " -> log-level = " + logLevel + "\n";
        }

        if (acls != null) {
            out += " -> acls = " + acls + "\n";
        }

        return out;
    }

    public static class DeviceAcl implements Section {
        private Acl cfgRead;
        private Acl cfgSet;
        private Acl read;
        private Acl write;

        public DeviceAcl() {}

        public void setCfgRead(Acl acl) {
            this.cfgRead = acl;
        }

        public void setCfgSet(Acl acl) {
            this.cfgSet = acl;
        }

        public void setRead(Acl acl) {
            this.read = acl;
        }

        public void setWrite(Acl acl) {
            this.write = acl;
        }

        public Acl getCfgRead() {
            return cfgRead;
        }

        public Acl getCfgSet() {
            return cfgSet;
        }

        public Acl getRead() {
            return read;
        }

        public Acl getWrite() {
            return write;
        }

        public void acceptVisitor(ConfigVisitor visitor) {
            visitor.visitDeviceAcl(this);
        }

        @Override
        public String toString() {
            String out = "";
            if (cfgRead != null || cfgSet != null || read != null || write != null) {
                out += "Acls [ ";
                if (cfgRead != null)
                    out += "<cfgRead: " + cfgRead.getName() + "> ";
                if (cfgSet != null)
                    out += "<cfgSet: " + cfgSet.getName() + "> ";
                if (read != null)
                    out += "<read: " + read.getName() + "> ";
                if (write != null)
                    out += "<write: " + write.getName() + "> ";
                out += "]";
            }
            return out;
        }
    }
}
