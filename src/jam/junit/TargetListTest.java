
package jam.junit;

import java.util.List;

import jam.util.TargetList;

import org.junit.*;
import static org.junit.Assert.*;

public class TargetListTest {
    private final TargetList<String> targetList =
        TargetList.create("abc", "def", "ghi", "abc", "jkl");

    @Test public void testContainsTarget() {
        assertTrue(targetList.containsTarget("abc"));
        assertTrue(targetList.containsTarget("def"));
        assertTrue(targetList.containsTarget("ghi"));
        assertTrue(targetList.containsTarget("jkl"));
        assertFalse(targetList.containsTarget("foo"));
        assertFalse(targetList.containsTarget("bar"));
    }

    @Test public void testContainsTargets() {
        assertFalse(targetList.containsTargets(List.of()));

        assertTrue(targetList.containsTargets(List.of("abc")));
        assertTrue(targetList.containsTargets(List.of("def")));
        assertTrue(targetList.containsTargets(List.of("ghi")));
        assertTrue(targetList.containsTargets(List.of("jkl")));
        assertFalse(targetList.containsTargets(List.of("foo")));
        assertFalse(targetList.containsTargets(List.of("bar")));

        assertTrue(targetList.containsTargets(List.of("abc", "def", "ghi")));
        assertFalse(targetList.containsTargets(List.of("ghi", "abc", "def")));

        assertTrue(targetList.containsTargets(List.of("abc", "jkl")));
        assertFalse(targetList.containsTargets(List.of("abc", "jkl", "abc")));

        assertTrue(targetList.containsTargets(List.of("abc", "def", "ghi", "abc", "jkl")));
        assertFalse(targetList.containsTargets(List.of("abc", "def", "ghi", "abc", "jkl", "abc")));
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
