package org.jaoed.packet;

import org.jaoed.net.RequestContext;

public interface PacketProcessor {
    public boolean enqueue(RequestContext ctx);
}
