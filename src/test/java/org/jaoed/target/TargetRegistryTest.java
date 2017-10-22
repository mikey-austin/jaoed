package org.jaoed.target;

import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.jaoed.config.Device;
import org.jaoed.net.RequestContext;
import org.jaoed.packet.AoeFrame;
import org.jaoed.packet.PacketProcessor;
import static org.jaoed.target.TargetUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class TargetRegistryTest {
    @Mock CommandFactory commandFactory;
    @Mock ResponseProcessor responseProcessor;
    @Mock ConfigArea configArea;
    
    @Test
    public void testTargetRegistry() throws Exception {
        int major = 65300, minor = 254;
        Device device = new Device()
            .setShelf(major)
            .setSlot(minor)
            .setTarget("/dev/sdd3");
        DeviceTarget target = DeviceTarget
            .newBuilder()
            .setDeviceConfig(device)
            .setResponseProcessor(responseProcessor)
            .setCommandFactory(commandFactory)
            .setConfigArea(configArea)
            .build();

        TargetRegistry registry = new TargetRegistry()
            .addTarget(target);

        // Test non-existant target.
        assertFalse(registry.lookup((short) 255, (byte) 13).isPresent());

        // Test real target.
        short encodedMajor = encodeMajor(major);
        byte encodedMinor = encodeMinor(minor);
        assertTrue(registry.lookup(encodedMajor, encodedMinor).isPresent());
    }
}
