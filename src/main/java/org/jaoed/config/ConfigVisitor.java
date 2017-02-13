package org.jaoed.config;

interface ConfigVisitor {
    public void visitDevice(Device device);
    public void visitInterface(Interface networkInterface);
    public void visitLogger(Logger logger);
    public void visitAcl(Acl acl);
    public void visitDeviceAcl(Device.DeviceAcl acls);
}
