package org.jaoed.target.commands.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.pcap4j.packet.EthernetPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.QueryConfig;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.ConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class ConfigFactory implements CommandFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class);

    private final Map<QueryConfigCommand, BiFunction<RequestContext, QueryConfig, TargetCommand>> dispatch;
    private final BiFunction<RequestContext, DeviceTarget, ResponseBuilder> responseBuilderFactory;

    public ConfigFactory() {
        this(ResponseBuilder::new);
    }

    public ConfigFactory(BiFunction<RequestContext, DeviceTarget, ResponseBuilder> responseBuilderFactory) {
        this.responseBuilderFactory = responseBuilderFactory;
        this.dispatch = new HashMap<>();
        this.dispatch.put(QueryConfigCommand.READ_CONFIG, this::readConfig);
        this.dispatch.put(QueryConfigCommand.TEST_FULL_MATCH, this::testFullMatch);
        this.dispatch.put(QueryConfigCommand.TEST_PREFIX_MATCH, this::testPrefixMatch);
        this.dispatch.put(QueryConfigCommand.SET_IF_EMPTY, this::setIfEmpty);
        this.dispatch.put(QueryConfigCommand.SET_FORCE, this::setForce);
    }

    @Override
    public TargetCommand makeCommand(RequestContext ctx) {
        try {
            QueryConfig query = QueryConfig.newPacket(
                ctx.getAoeFrame().getPayload());
            return Optional
                .ofNullable(
                    dispatch.get(query.getHeader().getSubCommand()))
                .orElseThrow(
                    () -> new IllegalArgumentException("no sub-command found"))
                .apply(ctx, query);
        } catch (Exception e) {
            LOG.error("could not make query config command", e);
        }

        return target -> responseBuilderFactory
            .apply(ctx, target)
            .setError(AoeError.CMD_UNKNOWN);
    }

    public TargetCommand readConfig(RequestContext ctx, QueryConfig query) {
        return target -> {
            LOG.debug("reading config for device {}", target);
            ConfigArea configArea = target.getConfigArea();
            ResponseBuilder response = responseBuilderFactory.apply(ctx, target);
            if (!configArea.isEmpty()) {
                response.setPayload(configArea.getConfig());
            }
            return response;
        };
    }

    public TargetCommand testFullMatch(RequestContext ctx, QueryConfig query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] queryString = query.getPayload().getRawData();
            if (configArea.isCompleteMatch(queryString)) {
                LOG.debug("full config string match for device {}", target);
                ResponseBuilder response = responseBuilderFactory.apply(ctx, target);
                if (!configArea.isEmpty()) {
                    response.setPayload(configArea.getConfig());
                }
                return response;
            }

            LOG.debug("full config string *mismatch* for device {}", target);
            return null;
        };
    }

    public TargetCommand testPrefixMatch(RequestContext ctx, QueryConfig query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] queryString = query.getPayload().getRawData();
            if (configArea.isPrefixMatch(queryString)) {
                LOG.debug("prefix match for device {}", target);
                ResponseBuilder response = responseBuilderFactory.apply(ctx, target);
                if (!configArea.isEmpty()) {
                    response.setPayload(configArea.getConfig());
                }
                return response;
            }

            LOG.debug("prefix *mismatch* for device {}", target);
            return null;
        };
    }

    public TargetCommand setIfEmpty(RequestContext ctx, QueryConfig query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            if (configArea.isEmpty()) {
                byte[] toSet = query.getPayload().getRawData();
                configArea.setConfig(toSet);
                LOG.debug("setting config string for device {}", target);
                return responseBuilderFactory.apply(ctx, target)
                    .setPayload(toSet);
            } else {
                LOG.debug("refusing to overwrite config string for device {}", target);
                return responseBuilderFactory.apply(ctx, target)
                    .setError(AoeError.CANNOT_SET_CONFIG);
            }
        };
    }

    public TargetCommand setForce(RequestContext ctx, QueryConfig query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] toSet = query.getPayload().getRawData();
            configArea.setConfig(toSet);
            LOG.debug("force setting config string for device {}", target);
            return responseBuilderFactory.apply(ctx, target)
                .setPayload(toSet);
        };
    }
}
