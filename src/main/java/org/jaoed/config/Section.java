package org.jaoed.config;

interface Section {
    public void acceptVisitor(ConfigVisitor visitor);
}
