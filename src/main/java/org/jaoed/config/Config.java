package org.jaoed.config;

import java.util.List;
import java.util.ArrayList;

import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;
import org.jaoed.config.Acl;

class Config {
    private List<Device> devices;
    private List<Interface> interfaces;
    private List<Logger> loggers;
    private List<Acl> acls;

    public Config() {
        devices = new ArrayList<Device>();
        interfaces = new ArrayList<Interface>();
        loggers = new ArrayList<Logger>();
        acls = new ArrayList<Acl>();
    }

    public void addDevice(Device device) {
        devices.add(device);
    }

    public void addInterface(Interface networkInterface) {
        interfaces.add(networkInterface);
    }

    public void addLogger(Logger logger) {
        loggers.add(logger);
    }

    public void addAcl(Acl acl) {
        acls.add(acl);
    }
}
