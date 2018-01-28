
package jam.junit;

import jam.app.JamLogger;

import org.junit.*;
import static org.junit.Assert.*;

public class JamLoggerTest {
    @Test public void testConfig() {
	System.out.println("debug");
	JamLogger.debug("debug");
	System.out.println("info");
	JamLogger.info("info");
	System.out.println("warn");
	JamLogger.warn("warn");
	System.out.println("error");
	JamLogger.error("error");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("tip.junit.JamLoggerTest");
    }
}
