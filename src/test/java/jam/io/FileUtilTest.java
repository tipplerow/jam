
package jam.io;

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
        File dir  = new File("foodir");
        File file = new File(dir, "foo.txt");

        assertFalse(dir.exists());

        FileUtil.ensureParentDirs(file);

        assertTrue(dir.exists());
        assertTrue(dir.delete());
    }

    @Test public void testGetBaseName() {
        assertEquals("foo.txt", FileUtil.getBaseName(new File("foo.txt")));
        assertEquals("foo.txt", FileUtil.getBaseName(new File("/var/tmp/foo.txt")));
    }

    @Test public void testGetBaseNamePrefix() {
        assertEquals("foo", FileUtil.getBaseNamePrefix(new File("foo.txt")));
        assertEquals("foo", FileUtil.getBaseNamePrefix(new File("/var/tmp/foo.txt.gz")));
    }

    @Test public void testGetCanonicalFile() {
        File localFile = new File("foo.txt");
        File canonicalFile = FileUtil.getCanonicalFile(localFile);

        assertEquals("foo.txt", localFile.getPath());
        assertEquals(FileUtil.join(JamHome.NAME, "foo.txt"), canonicalFile.getPath());
    }

    @Test public void testGetCanonicalPath() {
        assertEquals(FileUtil.join(JamHome.NAME, "foo.txt"), FileUtil.getCanonicalPath(new File("foo.txt")));
    }

    @Test public void testGetCanonicalPrefix() {
        assertEquals(FileUtil.join(JamHome.NAME, "foo"), FileUtil.getCanonicalPrefix(new File("foo.xml.gz")));
    }

    @Test public void testGetDirName() {
        assertEquals(".", FileUtil.getDirName(new File("foo.txt")));
        assertEquals("foo", FileUtil.getDirName(new File("foo/bar.txt")));
    }

    @Test public void testIsCanonicalFile() {
        assertFalse(FileUtil.isCanonicalFile(new File("foo.txt")));
        assertFalse(FileUtil.isCanonicalFile(new File("data/test/foo.txt")));
        assertTrue(FileUtil.isCanonicalFile(new File("/usr/bin/syslog")));
        assertTrue(FileUtil.isCanonicalFile(new File("/usr/bin/syslog/")));
    }

    @Test public void testRequireFound() {
        FileUtil.requireFile(System.getProperty("user.home"));
    }

    @Test(expected = RuntimeException.class)
    public void testRequireNotFound() {
        FileUtil.requireFile("no such file");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.FileUtilTest");
    }
}
