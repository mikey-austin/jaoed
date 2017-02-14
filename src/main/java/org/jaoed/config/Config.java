package org.jaoed.config;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;

public class Config {
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


    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public List<Acl> getAcls() {
        return acls;
    }

    public List<Logger> getLoggers() {
        return loggers;
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
