package org.jaoed.target.commands.config;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.util.MacAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.net.AoeVersion;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.QueryConfig;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.packet.namednumber.QueryConfigCommand;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class ResponseBuilder implements TargetResponse {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseBuilder.class);

    private final RequestContext ctx;
    private final DeviceTarget target;
    private byte[] payload;
    private AoeError error;

    protected ResponseBuilder(RequestContext ctx, DeviceTarget target) {
        this.ctx = ctx;
        this.target = target;
        this.payload = new byte[] {};
        this.error = null;
    }

    protected ResponseBuilder setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    protected ResponseBuilder setError(AoeError error) {
        this.error = error;
        return this;
    }

    @Override
    public EthernetPacket makeResponse() {
        try {
            QueryConfig.Builder config = new QueryConfig.Builder()
                .firmwareVersion(target.getFirmwareVersion())
                .sectorCount(target.getSectorCount())
                .bufferCount(target.getBufferCount())
                .aoeProtocolVersion(AoeVersion.SUPPORTED);
            if (payload != null) {
                config
                    .configStringLength((short) payload.length)
                    .payloadBuilder(
                        new UnknownPacket.Builder().rawData(payload));
            }

            if (ctx.getAoeFrame().getPayload() != null) {
                QueryConfig requestConfig = QueryConfig.newPacket(
                    ctx.getAoeFrame().getPayload());
                config.subCommand(
                    requestConfig.getHeader().getSubCommand());
            }

            AoeFrame.Builder aoe = ctx
                .getAoeFrame()
                .getBuilder()
                .responseFlag(true)
                .payloadBuilder(config);
            if (error != null) {
                aoe.error(error);
            }

            EthernetPacket.Builder eth = ctx
                .getEthernetFrame()
                .getBuilder()
                .payloadBuilder(aoe);

            // Reverse ethernet request addresses.
            eth.dstAddr(
                ctx.getEthernetFrame().getHeader().getSrcAddr());
            eth.srcAddr(
                ctx.getEthernetFrame().getHeader().getDstAddr());

            return eth.build();
        } catch (Exception e) {
            LOG.error("could not build response frame", e);
            return null;
        }
    }
}
