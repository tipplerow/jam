
package jam.junit;

import java.util.List;

import jam.util.TargetList;

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

    @Test public void testContainsTargets() {
        assertFalse(targetList.contains(List.of()));

        assertTrue(targetList.contains(List.of("abc")));
        assertTrue(targetList.contains(List.of("def")));
        assertTrue(targetList.contains(List.of("ghi")));
        assertTrue(targetList.contains(List.of("jkl")));
        assertFalse(targetList.contains(List.of("foo")));
        assertFalse(targetList.contains(List.of("bar")));

        assertTrue(targetList.contains(List.of("abc", "def", "ghi")));
        assertFalse(targetList.contains(List.of("ghi", "abc", "def")));

        assertTrue(targetList.contains(List.of("abc", "jkl")));
        assertFalse(targetList.contains(List.of("abc", "jkl", "abc")));

        assertTrue(targetList.contains(List.of("abc", "def", "ghi", "abc", "jkl")));
        assertFalse(targetList.contains(List.of("abc", "def", "ghi", "abc", "jkl", "abc")));
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
        org.junit.runner.JUnitCore.main("jam.junit.TargetListTest");
    }
}
