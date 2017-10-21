package org.jaoed.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import static org.pcap4j.util.ByteArrays.*;

import org.jaoed.packet.namednumber.*;

public final class QueryConfig extends AbstractPacket {
    private final QueryConfigHeader header;
    private final Packet payload;

    public static QueryConfig newPacket(Packet payload) throws IllegalRawDataException {
        byte[] rawData = payload.getRawData();
        return newPacket(rawData, 0, rawData.length);
    }

    public static QueryConfig newPacket(byte[] rawData, int offset, int length)
        throws IllegalRawDataException {

        ByteArrays.validateBounds(rawData, offset, length);
        return new QueryConfig(rawData, offset, length);
    }

    private QueryConfig(byte[] rawData, int offset, int length)
        throws IllegalRawDataException {

        this.header = new QueryConfigHeader(rawData, offset, length);

        // We don't use packet factories, so just put the rest as an
        // unknown packet.
        int payloadOffset = offset + header.length();
        int payloadLength = length - header.length();
        if (payloadLength < 0) {
            throw new IllegalRawDataException("invalid payload length");
        } else if (payloadLength > 0) {
            this.payload = new UnknownPacket.Builder()
                .rawData(Arrays.copyOfRange(rawData, payloadOffset, payloadOffset + payloadLength))
                .build();
        } else {
            this.payload = null;
        }
    }

    private QueryConfig(Builder builder) {
        this.header = new QueryConfigHeader(builder);
        this.payload = builder.payloadBuilder != null
            ? builder.payloadBuilder.build()
            : null;
    }

    @Override
    public QueryConfigHeader getHeader() {
        return header;
    }

    @Override
    public Packet getPayload() {
        return payload;
    }

    @Override
    public Builder getBuilder() {
        return new Builder(this);
    }

    public static final class Builder extends AbstractPacket.AbstractBuilder {
        private short bufferCount;
        private short firmwareVersion;
        private byte sectorCount;
        private byte aoeProtocolVersion;
        private QueryConfigCommand subCommand;
        private short configStringLength;
        private AbstractPacket.Builder payloadBuilder;

        public Builder() {}

        private Builder(QueryConfig packet) {
            QueryConfigHeader header = packet.getHeader();
            this.bufferCount = header.bufferCount;
            this.firmwareVersion = header.firmwareVersion;
            this.sectorCount = header.sectorCount;
            this.aoeProtocolVersion = header.aoeProtocolVersion;
            this.configStringLength = header.configStringLength;
            QueryConfigCommand
                .getInstance(header.subCommand.value())
                .ifPresent(command -> this.subCommand = command);
            if (packet.payload != null)
                this.payloadBuilder = packet.payload.getBuilder();
        }

        public Builder bufferCount(short bufferCount) {
            this.bufferCount = bufferCount;
            return this;
        }

        public Builder firmwareVersion(short firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder sectorCount(byte sectorCount) {
            this.sectorCount = sectorCount;
            return this;
        }

        public Builder aoeProtocolVersion(byte aoeProtocolVersion) {
            this.aoeProtocolVersion = aoeProtocolVersion;
            return this;
        }

        public Builder subCommand(byte command) {
            QueryConfigCommand.getInstance(command).ifPresent(this::subCommand);
            return this;
        }

        public Builder subCommand(QueryConfigCommand subCommand) {
            this.subCommand = subCommand;
            return this;
        }

        public Builder configStringLength(short configStringLength) {
            this.configStringLength = configStringLength;
            return this;
        }

        @Override
        public Builder payloadBuilder(AbstractPacket.Builder payloadBuilder) {
            this.payloadBuilder = payloadBuilder;
            return this;
        }

        @Override
        public AbstractPacket.Builder getPayloadBuilder() {
            return this.payloadBuilder;
        }

        @Override
        public QueryConfig build() {
            return new QueryConfig(this);
        }
    }

    public static final class QueryConfigHeader extends AbstractHeader {
        private static final int QUERY_CONFIG_SIZE = 8;

        private static final int BUFFER_COUNT_OFFSET = 0;
        private static final int BUFFER_COUNT_SIZE = 2;

        private static final int FIRMWARE_VERSION_OFFSET = BUFFER_COUNT_OFFSET + BUFFER_COUNT_SIZE;
        private static final int FIRMWARE_VERSION_SIZE = 2;

        private static final int SECTOR_COUNT_OFFSET = FIRMWARE_VERSION_OFFSET + FIRMWARE_VERSION_SIZE;
        private static final int SECTOR_COUNT_SIZE = 1;

        private static final int AOE_CMD_OFFSET = SECTOR_COUNT_OFFSET + SECTOR_COUNT_SIZE;
        private static final int AOE_CMD_SIZE = 1;

        private static final int CONFIG_STR_LEN_OFFSET = AOE_CMD_OFFSET + AOE_CMD_SIZE;
        private static final int CONFIG_STR_LEN_SIZE = 2;

