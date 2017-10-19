package org.jaoed.target.commands.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.pcap4j.packet.EthernetPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.QueryConfig;
import org.jaoed.packet.namednumber.QueryConfigCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.ConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class ConfigFactory implements CommandFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class);
    private static final Map<QueryConfigCommand, CommandFactory> factories = new HashMap<>();

    static {
        factories.put(QueryConfigCommand.READ_CONFIG, ConfigFactory::readConfig);
        factories.put(QueryConfigCommand.TEST_FULL_MATCH, ConfigFactory::testFullMatch);
        factories.put(QueryConfigCommand.TEST_PREFIX_MATCH, ConfigFactory::testPrefixMatch);
        factories.put(QueryConfigCommand.SET_IF_EMPTY, ConfigFactory::setIfEmpty);
        factories.put(QueryConfigCommand.SET_FORCE, ConfigFactory::setForce);
    }

    @Override
    public TargetCommand makeCommand(RequestContext ctx) {
        try {
            QueryConfig config = QueryConfig.newPacket(
                ctx.getAoeFrame().getPayload());
            return Optional
                .ofNullable(
                    factories.get(config.getHeader().getSubCommand()))
                .orElseThrow(
                    () -> new IllegalArgumentException("no sub-command found"))
                .makeCommand(ctx);
        } catch (Exception e) {
            LOG.error("could not make query config command", e);
        }

        return null;
    }

    public static TargetCommand readConfig(RequestContext ctx) {
        return (DeviceTarget target) -> {
            LOG.debug("reading config for device {}", target);
            ConfigArea configArea = target.getConfigArea();
            byte[] payload = configArea.isEmpty()
                ? (new byte[] {})
                : configArea.getConfig();
            return new ResponseBuilder(ctx)
                .setPayload(payload);
        };
    }

    public static TargetCommand testFullMatch(RequestContext ctx) {
        return null;
    }

    public static TargetCommand testPrefixMatch(RequestContext ctx) {
        return null;
    }

    public static TargetCommand setIfEmpty(RequestContext ctx) {
        return null;
    }

    public static TargetCommand setForce(RequestContext ctx) {
        return null;
    }
}
