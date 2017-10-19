package org.jaoed.target;

import org.pcap4j.packet.EthernetPacket;

public interface TargetResponse {
    public EthernetPacket makeResponse();
}
