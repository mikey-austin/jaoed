package org.jaoed.packet.namednumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.pcap4j.packet.namednumber.NamedNumber;

public final class QueryConfigSubCommand extends NamedNumber<Byte, QueryConfigSubCommand> {

    private static final Map<Byte, QueryConfigSubCommand> registry
        = new HashMap<Byte, QueryConfigSubCommand>();

    public static final QueryConfigSubCommand READ_CONFIG
        = new QueryConfigSubCommand((byte) 0, "Read target config string");

    public static final QueryConfigSubCommand TEST_FULL_MATCH
        = new QueryConfigSubCommand((byte) 1, "Test complete config string match");

    public static final QueryConfigSubCommand TEST_PREFIX_MATCH
        = new QueryConfigSubCommand((byte) 2, "Test prefix config string match");

    public static final QueryConfigSubCommand SET_IF_EMPTY
        = new QueryConfigSubCommand((byte) 3, "Set config string if not already set");

    public static final QueryConfigSubCommand SET_FORCE
        = new QueryConfigSubCommand((byte) 4, "Unconditionally set target config string");

    static {
        registry.put(READ_CONFIG.value(), READ_CONFIG);
        registry.put(TEST_FULL_MATCH.value(), TEST_FULL_MATCH);
        registry.put(TEST_PREFIX_MATCH.value(), TEST_PREFIX_MATCH);
        registry.put(SET_IF_EMPTY.value(), SET_IF_EMPTY);
        registry.put(SET_FORCE.value(), SET_FORCE);
    }

    public QueryConfigSubCommand(Byte value, String name) {
        super(value, name);
    }

    public static Optional<QueryConfigSubCommand> getInstance(Byte value) {
        return Optional.ofNullable(registry.get(value));
    }

    @Override
    public int compareTo(QueryConfigSubCommand o) {
        return value().compareTo(o.value());
    }
}
