package org.jaoed.net;

import org.pcap4j.packet.EthernetPacket;

import org.jaoed.packet.AoeFrame;

public class RequestContext {
    private final EthernetPacket requestFrame;
    private final AoeFrame aoeFrame;

    public RequestContext(EthernetPacket requestFrame) throws Exception {
        this.requestFrame = requestFrame;
        this.aoeFrame = AoeFrame.newPacket(requestFrame.getPayload());
    }

    public AoeFrame getAoeFrame() {
        return aoeFrame;
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
