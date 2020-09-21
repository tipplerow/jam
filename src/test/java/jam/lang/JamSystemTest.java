
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
        if (JamSystem.isMacOS())
            assertTrue(JamSystem.getCoreCount() > 0);
    }

    @Test public void testCPUCount() {
        //
        // The core count will vary by system, so just make sure that
        // the method call does not throw an exception and returns a
        // positive value...
        //
        if (JamSystem.isMacOS())
            assertTrue(JamSystem.getCPUCount() > 0);
    }

    @Test public void testIsLinux() {
        System.out.println("Is Linux? " + Boolean.toString(JamSystem.isLinux()));
    }

    @Test public void testIsMacOS() {
        System.out.println("Is MacOS? " + Boolean.toString(JamSystem.isMacOS()));
    }

    @Test public void testUname() {
        System.out.println("uname = " + JamSystem.uname());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.JamSystemTest");
    }
}
