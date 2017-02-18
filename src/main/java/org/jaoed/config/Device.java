package org.jaoed.config;

import java.util.List;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Acl;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;

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

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Interface getInterface() {
        return iface;
    }

    public void setInterface(Interface iface) {
        this.iface = iface;
    }

    public boolean isWriteCacheOn() {
        return writeCache;
    }

    public void setWriteCache(boolean writeCache) {
        this.writeCache = writeCache;
    }

    public boolean isBroadcastOn() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public DeviceAcl getAcls() {
        return acls;
    }

    public void setAcls(DeviceAcl acls) {
        this.acls = acls;
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

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitDevice(this);
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
