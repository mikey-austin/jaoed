package org.jaoed.config;

import java.util.List;
import java.util.LinkedList;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Logger;

public class Acl implements Section {
    private String name;
    private Policy policy;
    private Logger logger;
    private List<String> acceptedHosts;
    private List<String> rejectedHosts;
    private Logger.Level logLevel;

    public Acl() {
        acceptedHosts = new LinkedList<String>();
        rejectedHosts = new LinkedList<String>();
        logLevel = Logger.Level.INFO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void addAcceptedHost(String host) {
        acceptedHosts.add(host);
    }

    public List<String> getAcceptedHosts() {
        return acceptedHosts;
    }

    public void addRejectedHost(String host) {
        rejectedHosts.add(host);
    }

    public List<String> getRejectedHosts() {
        return rejectedHosts;
    }

    public Logger.Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Logger.Level logLevel) {
        this.logLevel = logLevel;
    }

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitAcl(this);
    }

    public enum Policy {
        ACCEPT,
        REJECT
    }

    @Override
    public String toString() {
        String out = "Acl<" + name + ">:\n"
            + " -> policy = " + policy + "\n"
            + " -> accept = " + acceptedHosts + "\n"
            + " -> reject = " + rejectedHosts + "\n";
        if (logger != null) {
            out += " -> logger = " + logger.getName() + "\n"
                + " -> log-level = " + logLevel + "\n";
        }

        return out;
    }
}
