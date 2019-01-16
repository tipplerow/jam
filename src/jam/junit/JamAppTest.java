
package jam.junit;

import java.io.File;

import jam.app.JamApp;

import org.junit.*;
import static org.junit.Assert.*;

public class JamAppTest {
    private static final class TestApp extends JamApp {
        private TestApp() {
            super(new String[] { "data/test/app.prop" });
        }
    }

    @Test public void testReportDir() {
        TestApp app = new TestApp();
        assertEquals(new File("tmpDir"), app.getReportDir());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamAppTest");
    }
}
