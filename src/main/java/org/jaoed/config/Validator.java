package org.jaoed.config;

import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;

public class Validator implements ConfigVisitor {
    private HashMap<String, Device> targets;
    private HashMap<Long, Device> targetShelfSlots;
    private ValidationException exception;

    public Validator() {
        targets = new HashMap<String, Device>();
        targetShelfSlots = new HashMap<Long, Device>();
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

        Path targetPath = FileSystems.getDefault().getPath(target);
        if (!targetPath.isAbsolute())
            throw new ValidationException("Device target path must be absolute");

        if (!Files.isReadable(targetPath))
            throw new ValidationException("Device target path not readable");

        if (!Files.isWritable(targetPath))
            throw new ValidationException("Device target path not writable");
    }

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
        if (targetShelfSlots.containsKey(shelfSlot)) {
            exception = new ValidationException(
                "Shelf " + Integer.toString(shelf) + " and slot "
                + Integer.toString(slot) + " for target "
                + device.getTarget() + " has already been specified");
        } else {
            targetShelfSlots.put(shelfSlot, device);
        }
    }

    public void visitInterface(Interface iface) {
        try {
            if (iface.getName() == null)
                throw new ValidationException("Interface name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitLogger(Logger logger) {
        try {
            if (logger.getName() == null)
                throw new ValidationException("Logger name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitAcl(Acl acl) {
        try {
            if (acl.getName() == null)
                throw new ValidationException("Acl name required");
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitDeviceAcl(Device.DeviceAcl acls) {}
}
