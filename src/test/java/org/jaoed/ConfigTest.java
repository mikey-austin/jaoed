package org.jaoed;

import java.io.IOException;
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

    public void testConfigParser() throws IOException {
        String file = getClass()
            .getClassLoader()
            .getResource("configTest1.conf")
            .getFile();
        assertNotNull(file);

        Config config = Config.parseFile(file);
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
}
