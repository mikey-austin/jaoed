package org.jaoed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jaoed.config.Config;
import org.jaoed.config.ConfigBuilder;
import org.jaoed.config.Device;
import org.jaoed.config.ValidationException;
import org.jaoed.config.Validator;
import org.junit.Test;

public class ConfigTest {
    @Test
    public void testConfigParser() throws IOException, ValidationException {
        String file = getClass().getClassLoader().getResource("configTest1.conf").getFile();
        assertNotNull(file);

        Validator validator = new MockValidator();
        ConfigBuilder builder = new ConfigBuilder(validator);
        builder.parseFile(file);
        Config config = builder.build();
        assertNotNull(config);

        // Test parsed devices.
        assertTrue("parsed devices ok", config.getDevices().size() == 2);

        // Test parsed loggers.
        assertTrue("parsed loggers ok", config.getLoggers().size() == 2);

        // Test parsed acls.
        assertTrue("parsed acls ok", config.getAcls().size() == 3);

        // Test parsed interfaces.
        assertTrue("parsed interfaces ok", config.getInterfaces().size() == 2);
    }

    @Test
    public void testConfigContents() throws IOException, ValidationException {
        String expectedPath = getClass().getClassLoader().getResource("configDump.txt").getFile();
        assertNotNull(expectedPath);
        File expected = new File(expectedPath);
        assertNotNull(expected);

        String file = getClass().getClassLoader().getResource("configTest2.conf").getFile();
        assertNotNull(file);

        Validator validator = new MockValidator();
        ConfigBuilder builder = new ConfigBuilder(validator);
        builder.parseFile(file);
        Config config = builder.build();
        assertNotNull(config);

        // Compare dump to expected contents.
        List<String> lines = Arrays.asList(config.toString().split("\n"));
        assertEquals(FileUtils.readLines(expected), lines);
    }

    @Test
    public void testDuplicateDeviceTargets() throws IOException, ValidationException {
        String configData =
                "  device { \n"
                        + "  shelf = 0\n"
                        + "  slot = 0\n"
                        + "  target = \"/tmp\"\n"
                        + "}\n"
                        + "device { \n"
                        + "  shelf = 0\n"
                        + "  slot = 1\n"
                        + "  target = \"/tmp\"\n"
                        + "}";

        try {
            Validator validator = new MockValidator();
            ConfigBuilder builder = new ConfigBuilder(validator);
            builder.parseString(configData);
            Config config = builder.build();
            fail("Expected a duplicate target ValidationException to be thrown");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().matches("^Target.*already been specified"));
        }
    }

    @Test
    public void testDuplicateDeviceSlotShelf() throws IOException, ValidationException {
        String configData =
                "  device { \n"
                        + "  shelf = 3\n"
                        + "  slot = 3\n"
                        + "  target = \"/tmp/t1\"\n"
                        + "}\n"
                        + "device { \n"
                        + "  shelf = 3\n"
                        + "  slot = 3\n"
                        + "  target = \"/tmp/t2\"\n"
                        + "}";

        // Create temporary file targets.
        File t1 = File.createTempFile("t_1", ".test");
        t1.deleteOnExit();

        File t2 = File.createTempFile("t_2", ".test");
        t2.deleteOnExit();

        try {
            ConfigBuilder builder = new ConfigBuilder();
            builder.parseString(configData);
            Config config = builder.build();
            fail("Expected a duplicate slot/shelf ValidationException to be thrown");
        } catch (ValidationException e) {
            assertTrue(
                    e.getMessage(), e.getMessage().matches("^Shelf.*slot.*already been specified"));
        }
    }

    private static class MockValidator extends Validator {
        @Override
        public void validateDevice(Device device) throws ValidationException {
            // Don't validate actual target filesystem existance.
        }
    }
}
