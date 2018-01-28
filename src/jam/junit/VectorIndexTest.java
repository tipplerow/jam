
package jam.junit;

import java.util.Set;
import java.util.TreeSet;

import jam.data.VectorIndex;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorIndexTest {
    @Test public void testConstructor1() {
        Set<String> keys = new TreeSet<String>();

        keys.add("abc");
        keys.add("def");
        keys.add("ghi");

        VectorIndex index = new VectorIndex(keys);

        assertEquals(3, index.size());

        assertTrue(index.contains("abc"));
        assertTrue(index.contains("def"));
        assertTrue(index.contains("ghi"));

        assertEquals(0, index.indexOf("abc"));
        assertEquals(1, index.indexOf("def"));
        assertEquals(2, index.indexOf("ghi"));

        assertEquals("abc", index.keys().get(0));
        assertEquals("def", index.keys().get(1));
        assertEquals("ghi", index.keys().get(2));
    }

    @Test public void testConstructor2() {
        VectorIndex index = new VectorIndex("ghi", "def", "abc");

        assertEquals(3, index.size());

        assertTrue(index.contains("abc"));
        assertTrue(index.contains("def"));
        assertTrue(index.contains("ghi"));

        assertEquals(0, index.indexOf("ghi"));
        assertEquals(1, index.indexOf("def"));
        assertEquals(2, index.indexOf("abc"));

        assertEquals("ghi", index.keys().get(0));
        assertEquals("def", index.keys().get(1));
        assertEquals("abc", index.keys().get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateKey() {
        VectorIndex index = new VectorIndex("abc", "ghi", "def", "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingKey() {
        VectorIndex index = new VectorIndex("ghi", "def", "abc");
        index.indexOf("foo");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.VectorIndexTest");
    }
}
