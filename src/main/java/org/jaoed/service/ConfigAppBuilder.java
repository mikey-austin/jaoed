package org.jaoed.service;

import java.util.LinkedList;
import java.util.List;

import org.jaoed.config.*;
import org.jaoed.net.*;
import org.jaoed.packet.*;
import org.jaoed.packet.namednumber.*;
import org.jaoed.target.*;
import org.jaoed.target.commands.config.*;

public class ConfigAppBuilder implements AppBuilder {
    private final String configPath;
    private final List<Service> services;

    public ConfigAppBuilder(String configPath) throws Exception {
        this.configPath = configPath;
        this.services = new LinkedList<>();
    }

    @Override
    public AppReloader getReloader() {
        return () -> {
            // TODO: re-fill target registry here
        };
    }

    @Override
    public List<Service> getServices() {
        return services;
    }

    @Override
    public App build() throws Exception {
        Config config = new ConfigBuilder()
            .parseFile(configPath)
            .build();

        // TODO: make configurable in global section.
        int outputQueueSize = 1000;
        int poolSize = 5;
        int senderPollMs = 1000;
        PooledSender sender = new PooledSender(
            outputQueueSize, senderPollMs, poolSize);
        services.add(sender);

        TargetRegistry targetRegistry = new TargetRegistry();
        config.getInterfaces().stream()
            .map(iface -> new InterfaceListener(iface, targetRegistry))
            .forEach(services::add);

        // TODO: add more commands here.
        CommandFactory commandDispatcher = CommandDispatcher
            .newBuilder()
            .addCommandFactory(AoeCommand.QUERY_CONFIG, new QueryConfigCommand())
            .build();

        // TODO: make configurable per device section.
        int targetPollMs = 1000;
        int inputQueueSize = 1000;
        for (Device device : config.getDevices()) {
            DeviceTarget target = DeviceTarget
                .newBuilder()
                .setDeviceConfig(device)
                .setResponseProcessor(sender)
                .setPollInterval(targetPollMs)
                .setInputQueueSize(inputQueueSize)
                .setCommandFactory(commandDispatcher)
                .setConfigArea(new DeviceConfigArea())
                .build();
            services.add(target);
        }

        return new App(this);
    }
}
