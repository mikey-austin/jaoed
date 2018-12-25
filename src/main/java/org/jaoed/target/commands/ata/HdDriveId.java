package org.jaoed.target.commands.ata;

public class HdDriveId {
    private static final long MAX_ATA_LBA = 0x0FFFFFFFL;
    private static final int TOTAL_LEN = 512;
    private static final int SERIAL_LEN = 20;
    private static final int MODEL_LEN = 40;

    private final String serial;
    private final String model;
    private final int cylinders;
    private final int heads = 255;
    private final int sectors = 64;
    private final int lbaCapacity;
    private final long lbaCapacity2;
    private final byte capability = 0x2; // Supports LBA.
    private final short commandSet2 = (1 << 10); // Supports LBA48.
    private final short cfsEnable2 = (1 << 10); // We use LBA48.

    public HdDriveId(String serial, long sizeInBytes) {
        this("123456789", serial, sizeInBytes);
    }

    public HdDriveId(String model, String serial, long sizeInBytes) {
        this.model = model;
        this.serial = serial;
        this.cylinders = (int) (sizeInBytes >>> 8) >>> 6;
        this.lbaCapacity = (int) (sizeInBytes > MAX_ATA_LBA ? MAX_ATA_LBA : sizeInBytes);
        this.lbaCapacity2 = sizeInBytes;
    }

    /*
     * Serialize contents into the 512 byte format as specified
     * by the ATA spec.
     */
    public byte[] toByteArray() {
        return new byte[] {};
    }
}
