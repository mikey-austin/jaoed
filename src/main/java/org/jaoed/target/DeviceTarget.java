package org.jaoed.target;

import java.lang.Runnable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.pcap4j.packet.EthernetPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;

public class DeviceTarget implements PacketProcessor, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceTarget.class);

    private final BlockingQueue<TargetCommand> inputQueue;
    private final ResponseProcessor responseProcessor;
    private final CommandFactory commandFactory;
    private final Device deviceConfig;
    private final int pollInterval;
    private final ConfigArea configArea;
    private final short firmwareVersion;
    private final byte sectorCount;

    private volatile boolean running;

    private DeviceTarget(Builder builder) {
        this.inputQueue = new ArrayBlockingQueue<>(builder.inputQueueSize);
        this.responseProcessor = builder.responseProcessor;
        this.deviceConfig = builder.deviceConfig;
        this.commandFactory = builder.commandFactory;
        this.pollInterval = builder.pollInterval;
        this.running = false;
        this.configArea = builder.configArea;
        this.firmwareVersion = builder.firmwareVersion;
        this.sectorCount = builder.sectorCount;
    }

    @Override
    public String toString() {
        return deviceConfig.getTarget();
    }

    @Override
    public void run() {
        this.running = true;
        while (running) {
            try {
                TargetCommand command = inputQueue.poll(pollInterval, TimeUnit.MILLISECONDS);
                if (command != null) {
                    responseProcessor.enqueue(
                        command.execute(this));
                }
            } catch (Exception e) {
                LOG.error("error executing command in {} target", deviceConfig.getTarget());
            }
        }
    }

    public short getFirmwareVersion() {
        return firmwareVersion;
    }

    public byte getSectorCount() {
        return sectorCount;
    }

    public short getBufferCount() {
        return (short) inputQueue.size();
    }

    public Device getDevice() {
        return deviceConfig;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public boolean enqueue(RequestContext ctx) {
        return inputQueue.offer(
            commandFactory.makeCommand(ctx));
    }

    public ConfigArea getConfigArea() {
        return configArea;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int inputQueueSize;
        private int pollInterval;
        private ResponseProcessor responseProcessor;
        private Device deviceConfig;
        private CommandFactory commandFactory;
        private ConfigArea configArea;
        private short firmwareVersion;
        private byte sectorCount;

        private Builder() {
            this.pollInterval = 1_000;
            this.inputQueueSize = 1_000;
            this.responseProcessor = null;
            this.deviceConfig = null;
            this.commandFactory = null;
            this.configArea = null;
            this.sectorCount = 0;
            this.firmwareVersion = 0;
        }

        public Builder setResponseProcessor(ResponseProcessor responseProcessor) {
            this.responseProcessor = responseProcessor;
            return this;
        }

        public Builder setPollInterval(int intervalMs) {
            this.pollInterval = intervalMs;
            return this;
        }

        public Builder setInputQueueSize(int size) {
            this.inputQueueSize = size;
            return this;
        }

        public Builder setDeviceConfig(Device deviceConfig) {
            this.deviceConfig = deviceConfig;
            return this;
        }

        public Builder setCommandFactory(CommandFactory commandFactory) {
            this.commandFactory = commandFactory;
            return this;
        }

        public Builder setConfigArea(ConfigArea configArea) {
            this.configArea = configArea;
            return this;
        }

        public Builder setFirmwareVersion(short firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder setSectorCount(byte sectorCount) {
            this.sectorCount = sectorCount;
            return this;
        }

        public DeviceTarget build() throws Exception {
            if (deviceConfig == null) {
                throw new Exception("missing device configuration");
            }
            if (responseProcessor == null) {
                throw new Exception("missing response processor");
            }
            if (commandFactory == null) {
                throw new Exception("missing command factory");
            }
            if (configArea == null) {
                throw new Exception("missing device config area");
            }
            return new DeviceTarget(this);
        }
    }
}
