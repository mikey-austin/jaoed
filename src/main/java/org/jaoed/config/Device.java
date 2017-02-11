package org.jaoed.config;

import java.util.List;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Acl;
import org.jaoed.config.Interface;

public class Device implements Section {
    private int shelf;
    private int slot;
    private String target;
    private Interface networkInterface;
    private boolean writeCache;
    private boolean broadcast;
    private DeviceAcl acl;

    public Device() {
        acl = new DeviceAcl();
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
