package org.jaoed.config;

import java.util.Stack;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.io.IOException;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.jaoed.config.Config;
import org.jaoed.config.Validator;
import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;

public class ConfigBuilder extends ConfigBaseListener {
    private Config config;
    private HashMap<String, Logger> loggerTab;
    private HashMap<String, Interface> ifaceTab;
    private HashMap<String, Acl> aclTab;
    private Section currentSection;
    private Validator validator;

    public ConfigBuilder() {
        this(new Validator());
    }

    public ConfigBuilder(Validator validator) {
        super();
        this.config = new Config();
        this.loggerTab = new HashMap<String, Logger>();
        this.ifaceTab = new HashMap<String, Interface>();
        this.aclTab = new HashMap<String, Acl>();
        this.validator = validator;
    }

    public Config getConfig() throws ValidationException {
        config.validate(validator);
        return config;
    }

    public void parseString(String data) throws IOException {
        CharStream stream = (CharStream) new ANTLRInputStream(data);
        parseInputStream(stream);
    }

    public void parseFile(String fileName) throws IOException {
        CharStream stream = (CharStream) new ANTLRFileStream(fileName);
        parseInputStream(stream);
    }

    public void parseInputStream(CharStream stream)
        throws IOException {

        ConfigLexer lexer = new ConfigLexer(stream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        ConfigParser parser = new ConfigParser(tokenStream);
        ConfigBuilder builder = new ConfigBuilder();

        // Start the parser.
        parser.addParseListener(this);
        parser.config();
    }

    @Override
    public void exitLoggerType(ConfigParser.LoggerTypeContext ctx) {
        String type = ctx.getChild(2).getText();
        Logger logger = type.equals("syslog")
            ? new org.jaoed.config.logger.Syslog()
            : new org.jaoed.config.logger.File();

        // Record the above instantiated subclass, which will be fleshened
        // on the call to the exitLoggerSection hook.
        currentSection = logger;
    }

    @Override
    public void exitLoggerSection(ConfigParser.LoggerSectionContext ctx) {
        Logger logger = (Logger) currentSection;
        logger.setName(ctx.getChild(1).getText());

        ConfigParser.LoggerStatementsContext statements = ctx.loggerStatements();
        for (ConfigParser.LoggerAssignmentContext assignment : statements.loggerAssignment()) {
            if (assignment instanceof ConfigParser.LoggerFileContext) {
                String file = unquote(assignment.getChild(2).getText());
                ((org.jaoed.config.logger.File) logger).setFileName(file);
            } else if (assignment instanceof ConfigParser.LoggerSyslogLevelContext) {
                Integer level = new Integer(assignment.getChild(2).getText());
                ((org.jaoed.config.logger.Syslog) logger).setLevel(level);
            } else if (assignment instanceof ConfigParser.LoggerSyslogFacilityContext) {
                Integer facility = new Integer(assignment.getChild(2).getText());
                ((org.jaoed.config.logger.Syslog) logger).setFacility(facility);
            }
        }

        currentSection = null;
        config.addLogger(logger);
        loggerTab.put(logger.getName(), logger);
    }

    @Override
    public void exitAclSection(ConfigParser.AclSectionContext ctx) {
        Acl acl = new Acl();
        acl.setName(ctx.getChild(1).getText());

        ConfigParser.AclStatementsContext statements = ctx.aclStatements();
        for (ConfigParser.AclAssignmentContext assignment : statements.aclAssignment()) {
            if (assignment instanceof ConfigParser.AclPolicyContext) {
                String policy = assignment.getChild(2).getText();
                acl.setPolicy(policy.equals("accept")
                    ? Acl.Policy.ACCEPT
                    : Acl.Policy.REJECT);
            } else if (assignment instanceof ConfigParser.AclAcceptContext) {
                for (String host : getListStrings(assignment.getChild(2))) {
                    acl.addAcceptedHost(host);
                }
            } else if (assignment instanceof ConfigParser.AclRejectContext) {
                for (String host : getListStrings(assignment.getChild(2))) {
                    acl.addRejectedHost(host);
                }
            } else if (assignment instanceof ConfigParser.AclLoggerContext) {
                Logger logger = loggerTab.get(assignment.getChild(2).getText());
                if (logger != null)
                    acl.setLogger(logger);
            } else if (assignment instanceof ConfigParser.AclLogLevelContext) {
                String level = assignment.getChild(2).getText();
                acl.setLogLevel(Logger.makeLevel(level));
            }
        }

        config.addAcl(acl);
        aclTab.put(acl.getName(), acl);
    }

    @Override
    public void exitInterfaceSection(ConfigParser.InterfaceSectionContext ctx) {
        Interface iface = new Interface();
        iface.setName(ctx.getChild(1).getText());

        ConfigParser.InterfaceStatementsContext statements = ctx.interfaceStatements();
        for (ConfigParser.InterfaceAssignmentContext assignment : statements.interfaceAssignment()) {
            if (assignment instanceof ConfigParser.InterfaceMtuContext) {
                iface.setMtu(assignment.getChild(2).getText());
            } else if (assignment instanceof ConfigParser.InterfaceLoggerContext) {
                Logger logger = loggerTab.get(assignment.getChild(2).getText());
                if (logger != null)
                    iface.setLogger(logger);
            } else if (assignment instanceof ConfigParser.InterfaceLogLevelContext) {
                String level = assignment.getChild(2).getText();
                iface.setLogLevel(Logger.makeLevel(level));
            }
        }

        config.addInterface(iface);
        ifaceTab.put(iface.getName(), iface);
    }

    @Override
    public void exitDeviceAclSection(ConfigParser.DeviceAclSectionContext ctx) {
        Device.DeviceAcl acls = new Device.DeviceAcl();
        ConfigParser.DeviceAclStatementsContext statements = ctx.deviceAclStatements();
        for (ConfigParser.DeviceAclAssignmentContext assignment : statements.deviceAclAssignment()) {
            if (assignment instanceof ConfigParser.DeviceAclCfgReadContext) {
                Acl acl = aclTab.get(assignment.getChild(2).getText());
                if (acl != null)
                    acls.setCfgRead(acl);
            } else if (assignment instanceof ConfigParser.DeviceAclCfgSetContext) {
                Acl acl = aclTab.get(assignment.getChild(2).getText());
                if (acl != null)
                    acls.setCfgSet(acl);
            } else if (assignment instanceof ConfigParser.DeviceAclReadContext) {
                Acl acl = aclTab.get(assignment.getChild(2).getText());
                if (acl != null)
                    acls.setRead(acl);
            } else if (assignment instanceof ConfigParser.DeviceAclWriteContext) {
                Acl acl = aclTab.get(assignment.getChild(2).getText());
                if (acl != null)
                    acls.setWrite(acl);
            }
        }

        currentSection = acls;
    }

    @Override
    public void exitDeviceSection(ConfigParser.DeviceSectionContext ctx) {
        Device device = new Device();

        ConfigParser.DeviceStatementsContext statements = ctx.deviceStatements();
        for (ConfigParser.DeviceAssignmentContext assignment : statements.deviceAssignment()) {
            if (assignment instanceof ConfigParser.DeviceTargetContext) {
                device.setTarget(unquote(assignment.getChild(2).getText()));
            } else if (assignment instanceof ConfigParser.DeviceShelfContext) {
                String shelf = assignment.getChild(2).getText();
                device.setShelf(new Integer(shelf));
            } else if (assignment instanceof ConfigParser.DeviceSlotContext) {
                String slot = assignment.getChild(2).getText();
                device.setSlot(new Integer(slot));
            } else if (assignment instanceof ConfigParser.DeviceWriteCacheContext) {
                String writeCache = assignment.getChild(2).getText();
                device.setWriteCache(writeCache.equals("on") ? true : false);
            } else if (assignment instanceof ConfigParser.DeviceBroadcastContext) {
                String broadcast = assignment.getChild(2).getText();
                device.setBroadcast(broadcast.equals("true") ? true : false);
            } else if (assignment instanceof ConfigParser.DeviceInterfaceContext) {
                Interface iface = ifaceTab.get(assignment.getChild(2).getText());
                if (iface != null)
                    device.setInterface(iface);
            } else if (assignment instanceof ConfigParser.DeviceLoggerContext) {
                Logger logger = loggerTab.get(assignment.getChild(2).getText());
                if (logger != null)
                    device.setLogger(logger);
            } else if (assignment instanceof ConfigParser.DeviceLogLevelContext) {
                String level = assignment.getChild(2).getText();
                device.setLogLevel(Logger.makeLevel(level));
            }
        }

        if (currentSection instanceof Device.DeviceAcl) {
            device.setAcls((Device.DeviceAcl) currentSection);
            currentSection = null;
        }

        config.addDevice(device);
    }

    private List<String> getListStrings(ParseTree ctx) {
        ConfigParser.ListContext listCtx = (ConfigParser.ListContext) ctx;
        List<String> strings = new LinkedList<String>();

        for (ConfigParser.ListEntryContext entry : listCtx.listStatements().listEntry()) {
            if (entry instanceof ConfigParser.StrEntryContext) {
                strings.add(
                    unquote(((ConfigParser.StrEntryContext) entry)
                            .STRING().getText()));
            }
        }

        return strings;
    }

    private String unquote(String input) {
        StringBuilder builder = new StringBuilder(input);
        builder.deleteCharAt(builder.indexOf("\""));
        builder.deleteCharAt(builder.lastIndexOf("\""));
        return builder.toString();
    }
}
