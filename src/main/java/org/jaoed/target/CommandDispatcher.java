package org.jaoed.target;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import org.pcap4j.packet.EthernetPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.namednumber.AoeCommand;

public class CommandDispatcher implements CommandFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CommandDispatcher.class);

    private final Map<AoeCommand, CommandFactory> commandFactories;

    private CommandDispatcher(Builder builder) {
        this.commandFactories = new HashMap<>();
        this.commandFactories.putAll(builder.commandFactories);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Optional<TargetCommand> makeCommand(RequestContext ctx) {
        AoeCommand command = ctx.getAoeFrame().getHeader().getCommand();
        if (!commandFactories.containsKey(command)) {
            LOG.warn("unknown command type ({}) requested", command);
            return Optional.empty();
        }

        return commandFactories
            .get(command)
            .makeCommand(ctx);
    }

    public static class Builder {
        private final Map<AoeCommand, CommandFactory> commandFactories = new HashMap<>();

        public Builder addCommandFactory(AoeCommand command, CommandFactory factory) {
            commandFactories.put(command, factory);
            return this;
        }

        public CommandDispatcher build() {
            return new CommandDispatcher(this);
        }
    }
}
