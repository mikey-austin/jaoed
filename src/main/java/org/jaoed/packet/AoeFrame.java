package org.jaoed.packet;

import static org.jaoed.target.TargetUtils.*;
import static org.pcap4j.util.ByteArrays.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jaoed.packet.namednumber.*;
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.util.ByteArrays;

public class AoeFrame extends AbstractPacket {
    private final AoeHeader header;
    private final Packet payload;

    public static AoeFrame newPacket(Packet payload) throws IllegalRawDataException {
        byte[] rawData = payload.getRawData();
        return newPacket(rawData, 0, rawData.length);
    }

    public static AoeFrame newPacket(byte[] rawData, int offset, int length)
            throws IllegalRawDataException {

        ByteArrays.validateBounds(rawData, offset, length);
        return new AoeFrame(rawData, offset, length);
    }

    private AoeFrame(byte[] rawData, int offset, int length) throws IllegalRawDataException {

        this.header = new AoeHeader(rawData, offset, length);

        // We don't use packet factories, so just put the rest as an
        // unknown packet.
        int payloadOffset = offset + header.length();
        int payloadLength = length - header.length();
        if (payloadLength < 0) {
            throw new IllegalRawDataException("invalid payload length");
        } else if (payloadLength > 0) {
            this.payload =
                    new UnknownPacket.Builder()
                            .rawData(
                                    Arrays.copyOfRange(
                                            rawData, payloadOffset, payloadOffset + payloadLength))
                            .build();
        } else {
            this.payload = null;
        }
    }

    private AoeFrame(Builder builder) {
        this.header = new AoeHeader(builder);
        this.payload = builder.payloadBuilder != null ? builder.payloadBuilder.build() : null;
    }

