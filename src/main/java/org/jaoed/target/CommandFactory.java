package org.jaoed.target;

import org.pcap4j.packet.EthernetPacket;

import org.jaoed.packet.AoeFrame;

public interface CommandFactory {
    public TargetCommand makeCommand(EthernetPacket.EthernetHeader header, AoeFrame frame);
}
