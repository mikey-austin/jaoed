package org.jaoed.config;

import org.jaoed.config.ConfigVisitor;

interface Section {
    public void acceptVisitor(ConfigVisitor visitor);
    public void validate() throws ValidationException;
}
