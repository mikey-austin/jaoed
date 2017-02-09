package org.jaoed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JaoedTest extends TestCase {

    public JaoedTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(JaoedTest.class);
    }

    public void testJaoed() {
        assertTrue(true);
    }
}
