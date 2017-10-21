package org.jaoed.net;

import java.util.function.Consumer;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;

import org.jaoed.packet.AoeFrame;

public class RequestContext {
    private final EthernetPacket requestFrame;
    private final AoeFrame aoeFrame;
    private final Consumer<Packet> sender;

    public RequestContext(EthernetPacket requestFrame, Consumer<Packet> sender) throws Exception {
        this.requestFrame = requestFrame;
        this.aoeFrame = AoeFrame.newPacket(requestFrame.getPayload());
        this.sender = sender;
    }

    public AoeFrame getAoeFrame() {
        return aoeFrame;
    }

    public Consumer<Packet> getSender() {
        return sender;
    }

    public EthernetPacket getEthernetFrame() {
        return requestFrame;
    }

    public String getDstAddr() {
        return requestFrame.getHeader().getDstAddr().toString();
    }

    @Override
    public String toString() {
        return requestFrame.getHeader().toString()
            + " "
            + aoeFrame.getHeader().toString();
    }
}
