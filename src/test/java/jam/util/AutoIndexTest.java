
package jam.util;

import org.junit.*;
import static org.junit.Assert.*;

public class AutoIndexTest {
    @Test public void testDefaultConstructor() {
        AutoIndex<String> index = AutoIndex.create();

        assertEquals(0, index.size());
        assertFalse(index.containsIndex(0));
        assertFalse(index.containsObject("abc"));

        assertEquals(0, index.add("abc"));
        assertEquals(1, index.add("def"));
        assertEquals(2, index.add("ghi"));

        assertIndex(index);
    }

    @Test public void testListConstructor() {
        AutoIndex<String> index = AutoIndex.create("abc", "def", "ghi");
        assertIndex(index);
    }

    private void assertIndex(AutoIndex<String> index) {
        assertEquals(3, index.size());

        assertEquals(0, index.indexOf("abc"));
        assertEquals(1, index.indexOf("def"));
        assertEquals(2, index.indexOf("ghi"));

        assertEquals("abc", index.lookup(0));
        assertEquals("def", index.lookup(1));
        assertEquals("ghi", index.lookup(2));
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicateObject() {
        AutoIndex<String> index = AutoIndex.create();

        index.add("abc");
        index.add("abc");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.AutoIndexTest");
    }
}
