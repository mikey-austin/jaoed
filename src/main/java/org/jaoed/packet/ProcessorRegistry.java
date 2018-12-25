package org.jaoed.packet;

import java.util.List;
import java.util.Optional;
import org.jaoed.net.RequestContext;

public interface ProcessorRegistry {
    public Optional<List<PacketProcessor>> lookup(RequestContext ctx);
}
