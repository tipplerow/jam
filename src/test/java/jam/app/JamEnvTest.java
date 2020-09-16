
package jam.app;

import java.io.File;
import java.io.IOException;

import org.junit.*;
import static org.junit.Assert.*;

public class JamEnvTest {
    @Test public void testIsSet() {
        assertTrue(JamEnv.isSet("JAM_HOME"));
        assertFalse(JamEnv.isSet("no such variable"));
    }

    @Test public void testIsUnset() {
        assertTrue(JamEnv.isUnset("no such variable"));
        assertFalse(JamEnv.isUnset("JAM_HOME"));
    }

    @Test public void testReplace() throws IOException {
        String jamHome = System.getenv("JAM_HOME");
        String userName = System.getenv("USER");

        assertEquals(jamHome, JamEnv.replaceVariable("${JAM_HOME}"));
        assertEquals("foo/" + jamHome + "/xyz/" + userName, JamEnv.replaceVariable("foo/${JAM_HOME}/xyz/${USER}"));
    }

    @Test(expected = RuntimeException.class)
    public void testReplaceUnset() {
        JamEnv.replaceVariable("${__NOSUCHVAR__}");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.app.JamEnvTest");
    }
}
