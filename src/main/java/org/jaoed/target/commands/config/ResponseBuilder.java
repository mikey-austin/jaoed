package org.jaoed.target.commands.config;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.UnknownPacket;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.QueryConfig;
import org.jaoed.packet.namednumber.QueryConfigCommand;
import org.jaoed.target.ConfigArea;
import org.jaoed.target.DeviceTarget;
import org.jaoed.target.TargetCommand;
import org.jaoed.target.TargetResponse;

public class ResponseBuilder implements TargetResponse {
    private final RequestContext ctx;
    private byte[] payload;

    protected ResponseBuilder(RequestContext ctx) {
        this.ctx = ctx;
        this.payload = new byte[] {};
    }

    protected ResponseBuilder setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public EthernetPacket makeResponse() {
        return null;
    }
}
