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
import org.jaoed.packet.QueryConfigPayload;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigSubCommand;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.ConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class QueryConfigCommand implements CommandFactory {
    private static final Logger LOG = LoggerFactory.getLogger(QueryConfigCommand.class);

    private final Map<QueryConfigSubCommand, BiFunction<RequestContext, QueryConfigPayload, TargetCommand>> dispatch;
    private final BiFunction<RequestContext, DeviceTarget, QueryConfigResponse> queryConfigResponseFactory;

    public QueryConfigCommand() {
        this(QueryConfigResponse::new);
    }

    public QueryConfigCommand(BiFunction<RequestContext, DeviceTarget, QueryConfigResponse> queryConfigResponseFactory) {
        this.queryConfigResponseFactory = queryConfigResponseFactory;
        this.dispatch = new HashMap<>();
        this.dispatch.put(QueryConfigSubCommand.READ_CONFIG, this::readConfig);
        this.dispatch.put(QueryConfigSubCommand.TEST_FULL_MATCH, this::testFullMatch);
        this.dispatch.put(QueryConfigSubCommand.TEST_PREFIX_MATCH, this::testPrefixMatch);
        this.dispatch.put(QueryConfigSubCommand.SET_IF_EMPTY, this::setIfEmpty);
        this.dispatch.put(QueryConfigSubCommand.SET_FORCE, this::setForce);
    }

    @Override
    public TargetCommand makeCommand(RequestContext ctx) {
        try {
            QueryConfigPayload query = QueryConfigPayload.newPacket(
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

        return target -> queryConfigResponseFactory
            .apply(ctx, target)
            .setError(AoeError.CMD_UNKNOWN);
    }

    public TargetCommand readConfig(RequestContext ctx, QueryConfigPayload query) {
        return target -> {
            LOG.debug("reading config for device {}", target);
            ConfigArea configArea = target.getConfigArea();
            QueryConfigResponse response = queryConfigResponseFactory.apply(ctx, target);
            if (!configArea.isEmpty()) {
                response.setPayload(configArea.getConfig());
            }
            return response;
        };
    }

    public TargetCommand testFullMatch(RequestContext ctx, QueryConfigPayload query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] queryString = query.getPayload().getRawData();
            if (configArea.isCompleteMatch(queryString)) {
                LOG.debug("full config string match for device {}", target);
                QueryConfigResponse response = queryConfigResponseFactory.apply(ctx, target);
                if (!configArea.isEmpty()) {
                    response.setPayload(configArea.getConfig());
                }
                return response;
            }

            LOG.debug("full config string *mismatch* for device {}", target);
            return null;
        };
    }

    public TargetCommand testPrefixMatch(RequestContext ctx, QueryConfigPayload query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] queryString = query.getPayload().getRawData();
            if (configArea.isPrefixMatch(queryString)) {
                LOG.debug("prefix match for device {}", target);
                QueryConfigResponse response = queryConfigResponseFactory.apply(ctx, target);
                if (!configArea.isEmpty()) {
                    response.setPayload(configArea.getConfig());
                }
                return response;
            }

            LOG.debug("prefix *mismatch* for device {}", target);
            return null;
        };
    }

    public TargetCommand setIfEmpty(RequestContext ctx, QueryConfigPayload query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            if (configArea.isEmpty()) {
                byte[] toSet = query.getPayload().getRawData();
                configArea.setConfig(toSet);
                LOG.debug("setting config string for device {}", target);
                return queryConfigResponseFactory.apply(ctx, target)
                    .setPayload(toSet);
            } else {
                LOG.debug("refusing to overwrite config string for device {}", target);
                return queryConfigResponseFactory.apply(ctx, target)
                    .setError(AoeError.CANNOT_SET_CONFIG);
            }
        };
    }

    public TargetCommand setForce(RequestContext ctx, QueryConfigPayload query) {
        return target -> {
            ConfigArea configArea = target.getConfigArea();
            byte[] toSet = query.getPayload().getRawData();
            configArea.setConfig(toSet);
            LOG.debug("force setting config string for device {}", target);
            return queryConfigResponseFactory.apply(ctx, target)
                .setPayload(toSet);
        };
    }
}