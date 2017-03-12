package org.jaoed.config;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.lang.Iterable;

public class Config implements Iterable<Section> {
    private List<Device> devices;
    private Map<String, Interface> interfaces;
    private Map<String, Logger> loggers;
    private Map<String, Acl> acls;

    public Config() {
        devices = new ArrayList<Device>();
        interfaces = new HashMap<String, Interface>();
        loggers = new HashMap<String, Logger>();
        acls = new HashMap<String, Acl>();
    }

    public List<Device> getDevices() {
        return devices;
    }

    public Collection<Interface> getInterfaces() {
        return interfaces.values();
    }

    public Collection<Acl> getAcls() {
        return acls.values();
    }

    public Collection<Logger> getLoggers() {
        return loggers.values();
    }

    public void addDevice(Device device) {
        devices.add(device);
    }

    public void addInterface(Interface iface) {
        interfaces.put(iface.getName(), iface);
    }

    public void addLogger(Logger logger) {
        loggers.put(logger.getName(), logger);
    }

    public void addAcl(Acl acl) {
        acls.put(acl.getName(), acl);
    }

    public void validate(Validator validator) throws ValidationException {
        for (Section section : this)
            section.acceptVisitor(validator);
        validator.validate();
    }

    @Override
    public Iterator<Section> iterator() {
        List<Section> sections = new LinkedList<>();

        for (Logger logger : loggers.values())
            sections.add(logger);
        for (Acl acl : acls.values())
            sections.add(acl);
        for (Interface iface : interfaces.values())
            sections.add(iface);
        for (Device device : devices) {
            sections.add(device);
            if (device.getAcls() != null)
                sections.add(device.getAcls());
        }

        return sections.iterator();
    }

    @Override
    public String toString() {
        String out = "Config:\n";
        if (loggers.size() > 0)
            out += loggers + "\n";
        if (acls.size() > 0)
            out += acls + "\n";
        if (interfaces.size() > 0)
            out += interfaces + "\n";
        if (devices.size() > 0)
            out += devices;
        return out;
    }
}
