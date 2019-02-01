
package jam.junit;

import jam.app.JamEnv;;

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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamEnvTest");
    }
}
