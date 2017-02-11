package org.jaoed.config;

import java.util.List;
import java.util.ArrayList;

import org.jaoed.config.ConfigVisitor;
import org.jaoed.config.Section;
import org.jaoed.config.Logger;

class Acl implements Section {
    private String name;
    private Policy policy;
    private Logger logger;
    private List<String> acceptedHosts;
    private List<String> rejectedHosts;

    public Acl(int initialSize) {
        acceptedHosts = new ArrayList<String>(initialSize);
        rejectedHosts = new ArrayList<String>(initialSize);
    }

    public Acl() {
        acceptedHosts = new ArrayList<String>();
        rejectedHosts = new ArrayList<String>();
    }

    public void acceptVisitor(ConfigVisitor visitor) {
        visitor.visitAcl(this);
    }

    public enum Policy {
        ACCEPT,
        REJECT
    }
}