    @Override
    public AoeHeader getHeader() {
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
        private byte version;
        private boolean responseFlag;
        private boolean responseErrorFlag;
        private boolean reserved1Flag;
        private boolean reserved2Flag;
        private AoeError error;
        private short majorNumber;
        private byte minorNumber;
        private AoeCommand command;
        private byte[] tag;
        private AbstractPacket.Builder payloadBuilder;

        public Builder() {
            tag = new byte[] {0x0, 0x0, 0x0, 0x0};
        }

        private Builder(AoeFrame packet) {
            this.version = packet.header.version;
            this.responseFlag = packet.header.responseFlag;
            this.responseErrorFlag = packet.header.responseErrorFlag;
            this.reserved1Flag = packet.header.reserved1Flag;
            this.reserved2Flag = packet.header.reserved2Flag;
            this.error = packet.header.error;
            this.majorNumber = packet.header.majorNumber;
            this.minorNumber = packet.header.minorNumber;
            this.command = packet.header.command;
            if (packet.header.tag != null && packet.header.tag.length == 4) {
                this.tag = packet.header.tag;
            } else {
                this.tag = new byte[] {0x0, 0x0, 0x0, 0x0};
            }
            if (packet.payload != null) this.payloadBuilder = packet.payload.getBuilder();
        }

        public Builder version(byte version) {
            this.version = version;
            return this;
        }

        public Builder responseFlag(boolean responseFlag) {
            this.responseFlag = responseFlag;
            return this;
        }

        public Builder responseErrorFlag(boolean responseErrorFlag) {
            this.responseErrorFlag = responseErrorFlag;
            return this;
        }

        public Builder reserved1Flag(boolean reserved1Flag) {
            this.reserved1Flag = reserved1Flag;
            return this;
        }

        public Builder reserved2Flag(boolean reserved2Flag) {
            this.reserved2Flag = reserved2Flag;
            return this;
        }

        public Builder error(byte error) {
            return error(AoeError.getInstance(error));
        }

        public Builder error(AoeError error) {
            this.error = error;
            return this;
        }

        public Builder majorNumber(int majorNumber) {
            return majorNumber(encodeMajor(majorNumber));
        }

        public Builder majorNumber(short majorNumber) {
            this.majorNumber = majorNumber;
            return this;
        }

        public Builder minorNumber(int minorNumber) {
            return minorNumber(encodeMinor(minorNumber));
        }

        public Builder minorNumber(byte minorNumber) {
            this.minorNumber = minorNumber;
            return this;
        }

        public Builder command(byte command) {
            AoeCommand.getInstance(command).ifPresent(this::command);
            return this;
        }

        public Builder command(AoeCommand command) {
            this.command = command;
            return this;
        }

        public Builder tag(byte[] tag) {
            this.tag = tag;
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
        public AoeFrame build() {
            return new AoeFrame(this);
        }
    }

    public static final class AoeHeader extends AbstractHeader {
        private static final int AOE_HEADER_SIZE = 10;

        private static final int VERSION_FLAGS_OFFSET = 0;
        private static final int VERSION_FLAGS_SIZE = 1;

        private static final int ERROR_OFFSET = VERSION_FLAGS_OFFSET + VERSION_FLAGS_SIZE;
        private static final int ERROR_SIZE = 1;

        private static final int MAJOR_NUMBER_OFFSET = ERROR_OFFSET + ERROR_SIZE;
        private static final int MAJOR_NUMBER_SIZE = 2;

        private static final int MINOR_NUMBER_OFFSET = MAJOR_NUMBER_OFFSET + MAJOR_NUMBER_SIZE;
        private static final int MINOR_NUMBER_SIZE = 1;

        private static final int COMMAND_OFFSET = MINOR_NUMBER_OFFSET + MINOR_NUMBER_SIZE;
        private static final int COMMAND_SIZE = 1;

        private static final int TAG_OFFSET = COMMAND_OFFSET + COMMAND_SIZE;
        private static final int TAG_SIZE = 4;

        private byte version;
        private boolean responseFlag;
        private boolean responseErrorFlag;
        private boolean reserved1Flag;
        private boolean reserved2Flag;
        private AoeError error;
        private short majorNumber;
        private byte minorNumber;
        private AoeCommand command;
        private byte[] tag;

        private AoeHeader(byte[] rawData, int offset, int length) throws IllegalRawDataException {
            if (length < AOE_HEADER_SIZE) {
                StringBuilder sb = new StringBuilder(200);
                sb.append("The data is too short to build an AoE header(")
                        .append(AOE_HEADER_SIZE)
                        .append(" bytes). data: ")
                        .append(ByteArrays.toHexString(rawData, " "))
                        .append(", offset: ")
                        .append(offset)
                        .append(", length: ")
                        .append(length);
                throw new IllegalRawDataException(sb.toString());
            }

            byte versionFlags = ByteArrays.getByte(rawData, VERSION_FLAGS_OFFSET + offset);
            this.version = (byte) (versionFlags >>> 4);
            this.responseFlag = ((versionFlags & 0x08) == 0x08 ? true : false);
            this.responseErrorFlag = ((versionFlags & 0x04) == 0x04 ? true : false);
            this.reserved1Flag = ((versionFlags & 0x02) == 0x02 ? true : false);
            this.reserved2Flag = ((versionFlags & 0x01) == 0x01 ? true : false);

            this.error = AoeError.getInstance(ByteArrays.getByte(rawData, ERROR_OFFSET + offset));
            this.majorNumber = ByteArrays.getShort(rawData, MAJOR_NUMBER_OFFSET + offset);
            this.minorNumber = ByteArrays.getByte(rawData, MINOR_NUMBER_OFFSET + offset);
            AoeCommand.getInstance(ByteArrays.getByte(rawData, COMMAND_OFFSET + offset))
                    .ifPresent(namedCommand -> this.command = namedCommand);
            this.tag = ByteArrays.getSubArray(rawData, TAG_OFFSET + offset, TAG_SIZE);
        }

        private AoeHeader(Builder builder) {
            this.version = builder.version;
            this.responseFlag = builder.responseFlag;
            this.responseErrorFlag = builder.responseErrorFlag;
            this.reserved1Flag = builder.reserved1Flag;
            this.reserved2Flag = builder.reserved2Flag;
            this.error = builder.error;
            this.majorNumber = builder.majorNumber;
            this.minorNumber = builder.minorNumber;
            this.command = builder.command;
            this.tag = builder.tag;
        }

        public byte getVersion() {
            return version;
        }

        public boolean getResponseFlag() {
            return responseFlag;
        }

        public boolean getResponseErrorFlag() {
            return responseErrorFlag;
        }

        public boolean getReserved1Flag() {
            return reserved1Flag;
        }

        public boolean getReserved2Flag() {
            return reserved2Flag;
        }

        public AoeError getError() {
            return error;
        }

        public short getMajorNumber() {
            return majorNumber;
        }

        public byte getMinorNumber() {
            return minorNumber;
        }

        public AoeCommand getCommand() {
            return command;
        }

        public byte[] getTag() {
            return tag;
        }

        @Override
        protected List<byte[]> getRawFields() {
            List<byte[]> rawFields = new ArrayList<byte[]>();

            byte versionFlags = (byte) (version << 4);
            versionFlags |= (responseFlag ? 0x08 : 0x00);
            versionFlags |= (responseErrorFlag ? 0x04 : 0x00);
            versionFlags |= (reserved1Flag ? 0x02 : 0x00);
            versionFlags |= (reserved2Flag ? 0x01 : 0x00);
            rawFields.add(ByteArrays.toByteArray(versionFlags));

            rawFields.add(error != null ? ByteArrays.toByteArray(error.value()) : new byte[] {0x0});
            rawFields.add(ByteArrays.toByteArray(majorNumber));
            rawFields.add(ByteArrays.toByteArray(minorNumber));
            rawFields.add(ByteArrays.toByteArray(command.value()));
            rawFields.add(tag);

            return rawFields;
        }

        @Override
        public int length() {
            return AOE_HEADER_SIZE;
        }

        @Override
        protected String buildString() {
            StringBuilder sb = new StringBuilder();
            String ls = System.getProperty("line.separator");

            sb.append("[AoE Header (").append(length()).append(" bytes)]").append(ls);
            sb.append("  Version: ").append(version).append(ls);
            sb.append("  Response: ").append(responseFlag ? "true" : "false").append(ls);
            sb.append("  Response error: ").append(responseErrorFlag ? "true" : "false").append(ls);
            sb.append("  Error: ").append(error.value()).append(ls);
            sb.append("  Major number: ").append(majorNumber).append(ls);
            sb.append("  Minor number: ").append(minorNumber).append(ls);
            sb.append("  Command: ").append(command.value()).append(ls);
            sb.append("  Tag: 0x").append(ByteArrays.toHexString(tag, "")).append(ls);

            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!this.getClass().isInstance(obj)) return false;

            AoeHeader other = (AoeHeader) obj;
            return version == other.version
                    && responseFlag == other.responseFlag
                    && responseErrorFlag == other.responseErrorFlag
                    && reserved1Flag == other.reserved1Flag
                    && reserved2Flag == other.reserved2Flag
                    && error.equals(other)
                    && majorNumber == other.majorNumber
                    && minorNumber == other.minorNumber
                    && command.equals(other)
                    && Arrays.equals(tag, other.tag);
        }

        @Override
        protected int calcHashCode() {
            int result = 17;
            result = 31 * result + version;
            result = 31 * result + (responseFlag ? 1 : 0);
            result = 31 * result + (responseErrorFlag ? 1 : 0);
            result = 31 * result + error.hashCode();
            result = 31 * result + majorNumber;
            result = 31 * result + minorNumber;
            result = 31 * result + command.hashCode();
            result = 31 * result + tag.hashCode();
            return result;
        }
    }
}
