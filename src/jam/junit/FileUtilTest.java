
package jam.junit;

import java.io.File;

import jam.app.JamHome;
import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class FileUtilTest {
    @Test public void testEnsureDir() {
        FileUtil.ensureDir(".");
        FileUtil.ensureDir(System.getenv("JAM_HOME"));

        File dir1 = new File("dir1");
        File dir2 = new File(dir1, "dir2");

        assertFalse(dir1.exists());
        assertFalse(dir2.exists());

        FileUtil.ensureDir(dir2);

        assertTrue(dir1.exists());
        assertTrue(dir2.exists());

        assertTrue(dir2.delete());
        assertTrue(dir1.delete());
    }

    @Test public void testEnsureParentDirs() {
        File dir  = new File("foo");
        File file = new File(dir, "foo.txt");

        assertFalse(dir.exists());

        FileUtil.ensureParentDirs(file);

        assertTrue(dir.exists());
        assertTrue(dir.delete());
    }

    @Test public void testGetBasename() {
        assertEquals("foo.txt", FileUtil.getBasename(new File("foo.txt")));
        assertEquals("foo.txt", FileUtil.getBasename(new File("/var/tmp/foo.txt")));
    }

    @Test public void testGetBasenamePrefix() {
        assertEquals("foo", FileUtil.getBasenamePrefix(new File("foo.txt")));
        assertEquals("foo", FileUtil.getBasenamePrefix(new File("/var/tmp/foo.txt.gz")));
    }

    @Test public void testGetCanonicalPath() {
        assertEquals(FileUtil.join(JamHome.NAME, "foo.txt"), FileUtil.getCanonicalPath(new File("foo.txt")));
    }

    @Test public void testGetCanonicalPrefix() {
        assertEquals(FileUtil.join(JamHome.NAME, "foo"), FileUtil.getCanonicalPrefix(new File("foo.xml.gz")));
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
