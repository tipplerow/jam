
package jam.process;

import java.util.List;

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

    @Test public void testSetEnv() {
        JamProcess process = JamProcess.create("env");

        assertFalse(process.executed());

        process.run();

        assertTrue(process.executed());
        assertFalse(process.stdout().contains("MYTESTVAR=foo"));

        process.setenv("MYTESTVAR", "foo");
        process.run();

        assertTrue(process.executed());
        assertTrue(process.stdout().contains("MYTESTVAR=foo"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.process.JamProcessTest");
    }
}
