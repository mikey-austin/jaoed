package org.jaoed.target.commands.ata;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.AtaPayload;
import org.jaoed.packet.namednumber.AoeError;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetResponse;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtaResponse implements TargetResponse {
    private static final Logger LOG = LoggerFactory.getLogger(AtaResponse.class);

    private final RequestContext ctx;
    private final DeviceTarget target;
    private byte[] payload;
    private AoeError error;

    protected AtaResponse(RequestContext ctx, DeviceTarget target) {
        this.ctx = ctx;
        this.target = target;
        this.payload = new byte[] {};
        this.error = null;
    }

    protected AtaResponse setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    protected AtaResponse setError(AoeError error) {
        this.error = error;
        return this;
    }

    @Override
    public void sendResponse() {
        // Build and send off the response.
        Packet response = makeResponse();
        LOG.trace("sending ATA response payload: {}", response);
        ctx.getSender().accept(response);
    }

    public Packet makeResponse() {
        try {
            AtaPayload request = AtaPayload.newPacket(ctx.getAoeFrame().getPayload());
            AtaPayload.Builder ata = request.getBuilder();

            // TODO: Set various error byte and payload fields in ata builder.

            AoeFrame.Builder aoe =
                    ctx.getAoeFrame()
                            .getBuilder()
                            .responseFlag(true)
                            .majorNumber(target.getDevice().getShelf())
                            .minorNumber(target.getDevice().getSlot())
                            .payloadBuilder(ata);
            if (error != null) {
                aoe.responseErrorFlag(true);
                aoe.error(error);
            }

            EthernetPacket.Builder eth = ctx.getEthernetFrame().getBuilder().payloadBuilder(aoe);

            // Reverse ethernet request addresses.
            eth.dstAddr(ctx.getEthernetFrame().getHeader().getSrcAddr());

            // Always stamp the incoming interface's hardware address.
            eth.srcAddr(ctx.getIfaceAddr());

            return eth.build();
        } catch (Exception e) {
            LOG.error("could not build response frame", e);
            return null;
        }
    }
}
