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
    private Interface networkInterface;
    private boolean writeCache;
    private boolean broadcast;
    private DeviceAcl acl;
    private Logger.Level logLevel;

    public Device() {
        acl = new DeviceAcl();
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

    public Interface getNetworkInterface() {
        return networkInterface;
    }

    public void setInterface(Interface networkInterface) {
        this.networkInterface = networkInterface;
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
        return acl;
    }

    public void setAcls(Acl cfgRead, Acl cfgSet, Acl read, Acl write) {
        this.acl.setCfgRead(cfgRead);
        this.acl.setCfgSet(cfgSet);
        this.acl.setRead(read);
        this.acl.setWrite(write);
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

    public class DeviceAcl {
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
    }
}
