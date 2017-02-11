package org.jaoed.config;

import java.util.Stack;

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
        ConfigParser.SectionStatementsContext statements = ctx.sectionStatements();
        for (ConfigParser.AssignmentContext assignment : statements.assignment()) {
            String varName = getVarName(assignment);
            System.out.print("Found var " + varName + " in acl section -> ");

            // We need to account for the value's type.
            ParseTree value = assignment.getChild(2);
            if (value instanceof TerminalNode) {
                System.out.println(((TerminalNode) value).getText());
            } else if (value instanceof ConfigParser.ListContext) {
                System.out.println("list...");
            }
        }

        config.addAcl(acl);
    }

    private String getVarName(ConfigParser.AssignmentContext assignment) {
        if (assignment instanceof ConfigParser.IntValContext) {
            return ((ConfigParser.IntValContext) assignment)
                .assignmentName().getChild(0).getText();
        } else if (assignment instanceof ConfigParser.StrValContext) {
            return ((ConfigParser.StrValContext) assignment)
                .assignmentName().getChild(0).getText();
        } else if (assignment instanceof ConfigParser.BoolValContext) {
            return ((ConfigParser.BoolValContext) assignment)
                .assignmentName().getChild(0).getText();
        } else if (assignment instanceof ConfigParser.ListValContext) {
            return ((ConfigParser.ListValContext) assignment)
                .assignmentName().getChild(0).getText();
        } else {
            return ((ConfigParser.SymValContext) assignment)
                .assignmentName().getChild(0).getText();
        }
    }
}
