
package jam.util;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class TargetListTest {
    private final TargetList<String> targetList =
        TargetList.create("abc", "def", "ghi", "abc", "jkl");

    @Test public void testContainsTarget() {
        assertTrue(targetList.contains("abc"));
        assertTrue(targetList.contains("def"));
        assertTrue(targetList.contains("ghi"));
        assertTrue(targetList.contains("jkl"));
        assertFalse(targetList.contains("foo"));
        assertFalse(targetList.contains("bar"));
    }

    @Test public void testContainsTargetSequence() {
        assertFalse(targetList.containsSequence(List.of()));

        assertTrue(targetList.containsSequence(List.of("abc")));
        assertTrue(targetList.containsSequence(List.of("def")));
        assertTrue(targetList.containsSequence(List.of("ghi")));
        assertTrue(targetList.containsSequence(List.of("jkl")));
        assertFalse(targetList.containsSequence(List.of("foo")));
        assertFalse(targetList.containsSequence(List.of("bar")));

        assertTrue(targetList.containsSequence(List.of("abc", "def", "ghi")));
        assertFalse(targetList.containsSequence(List.of("ghi", "abc", "def")));

        assertTrue(targetList.containsSequence(List.of("abc", "jkl")));
        assertFalse(targetList.containsSequence(List.of("abc", "jkl", "abc")));

        assertTrue(targetList.containsSequence(List.of("abc", "def", "ghi", "abc", "jkl")));
        assertFalse(targetList.containsSequence(List.of("abc", "def", "ghi", "abc", "jkl", "abc")));
    }

    @Test public void testCountTarget() {
        assertEquals(2, targetList.count("abc"));
        assertEquals(1, targetList.count("def"));
        assertEquals(1, targetList.count("ghi"));
        assertEquals(1, targetList.count("jkl"));
        assertEquals(0, targetList.count("foo"));
        assertEquals(0, targetList.count("bar"));
    }

    @Test public void testCountTargetSequence() {
        TargetList<String> targetList =
            TargetList.create("A", "B", "C", "C", "A", "B", "D", "A", "B", "E");

        assertEquals(3, targetList.countSequence(List.of("A")));
        assertEquals(3, targetList.countSequence(List.of("B")));
        assertEquals(2, targetList.countSequence(List.of("C")));
        assertEquals(1, targetList.countSequence(List.of("D")));
        assertEquals(1, targetList.countSequence(List.of("E")));
        assertEquals(3, targetList.countSequence(List.of("A", "B")));
        assertEquals(1, targetList.countSequence(List.of("A", "B", "C")));
        assertEquals(1, targetList.countSequence(List.of("A", "B", "D")));
        assertEquals(1, targetList.countSequence(List.of("A", "B", "E")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        targetList.add("abc");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClear() {
        targetList.clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove1() {
        targetList.remove("abc");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove2() {
        targetList.remove(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSet() {
        targetList.set(1, "abc");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.TargetListTest");
    }
}
