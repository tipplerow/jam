
package jam.junit;

import java.util.List;

import jam.process.JamProcess;

import org.junit.*;
import static org.junit.Assert.*;

public class JamProcessTest {
    @Test public void testRun() {
        JamProcess process = JamProcess.run("ls");

        assertTrue(process.success());
        assertTrue(process.stdout().contains("build.xml"));
        assertTrue(process.stderr().isEmpty());

        process = JamProcess.run("cat", "foo");
        
        assertFalse(process.success());
        assertTrue(process.stdout().isEmpty());
        assertEquals(List.of("cat: foo: No such file or directory"), process.stderr());
        
        process = JamProcess.run("cat", "LICENSE");

        assertEquals(201, process.stdout().size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamProcessTest");
    }
}
