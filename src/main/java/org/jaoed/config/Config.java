package org.jaoed.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;

public class Config {
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

    public static Config parseString(String data) throws IOException {
        CharStream stream = (CharStream) new ANTLRInputStream(data);
        return parseInputStream(stream);
    }

    public static Config parseFile(String fileName) throws IOException {
        CharStream stream = (CharStream) new ANTLRFileStream(fileName);
        return parseInputStream(stream);
    }

    public static Config parseInputStream(CharStream stream) throws IOException {
        ConfigLexer lexer = new ConfigLexer(stream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        ConfigParser parser = new ConfigParser(tokenStream);
        ConfigBuilder builder = new ConfigBuilder();

        // Start the parser.
        parser.addParseListener(builder);
        parser.config();

        // Collect the built configuration.
        return builder.getConfig();
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
            out += devices + "\n";
        return out;
    }
}
