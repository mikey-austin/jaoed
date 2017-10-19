package org.jaoed.target.commands.config;

import org.pcap4j.packet.EthernetPacket;

import org.jaoed.target.CommandFactory;
import org.jaoed.target.TargetCommand;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;

public class ConfigFactory implements CommandFactory {
    @Override
    public TargetCommand makeCommand(EthernetPacket.EthernetHeader header, AoeFrame frame) {
        return null;
    }
}
