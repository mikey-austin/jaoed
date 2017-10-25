package org.jaoed.target;

import java.util.Optional;

import org.jaoed.net.RequestContext;

public interface CommandFactory {
    public Optional<TargetCommand> makeCommand(RequestContext ctx);
}
