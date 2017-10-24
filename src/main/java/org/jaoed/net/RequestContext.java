package org.jaoed.net;

import java.util.function.Consumer;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.MacAddress;

import org.jaoed.packet.AoeFrame;

public class RequestContext {
    private final EthernetPacket requestFrame;
    private final AoeFrame aoeFrame;
    private final Consumer<Packet> sender;
    private final MacAddress ifaceAddr;

    public RequestContext(EthernetPacket requestFrame, Consumer<Packet> sender, MacAddress ifaceAddr) throws Exception {
        this.requestFrame = requestFrame;
        this.aoeFrame = AoeFrame.newPacket(requestFrame.getPayload());
        this.sender = sender;
        this.ifaceAddr = ifaceAddr;
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

    public MacAddress getIfaceAddr() {
        return ifaceAddr;
    }

    @Override
    public String toString() {
        return requestFrame.getHeader().toString()
            + " "
            + aoeFrame.getHeader().toString();
    }
}
