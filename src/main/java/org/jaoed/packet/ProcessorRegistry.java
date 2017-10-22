package org.jaoed.packet;

import java.util.Optional;

import org.jaoed.net.RequestContext;

public interface ProcessorRegistry {
    public Optional<PacketProcessor> lookup(RequestContext ctx);
}
