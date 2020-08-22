
package jam.junit;

import java.util.List;

import jam.io.IOUtil;
import jam.io.TOCFile;

import org.junit.*;
import static org.junit.Assert.*;

public class TOCFileTest {
    @Test public void testAll() {
        String fileName = "data/test/__TOC.txt";

        TOCFile file1 = TOCFile.instance(fileName);
        TOCFile file2 = TOCFile.instance(fileName);

        assertFalse(file1.exists());
        assertFalse(file1.contains("abc"));

        assertFalse(file2.exists());
        assertFalse(file2.contains("abc"));

        file1.add("abc");

        assertTrue(file1.exists());
        assertTrue(file1.contains("abc"));
        assertFalse(file1.contains("def"));

        assertTrue(file2.exists());
        assertTrue(file2.contains("abc"));
        assertFalse(file2.contains("def"));

        file2.add("abc");
        file2.add("abc");
        file2.add("def");
        file2.add("def");
        file2.add("def");

        assertTrue(file1.contains("abc"));
        assertTrue(file1.contains("def"));

        assertTrue(file2.contains("abc"));
        assertTrue(file2.contains("def"));

        // Ensure that no duplicate items are written...
        assertEquals(List.of("abc", "def"), IOUtil.readLines(fileName));

        assertTrue(file1.delete());
        assertFalse(file2.delete());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TOCFileTest");
    }
}
