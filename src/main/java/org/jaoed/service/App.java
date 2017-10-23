package org.jaoed.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private final AppReloader reloader;
    private final List<Service> services;

    protected App(AppBuilder builder) {
        this.reloader = builder.getReloader();
        this.services = builder.getServices();
    }

    @Override
    public void start() {
        services.forEach(Service::start);
    }

    @Override
    public void stop() {
        services.forEach(Service::stop);
    }

    public void reload() {
        reloader.reload();
    }
}
