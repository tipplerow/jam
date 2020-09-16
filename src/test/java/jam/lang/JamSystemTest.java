
package jam.lang;

import org.junit.*;
import static org.junit.Assert.*;

public class JamSystemTest {
    @Test public void testCoreCount() {
        //
        // The core count will vary by system, so just make sure that
        // the method call does not throw an exception and returns a
        // positive value...
        //
        assertTrue(JamSystem.getCoreCount() > 0);
    }

    @Test public void testCPUCount() {
        //
        // The core count will vary by system, so just make sure that
        // the method call does not throw an exception and returns a
        // positive value...
        //
        assertTrue(JamSystem.getCPUCount() > 0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.JamSystemTest");
    }
}
