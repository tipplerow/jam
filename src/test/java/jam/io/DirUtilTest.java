
package jam.io;

import java.io.File;

import jam.io.DirUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class DirUtilTest {
    @Test public void testOnDemandExistingDir() {
        File home = new File(System.getProperty("java.home"));

        // The Java runtime environment must exist...
        assertTrue(home.isDirectory());

        // ...and "onDemand" should return silently.
        DirUtil.onDemand(home);
    }

    @Test(expected = RuntimeException.class)
    public void testOnDemandExistingFile() {
        //
        // This test should be run from the top-level "jam" directory,
        // which must contain the "build.gradle" file...
        //
        File file = new File(System.getProperty("user.dir"), "build.gradle");

        assertTrue(file.isFile());
        DirUtil.onDemand(file);
    }

    @Test public void testOnDemandNonExistent() {
        File dir1 = new File("workdir1");
        File dir2 = new File(dir1, "workdir2");

        assertFalse(dir1.exists());
        assertFalse(dir2.exists());

        DirUtil.onDemand(dir2);

        assertTrue(dir1.exists());
        assertTrue(dir2.exists());

        assertTrue(dir1.isDirectory());
        assertTrue(dir2.isDirectory());

        assertTrue(dir2.delete());
        assertTrue(dir1.delete());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.DirUtilTest");
    }
}
