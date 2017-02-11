package org.jaoed.config;

import main.antlr4.org.jaoed.ConfigBaseListener;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Config;
import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;

class ConfigBuilder extends ConfigBaseListener implements ConfigVisitor {
    private Config config;
    private Section currentSection;

    public ConfigBuilder() {
        super();
        config = new Config();
    }

    public void visitDevice(Device device) {}
    public void visitInterface(Interface networkInterface) {}
    public void visitLogger(Logger logger) {}
    public void visitAcl(Acl acl) {}


}
