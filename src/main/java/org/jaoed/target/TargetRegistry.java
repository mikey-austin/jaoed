package org.jaoed.target;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.packet.ProcessorRegistry;
import static org.jaoed.target.TargetUtils.*;

public class TargetRegistry implements ProcessorRegistry {
    private final Map<Long, List<PacketProcessor>> targets = new HashMap<>();
    private final List<PacketProcessor> allTargets = new LinkedList<>();

    @Override
    public Optional<List<PacketProcessor>> lookup(RequestContext ctx) {
        AoeFrame.AoeHeader header = ctx.getAoeFrame().getHeader();
        return lookup(header.getMajorNumber(), header.getMinorNumber());
    }

    public Optional<List<PacketProcessor>> lookup(short major, byte minor) {
        if (allTargets.size() == 0) {
            return Optional.empty();
        } else if (decodeMajor(major) == 0xFFFF && decodeMinor(minor) == 0xFF) {
            // Broadcast packet; matches all targets.
            return Optional.of(allTargets);
        } else {
            return Optional.ofNullable(
                targets.get(combineMajorMinor(major, minor)));
        }
    }

    public TargetRegistry addTarget(DeviceTarget target) {
        Device device = target.getDevice();
        targets.put(
            combineMajorMinor(device.getShelf(), device.getSlot()),
            Collections.singletonList(target));
        allTargets.add(target);
        return this;
    }
}
