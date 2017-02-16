package org.jaoed;

import java.io.IOException;
import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Collection;

import org.jaoed.config.*;

public class ConfigTest extends TestCase {

    public ConfigTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ConfigTest.class);
    }

    public void testConfigParser() throws IOException, ValidationException {
        String file = getClass()
            .getClassLoader()
            .getResource("configTest1.conf")
            .getFile();
        assertNotNull(file);

        Config config = Config.parseFile(file, false);
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
            Config config = Config.parseString(configData);
            fail("Expected a duplicate target ValidationException to be thrown");
        } catch (ValidationException e) {
            assertTrue(
                e.getMessage().matches("^Target.*already been specified"));
        }
    }

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
            Config config = Config.parseString(configData);
            fail("Expected a duplicate slot/shelf ValidationException to be thrown");
        } catch (ValidationException e) {
            assertTrue(e.getMessage(),
                e.getMessage().matches("^Shelf.*slot.*already been specified"));
        }
    }
}
