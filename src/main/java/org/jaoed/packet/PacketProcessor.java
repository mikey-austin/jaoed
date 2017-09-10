package org.jaoed.packet;

import org.pcap4j.packet.EthernetPacket;

import org.jaoed.packet.AoeFrame;

public interface PacketProcessor {
    public boolean enqueue(EthernetPacket.EthernetHeader header, AoeFrame frame);
}
