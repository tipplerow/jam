
package jam.junit;

import jam.io.TOCFile;

import org.junit.*;
import static org.junit.Assert.*;

public class TOCFileTest {
    @Test public void testAll() {
        TOCFile file = TOCFile.instance("data/test/__TOC.txt");

        assertFalse(file.exists());
        assertFalse(file.contains("abc"));

        file.add("abc");

        assertTrue(file.exists());
        assertTrue(file.contains("abc"));
        assertFalse(file.contains("def"));

        file.add("abc");
        file.add("def");

        assertTrue(file.contains("abc"));
        assertTrue(file.contains("def"));

        assertTrue(file.delete());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TOCFileTest");
    }
}
