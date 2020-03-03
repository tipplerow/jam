
package jam.junit;

import java.util.List;

import jam.process.ProcessRunner;

import org.junit.*;
import static org.junit.Assert.*;

public class ProcessRunnerTest {
    @Test public void testEnv() {
        assertTrue(ProcessRunner.run("ls").contains("build.xml"));
        assertEquals(201, ProcessRunner.run("cat", "LICENSE").size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ProcessRunnerTest");
    }
}
