
package jam.junit;

import java.io.File;

import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class FileUtilTest {
    @Test public void testEnsureParentDirs() {
        File dir  = new File("foo");
        File file = new File(dir, "foo.txt");

        assertFalse(dir.exists());
        FileUtil.ensureParentDirs(file);
        assertTrue(dir.exists());
        assertTrue(dir.delete());
    }

    @Test public void testGetParentName() {
        assertEquals(".", FileUtil.getParentName(new File("foo.txt")));
        assertEquals("foo", FileUtil.getParentName(new File("foo/bar.txt")));
    }

    @Test public void testRequireFound() {
        FileUtil.requireFile(System.getProperty("user.home"));
    }

    @Test(expected = RuntimeException.class)
    public void testRequireNotFound() {
        FileUtil.requireFile("no such file");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FileUtilTest");
    }
}
