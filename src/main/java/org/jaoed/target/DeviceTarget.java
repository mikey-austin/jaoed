package org.jaoed.target;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.PacketProcessor;
import org.jaoed.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceTarget implements PacketProcessor, Runnable, Service {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceTarget.class);

    private final Thread deviceThread;
    private final BlockingQueue<TargetCommand> inputQueue;
    private final ResponseProcessor responseProcessor;
    private final CommandFactory commandFactory;
    private final Device deviceConfig;
    private final int pollInterval;
    private final ConfigArea configArea;
    private final short firmwareVersion;
    private final byte sectorCount;
    private final int inputQueueSize;

    private volatile boolean running;

    private DeviceTarget(Builder builder) {
        this.inputQueueSize = builder.inputQueueSize;
        this.inputQueue = new ArrayBlockingQueue<>(builder.inputQueueSize);
        this.responseProcessor = builder.responseProcessor;
        this.deviceConfig = builder.deviceConfig;
        this.commandFactory = builder.commandFactory;
        this.pollInterval = builder.pollInterval;
        this.running = false;
        this.configArea = builder.configArea;
        this.firmwareVersion = builder.firmwareVersion;
        this.sectorCount = builder.sectorCount;
        this.deviceThread = new Thread(this);
    }

    @Override
    public String toString() {
        return deviceConfig.getTarget();
    }

    @Override
    public void run() {
        LOG.info(
                "starting target {} thread [queue = {}; poll = {}ms]",
                deviceConfig.getTarget(),
                inputQueueSize,
                pollInterval);

        this.running = true;
        while (running) {
            try {
                TargetCommand command = inputQueue.poll(pollInterval, TimeUnit.MILLISECONDS);
                if (command != null) {
                    command.execute(this).ifPresent(responseProcessor::enqueue);
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
        return (short) inputQueueSize;
    }

    public Device getDevice() {
        return deviceConfig;
    }

    @Override
    public void start() {
        deviceThread.start();
    }

    @Override
    public void stop() {
        LOG.info("stopping target {} thread", deviceConfig.getTarget());
        this.running = false;
        try {
            deviceThread.join();
        } catch (Exception e) {
            LOG.error("an error occured whilst exiting device thread", e);
        }
    }

    @Override
    public boolean enqueue(RequestContext ctx) {
        return commandFactory.makeCommand(ctx).map(inputQueue::offer).orElse(false);
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
