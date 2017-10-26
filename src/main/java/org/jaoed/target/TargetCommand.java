package org.jaoed.target;

import java.util.Optional;

public interface TargetCommand {
    public Optional<TargetResponse> execute(DeviceTarget target);
}
