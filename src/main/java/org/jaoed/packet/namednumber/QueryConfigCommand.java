package org.jaoed.packet.namednumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.pcap4j.packet.namednumber.NamedNumber;

public final class QueryConfigCommand extends NamedNumber<Byte, QueryConfigCommand> {

    private static final Map<Byte, QueryConfigCommand> registry
        = new HashMap<Byte, QueryConfigCommand>();

    public static final QueryConfigCommand READ_CONFIG
        = new QueryConfigCommand((byte) 0, "Read target config string");

    public static final QueryConfigCommand TEST_FULL_MATCH
        = new QueryConfigCommand((byte) 1, "Test complete config string match");

    public static final QueryConfigCommand TEST_PREFIX_MATCH
        = new QueryConfigCommand((byte) 2, "Test prefix config string match");

    public static final QueryConfigCommand SET_IF_EMPTY
        = new QueryConfigCommand((byte) 3, "Set config string if not already set");

    public static final QueryConfigCommand SET_FORCE
        = new QueryConfigCommand((byte) 4, "Unconditionally set target config string");

    static {
        registry.put(READ_CONFIG.value(), READ_CONFIG);
        registry.put(TEST_FULL_MATCH.value(), TEST_FULL_MATCH);
        registry.put(TEST_PREFIX_MATCH.value(), TEST_PREFIX_MATCH);
        registry.put(SET_IF_EMPTY.value(), SET_IF_EMPTY);
        registry.put(SET_FORCE.value(), SET_FORCE);
    }

    public QueryConfigCommand(Byte value, String name) {
        super(value, name);
    }

    public static Optional<QueryConfigCommand> getInstance(Byte value) {
        return Optional.ofNullable(registry.get(value));
    }

    @Override
    public int compareTo(QueryConfigCommand o) {
        return value().compareTo(o.value());
    }
}
