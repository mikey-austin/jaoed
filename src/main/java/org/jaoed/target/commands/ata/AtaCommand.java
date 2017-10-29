package org.jaoed.target.commands.ata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.pcap4j.packet.EthernetPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.AtaPayload;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.target.CommandFactory;
import org.jaoed.target.ConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class AtaCommand implements CommandFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AtaCommand.class);

    private static final int WIN_READ      = 0x20; // 28-Bit
    private static final int WIN_READ_EXT  = 0x24; // 48-Bit
    private static final int WIN_WRITE     = 0x30; // 28-Bit
    private static final int WIN_WRITE_EXT = 0x34; // 48-Bit
    private static final int WIN_IDENTIFY  = 0xEC;

    private final BiFunction<RequestContext, DeviceTarget, AtaResponse> ataResponseFactory;

    public AtaCommand() {
        this(AtaResponse::new);
    }

    public AtaCommand(BiFunction<RequestContext, DeviceTarget, AtaResponse> ataResponseFactory) {
        this.ataResponseFactory = ataResponseFactory;
    }

    @Override
    public Optional<TargetCommand> makeCommand(RequestContext ctx) {
        try {
            TargetCommand command = null;
            AtaPayload query = AtaPayload.newPacket(
                ctx.getAoeFrame().getPayload());

            // Dispatch on the ATA command status byte.
            switch (query.getHeader().getCmdStatus()) {
            case WIN_READ:
            case WIN_READ_EXT:
                command = read(ctx, query);
                break;

            case WIN_WRITE:
            case WIN_WRITE_EXT:
                command = write(ctx, query);
                break;

            case WIN_IDENTIFY:
                command = identify(ctx, query);
                break;

            default:
                throw new Exception(
                    "could not understand ATA cmd/status code 0x"
                    + Integer.toHexString(query.getHeader().getCmdStatus()));
            }

            return Optional.ofNullable(command);
        } catch (Exception e) {
            LOG.error("could not make ATA command", e);
        }

        return Optional.of(
            target -> error(target, ctx, AoeError.CMD_UNKNOWN));
    }

    public TargetCommand read(RequestContext ctx, AtaPayload query) {
        return target -> {
            LOG.debug("ATA read requested for device {}", target);
            AtaResponse response = ataResponseFactory.apply(ctx, target);

            // Perform actual read and populate response.

            return Optional.of(response);
        };
    }

    public TargetCommand write(RequestContext ctx, AtaPayload query) {
        return target -> {
            LOG.debug("ATA write{} requested for device {}",
                      query.getHeader().isAsync() ? "[async]" : "", target);
            AtaResponse response = ataResponseFactory.apply(ctx, target);

            // TODO: Perform actual write and populate response.

            return Optional.of(response);
        };
    }

    public TargetCommand identify(RequestContext ctx, AtaPayload query) {
        return target -> {
            LOG.debug("ATA identify requested for device {}", target);
            AtaResponse response = ataResponseFactory.apply(ctx, target);

            // TODO: identify and populate response.

            return Optional.of(response);
        };
    }

    private Optional<TargetResponse> error(DeviceTarget target, RequestContext ctx, AoeError errorCode) {
        LOG.warn("ATA error for device {}", target);
        return Optional.empty();
    }
}
