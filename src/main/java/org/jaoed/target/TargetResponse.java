package org.jaoed.target;

import org.pcap4j.packet.Packet;

public interface TargetResponse {
    public Packet getResponse();
    public void sendResponse();
}
