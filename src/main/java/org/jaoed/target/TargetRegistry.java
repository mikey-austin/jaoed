package org.jaoed.target;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;
import static org.jaoed.target.TargetUtils.*;

public class TargetRegistry implements ProcessorRegistry {
    private final Map<Long, DeviceTarget> targets = new HashMap<>();

    @Override
    public Optional<PacketProcessor> lookup(RequestContext ctx) {
        AoeFrame.AoeHeader header = ctx.getAoeFrame().getHeader();
        return lookup(header.getMajorNumber(), header.getMinorNumber());
    }

    public Optional<PacketProcessor> lookup(short major, byte minor) {
        return Optional.ofNullable(
            targets.get(combineMajorMinor(major, minor)));
    }

    public TargetRegistry addTarget(DeviceTarget target) {
        Device device = target.getDevice();
        targets.put(
            combineMajorMinor(device.getShelf(), device.getSlot()), target);
        return this;
    }
}