        private short bufferCount = 0;
        private short firmwareVersion = 0;
        private byte sectorCount = 0;
        private byte aoeProtocolVersion = 0;
        private QueryConfigCommand subCommand;
        private short configStringLength = 0;

        private QueryConfigHeader(byte[] rawData, int offset, int length) throws IllegalRawDataException {
            if (length < QUERY_CONFIG_SIZE) {
                StringBuilder sb = new StringBuilder(200);
                sb.append("The data is too short to build a QueryConfig header (")
                    .append(QUERY_CONFIG_SIZE)
                    .append(" bytes). data: ")
                    .append(ByteArrays.toHexString(rawData, " "))
                    .append(", offset: ")
                    .append(offset)
                    .append(", length: ")
                    .append(length);
                throw new IllegalRawDataException(sb.toString());
            }

            this.bufferCount = ByteArrays.getShort(rawData, BUFFER_COUNT_OFFSET + offset);
            this.firmwareVersion = ByteArrays.getShort(rawData, FIRMWARE_VERSION_OFFSET + offset);
            this.sectorCount = ByteArrays.getByte(rawData, SECTOR_COUNT_OFFSET + offset);

            byte aoeCmdByte = ByteArrays.getByte(rawData, AOE_CMD_OFFSET + offset);
            this.aoeProtocolVersion = (byte) ((aoeCmdByte & 0xF0) >>> 4);
            QueryConfigCommand
                .getInstance((byte) (aoeCmdByte & 0x0F))
                .ifPresent(command -> this.subCommand = command);

            this.configStringLength = ByteArrays.getShort(rawData, CONFIG_STR_LEN_OFFSET + offset);
        }

        private QueryConfigHeader(Builder builder) {
            this.bufferCount = builder.bufferCount;
            this.firmwareVersion = builder.firmwareVersion;
            this.sectorCount = builder.sectorCount;
            this.aoeProtocolVersion = builder.aoeProtocolVersion;
            this.subCommand = builder.subCommand;
            this.configStringLength = builder.configStringLength;
        }

        public short getBufferCount() {
            return bufferCount;
        }

        public short getFirmwareVersion() {
            return firmwareVersion;
        }

        public byte getSectorCount() {
            return sectorCount;
        }

        public byte getAoeProtocolVersion() {
            return aoeProtocolVersion;
        }

        public QueryConfigCommand getSubCommand() {
            return subCommand;
        }

        public short getConfigStringLength() {
            return configStringLength;
        }

        @Override
        protected List<byte[]> getRawFields() {
            List<byte[]> rawFields = new ArrayList<byte[]>();

            rawFields.add(ByteArrays.toByteArray(bufferCount));
            rawFields.add(ByteArrays.toByteArray(firmwareVersion));
            rawFields.add(ByteArrays.toByteArray(sectorCount));

            byte aoeCmdByte = (byte) ((aoeProtocolVersion << 4) | subCommand.value());
            rawFields.add(ByteArrays.toByteArray(aoeCmdByte));
            rawFields.add(ByteArrays.toByteArray(configStringLength));

            return rawFields;
        }

        @Override
        public int length() {
            return QUERY_CONFIG_SIZE;
        }

        @Override
        protected String buildString() {
            StringBuilder sb = new StringBuilder();
            String ls = System.getProperty("line.separator");

            sb.append("[AoE/QueryConfig (")
                .append(length())
                .append(" bytes)]")
                .append(ls);
            sb.append("  Buffer count: ")
                .append(bufferCount)
                .append(ls);
            sb.append("  Firmware version: ")
                .append(firmwareVersion)
                .append(ls);
            sb.append("  Sector count: ")
                .append(sectorCount)
                .append(ls);
            sb.append("  AoE protocol version: ")
                .append(aoeProtocolVersion)
                .append(ls);
            sb.append("  Sub-command: ")
                .append(subCommand.name())
                .append(ls);
            sb.append("  Config string length: ")
                .append(configStringLength)
                .append(ls);

            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!this.getClass().isInstance(obj))
                return false;

            QueryConfigHeader other = (QueryConfigHeader) obj;
            return
                bufferCount == other.bufferCount
                && firmwareVersion == other.firmwareVersion
                && sectorCount == other.sectorCount
                && aoeProtocolVersion == other.aoeProtocolVersion
                && configStringLength == other.configStringLength
                && subCommand.equals(other.subCommand);
        }

        @Override
        protected int calcHashCode() {
            int result = 17;
            result = 31 * result + bufferCount;
            result = 31 * result + firmwareVersion;
            result = 31 * result + sectorCount;
            result = 31 * result + aoeProtocolVersion;
            result = 31 * result + subCommand.value();
            result = 31 * result + configStringLength;
            return result;
        }
    }
}
