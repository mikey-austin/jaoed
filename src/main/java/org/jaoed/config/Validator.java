package org.jaoed.config;

import java.util.HashMap;

class Validator implements ConfigVisitor {
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

    public void visitDevice(Device device) {
        try {
            device.validate();
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
            iface.validate();
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitLogger(Logger logger) {
        try {
            logger.validate();
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitAcl(Acl acl) {
        try {
            acl.validate();
        } catch (ValidationException e) {
            exception = e;
        }
    }

    public void visitDeviceAcl(Device.DeviceAcl acls) {
        try {
            acls.validate();
        } catch (ValidationException e) {
            exception = e;
        }
    }
}
