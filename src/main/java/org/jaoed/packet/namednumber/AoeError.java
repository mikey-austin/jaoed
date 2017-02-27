package org.jaoed.packet.namednumber;

import org.pcap4j.packet.namednumber.NamedNumber;

import java.util.HashMap;
import java.util.Map;

public final class AoeError extends NamedNumber<Byte, AoeError> {

    public static final AoeError CMD_UNKNOWN
        = new AoeError((byte) 1, "Command unrecognized");

    public static final AoeError IMPROPER_ARGS_VAL
        = new AoeError((byte) 2, "Improper value in args");

    public static final AoeError CANNOT_ACCEPT_ATA_CMD
        = new AoeError((byte) 3, "Target can no longer accept ATA commands");

    public static final AoeError CANNOT_SET_CONFIG
        = new AoeError((byte) 4, "Config string is already set");

    public static final AoeError VERSION_UNKNOWN
        = new AoeError((byte) 5, "Cannot understand version number");

    public static final AoeError TARGET_RESERVED
        = new AoeError((byte) 6, "Cannot complete command, target reserved");

    private static final Map<Byte, AoeError> registry
        = new HashMap<Byte, AoeError>();

    static {
        registry.put(CMD_UNKNOWN.value(), CMD_UNKNOWN);
        registry.put(IMPROPER_ARGS_VAL.value(), IMPROPER_ARGS_VAL);
        registry.put(CANNOT_ACCEPT_ATA_CMD.value(), CANNOT_ACCEPT_ATA_CMD);
        registry.put(CANNOT_SET_CONFIG.value(), CANNOT_SET_CONFIG);
        registry.put(VERSION_UNKNOWN.value(), VERSION_UNKNOWN);
        registry.put(TARGET_RESERVED.value(), TARGET_RESERVED);
    }

    public AoeError(Byte value, String name) {
        super(value, name);
    }

    public static AoeError getInstance(Byte value) {
        if (registry.containsKey(value)) {
            return registry.get(value);
        } else {
            return new AoeError(value, "unknown");
        }
    }

    @Override
    public int compareTo(AoeError o) {
        return value().compareTo(o.value());
    }
}
