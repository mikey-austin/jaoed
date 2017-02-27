package org.jaoed.packet.namednumber;

import org.pcap4j.packet.namednumber.NamedNumber;

import java.util.HashMap;
import java.util.Map;

public final class AoeCommand extends NamedNumber<Byte, AoeCommand> {

    public static final AoeCommand ISSUE_ATA
        = new AoeCommand((byte) 0, "Issue ata command");

    public static final AoeCommand QUERY_CONFIG
        = new AoeCommand((byte) 1, "Query/config command");

    public static final AoeCommand MAC_MASK_LIST
        = new AoeCommand((byte) 2, "MAC mask list command");

    public static final AoeCommand RESERVE_RELEASE
        = new AoeCommand((byte) 3, "Reserve/release command");

    private static final Map<Byte, AoeCommand> registry
        = new HashMap<Byte, AoeCommand>();

    static {
        registry.put(ISSUE_ATA.value(), ISSUE_ATA);
        registry.put(QUERY_CONFIG.value(), QUERY_CONFIG);
        registry.put(MAC_MASK_LIST.value(), MAC_MASK_LIST);
        registry.put(RESERVE_RELEASE.value(), RESERVE_RELEASE);
    }

    public AoeCommand(Byte value, String name) {
        super(value, name);
    }

    public static AoeCommand getInstance(Byte value) {
        if (registry.containsKey(value)) {
            return registry.get(value);
        } else {
            return new AoeCommand(value, "unknown");
        }
    }

    @Override
    public int compareTo(AoeCommand o) {
        return value().compareTo(o.value());
    }
}
