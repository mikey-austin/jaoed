package org.jaoed.target;

import org.jaoed.net.RequestContext;

public interface CommandFactory {
    public TargetCommand makeCommand(RequestContext ctx);
}
