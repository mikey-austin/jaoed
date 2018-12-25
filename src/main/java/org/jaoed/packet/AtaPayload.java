package org.jaoed.packet;

import static org.pcap4j.util.ByteArrays.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.util.ByteArrays;

public final class AtaPayload extends AbstractPacket {
    private static final int LBA_FIELDS = 6;

    private final Header header;
    private final Packet payload;

    public static AtaPayload newPacket(Packet payload) throws IllegalRawDataException {
        byte[] rawData = payload.getRawData();
        return newPacket(rawData, 0, rawData.length);
    }

    public static AtaPayload newPacket(byte[] rawData, int offset, int length)
            throws IllegalRawDataException {

        ByteArrays.validateBounds(rawData, offset, length);
        return new AtaPayload(rawData, offset, length);
    }

    private AtaPayload(byte[] rawData, int offset, int length) throws IllegalRawDataException {

        this.header = new Header(rawData, offset, length);

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

    private AtaPayload(Builder builder) {
        this.header = new Header(builder);
        this.payload = builder.payloadBuilder != null ? builder.payloadBuilder.build() : null;
    }

    @Override
    public Header getHeader() {
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
        private AbstractPacket.Builder payloadBuilder;
        private boolean isAsync;
        private boolean isWrite;
        private int errFeature;
        private int sectorCount;
        private int cmdStatus;
        private int[] lba;

        public Builder() {}

        private Builder(AtaPayload packet) {
            Header header = packet.getHeader();
            this.isAsync = header.isAsync;
            this.isWrite = header.isWrite;
            this.errFeature = header.errFeature;
            this.sectorCount = header.sectorCount;
            this.cmdStatus = header.cmdStatus;
            this.lba = Arrays.copyOf(header.lba, LBA_FIELDS);
            if (packet.payload != null) this.payloadBuilder = packet.payload.getBuilder();
        }

        public Builder isAsync(boolean isAsync) {
            this.isAsync = isAsync;
            return this;
        }

        public Builder isWrite(boolean isWrite) {
            this.isWrite = isWrite;
            return this;
        }

        public Builder errFeature(int errFeature) {
            this.errFeature = errFeature;
            return this;
        }

        public Builder sectorCount(int sectorCount) {
            this.sectorCount = sectorCount;
            return this;
        }

        public Builder cmdStatus(int cmdStatus) {
            this.cmdStatus = cmdStatus;
            return this;
        }

        public Builder lba(int[] lba) {
            this.lba = Arrays.copyOf(lba, LBA_FIELDS);
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
        public AtaPayload build() {
            return new AtaPayload(this);
        }
    }

    public static final class Header extends AbstractHeader {
        private static final int ATA_SIZE = 12;

        private static final int AFLAGS_OFFSET = 0;
        private static final int AFLAGS_SIZE = 1;

        private static final int ERR_OFFSET = AFLAGS_OFFSET + AFLAGS_SIZE;
        private static final int ERR_SIZE = 1;

        private static final int SECTOR_OFFSET = ERR_OFFSET + ERR_SIZE;
        private static final int SECTOR_SIZE = 1;

        private static final int CMD_OFFSET = SECTOR_OFFSET + SECTOR_SIZE;
        private static final int CMD_SIZE = 1;

        private static final int LBA_OFFSET = CMD_OFFSET + CMD_SIZE;

        private boolean isAsync;
        private boolean isWrite;
        private int errFeature;
        private int sectorCount;
        private int cmdStatus;
        private int[] lba;

        private Header(byte[] rawData, int offset, int length) throws IllegalRawDataException {
            if (length < ATA_SIZE) {
                StringBuilder sb = new StringBuilder(200);
                sb.append("The data is too short to build a AtaPayload header (")
                        .append(ATA_SIZE)
                        .append(" bytes). data: ")
                        .append(ByteArrays.toHexString(rawData, " "))
                        .append(", offset: ")
                        .append(offset)
                        .append(", length: ")
                        .append(length);
                throw new IllegalRawDataException(sb.toString());
            }

            byte aflags = ByteArrays.getByte(rawData, AFLAGS_OFFSET + offset);
            this.isWrite = ((aflags & 0x01) == 0x01);
            this.isAsync = ((aflags & 0x02) == 0x02);

            this.errFeature = ((int) 0xFF) & ByteArrays.getByte(rawData, ERR_OFFSET + offset);
            this.sectorCount = ((int) 0xFF) & ByteArrays.getByte(rawData, SECTOR_OFFSET + offset);
            this.cmdStatus = ((int) 0xFF) & ByteArrays.getByte(rawData, CMD_OFFSET + offset);
            this.lba = new int[LBA_FIELDS];
            for (int i = 0; i < LBA_FIELDS; i++) {
                this.lba[i] = ((int) 0xFF) & ByteArrays.getByte(rawData, LBA_OFFSET + offset + i);
            }
        }

        private Header(Builder builder) {
            this.isWrite = builder.isWrite;
            this.isAsync = builder.isAsync;
            this.errFeature = builder.errFeature;
            this.sectorCount = builder.sectorCount;
            this.cmdStatus = builder.cmdStatus;
            this.lba = Arrays.copyOf(builder.lba, builder.lba.length);
        }

        public boolean isAsync() {
            return this.isAsync;
        }

        public boolean isWrite() {
            return this.isWrite;
        }

        public int getErrFeature() {
            return this.errFeature;
        }

        public int getSectorCount() {
            return this.sectorCount;
        }

        public int getCmdStatus() {
            return this.cmdStatus;
        }

        public int[] getLba() {
            return this.lba;
        }

        @Override
        protected List<byte[]> getRawFields() {
            List<byte[]> rawFields = new ArrayList<byte[]>();

            byte aflags = (byte) (isWrite ? 0x01 : 0x00);
            if (isAsync) {
                aflags |= 0x02;
            }

            rawFields.add(ByteArrays.toByteArray(aflags));
            rawFields.add(ByteArrays.toByteArray((byte) (errFeature & 0xFF)));
            rawFields.add(ByteArrays.toByteArray((byte) (sectorCount & 0xFF)));
            rawFields.add(ByteArrays.toByteArray((byte) (cmdStatus & 0xFF)));

            for (int i = 0; i < LBA_FIELDS; i++) {
                rawFields.add(ByteArrays.toByteArray((byte) lba[i]));
            }

            // Reserved 16 bits to finish off header.
            rawFields.add(ByteArrays.toByteArray((short) 0x00));

            return rawFields;
        }

        @Override
        public int length() {
            return ATA_SIZE;
        }

        @Override
        protected String buildString() {
            StringBuilder sb = new StringBuilder();
            String ls = System.getProperty("line.separator");

            sb.append("[AoE/AtaPayload (").append(length()).append(" bytes)]").append(ls);
            sb.append("  is async: ").append(isAsync ? "yes" : "no").append(ls);
            sb.append("  is write: ").append(isWrite ? "yes" : "no").append(ls);
            sb.append("  err/feature: ").append(errFeature).append(ls);
            sb.append("  sectors: ").append(sectorCount).append(ls);
            sb.append("  cmd/status: ").append(cmdStatus).append(ls);
            sb.append("  lba: ").append(Arrays.toString(lba)).append(ls);

            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!this.getClass().isInstance(obj)) return false;

            Header other = (Header) obj;
            return this.isAsync == other.isAsync
                    && this.isWrite == other.isWrite
                    && this.errFeature == other.errFeature
                    && this.sectorCount == other.sectorCount
                    && this.cmdStatus == other.cmdStatus
                    && Arrays.equals(this.lba, other.lba);
        }

        @Override
        protected int calcHashCode() {
            int result = 17;
            result = 31 * result + errFeature;
            result = 31 * result + sectorCount;
            result = 31 * result + cmdStatus;
            for (int i = 0; i < LBA_FIELDS; i++) result = 31 * result + lba[i];
            return result;
        }
    }
}
