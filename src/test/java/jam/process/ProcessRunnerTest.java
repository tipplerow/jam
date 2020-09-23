
package jam.process;

import org.junit.*;
import static org.junit.Assert.*;

public class ProcessRunnerTest {
    @Test public void testRun() {
        assertTrue(ProcessRunner.run("ls").contains("build.gradle"));
        assertEquals(201, ProcessRunner.run("cat", "LICENSE").size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.process.ProcessRunnerTest");
    }
}
