package org.jaoed.config;

import java.util.Stack;

import main.antlr4.org.jaoed.ConfigBaseListener;
import main.antlr4.org.jaoed.ConfigParser;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Config;
import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;

public class ConfigBuilder extends ConfigBaseListener implements ConfigVisitor {
    private Config config;
    private Stack<Section> sections;

    public ConfigBuilder() {
        super();
        config = new Config();
        sections = new Stack<Section>();
    }

    public Config getConfig() {
        return config;
    }

    public void visitDevice(Device device) {}
    public void visitInterface(Interface networkInterface) {}
    public void visitLogger(Logger logger) {}
    public void visitAcl(Acl acl) {}
}
