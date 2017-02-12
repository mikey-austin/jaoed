package org.jaoed.config;

import java.util.Stack;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.lang.StringBuilder;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.tree.*;

import org.jaoed.config.Config;
import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;

public class ConfigBuilder extends ConfigBaseListener {
    private Config config;
    private HashMap<String, Logger> loggerTab;
    private HashMap<String, Interface> ifaceTab;
    private HashMap<String, Acl> aclTab;
    private Section currentSection;

    public ConfigBuilder() {
        super();
        config = new Config();
        loggerTab = new HashMap<String, Logger>();
        ifaceTab = new HashMap<String, Interface>();
        aclTab = new HashMap<String, Acl>();
    }

    public Config getConfig() {
        return config;
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
        ConfigParser.LoggerStatementsContext statements = ctx.loggerStatements();
        for (ConfigParser.LoggerAssignmentContext assignment : statements.loggerAssignment()) {
            if (assignment instanceof ConfigParser.LoggerNameContext) {
                logger.setName(unquote(assignment.getChild(2).getText()));
            } else if (assignment instanceof ConfigParser.LoggerFileContext) {
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
        ConfigParser.AclStatementsContext statements = ctx.aclStatements();
        for (ConfigParser.AclAssignmentContext assignment : statements.aclAssignment()) {
            if (assignment instanceof ConfigParser.AclNameContext) {
                acl.setName(unquote(assignment.getChild(2).getText()));
            } else if (assignment instanceof ConfigParser.AclPolicyContext) {
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
            }
        }

        config.addAcl(acl);
        aclTab.put(acl.getName(), acl);
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
