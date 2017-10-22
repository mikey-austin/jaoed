package org.jaoed.config;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.jaoed.target.TargetUtils.*;

public class Validator implements ConfigVisitor {
    private Map<String, Device> targets;
    private Set<Long> targetShelfSlots;
    private ValidationException exception;

    public Validator() {
        targets = new HashMap<>();
        targetShelfSlots = new HashSet<>();
    }

    public void validate() throws ValidationException {
        // Throw the last saved exception.
        if (exception != null)
            throw exception;
    }

    public void validateDevice(Device device) throws ValidationException {
        String target = device.getTarget();

        if (target == null)
            throw new ValidationException("Device target required");

        if (device.getSlot() < 0 || device.getShelf() < 0)
            throw new ValidationException("Device slot/shelf must be >= 0");

        if (!validMajor(device.getShelf()))
            throw new ValidationException("Device slot/shelf must fit in 2 bytes (unsigned)");

        if (!validMinor(device.getSlot()))
            throw new ValidationException("Device slot/shelf must fit in a byte (unsigned)");

        Path targetPath = FileSystems.getDefault().getPath(target);
        if (!targetPath.isAbsolute())
            throw new ValidationException("Device target path must be absolute");

        if (!Files.isReadable(targetPath))
            throw new ValidationException("Device target path not readable");

        if (!Files.isWritable(targetPath))
            throw new ValidationException("Device target path not writable");
    }

    @Override
    public void visitDevice(Device device) {
        try {
            validateDevice(device);
        } catch (ValidationException e) {
            exception = e;
        }

        // Every target needs to be unique.
        if (targets.containsKey(device.getTarget())) {
            exception = new ValidationException(
                "Target " + device.getTarget()
                + " has already been specified");
        } else {
            targets.put(device.getTarget(), device);
        }

        // Make sure every slot/shelf specification is unique.
        int shelf = device.getShelf();
        int slot = device.getSlot();
        long shelfSlot = shelf << 32 | slot;
        if (targetShelfSlots.contains(shelfSlot)) {
            exception = new ValidationException(
                "Shelf " + Integer.toString(shelf) + " and slot "
                + Integer.toString(slot) + " for target "
                + device.getTarget() + " has already been specified");
        } else {
            targetShelfSlots.add(shelfSlot);
        }
    }

    @Override
    public void visitInterface(Interface iface) {
        try {
            if (iface.getName() == null)
                throw new ValidationException("Interface name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    @Override
    public void visitLogger(Logger logger) {
        try {
            if (logger.getName() == null)
                throw new ValidationException("Logger name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    @Override
    public void visitAcl(Acl acl) {
        try {
            if (acl.getName() == null)
                throw new ValidationException("Acl name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    @Override
    public void visitDeviceAcl(Device.DeviceAcl acls) {}
}
