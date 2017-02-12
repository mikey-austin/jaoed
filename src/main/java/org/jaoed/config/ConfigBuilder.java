package org.jaoed.config;

import java.util.Stack;
import java.util.List;
import java.util.LinkedList;
import java.lang.StringBuilder;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.tree.*;

import org.jaoed.config.Config;
import org.jaoed.config.Device;
import org.jaoed.config.Interface;
import org.jaoed.config.Logger;
import org.jaoed.config.Logger;

public class ConfigBuilder extends ConfigBaseListener {
    private Config config;

    public ConfigBuilder() {
        super();
        config = new Config();
    }

    public Config getConfig() {
        return config;
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
            }
        }

        config.addAcl(acl);
    }

    private List<String> getListStrings(ParseTree ctx) {
        ConfigParser.ListContext listCtx = (ConfigParser.ListContext) ctx;
        List<String> strings = new LinkedList<String>();

        for (ConfigParser.ListEntryContext entry : listCtx.listStatements().listEntry()) {
            if (entry instanceof ConfigParser.StrEntryContext) {
                strings.add(
                    unquote(((ConfigParser.StrEntryContext) entry).STRING().getText()));
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
