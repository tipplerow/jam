
package jam.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.junit.*;
import static org.junit.Assert.*;

public class AutoListTest {
    @Test public void testAdd() {
        AutoList<String> list = AutoList.create();
        assertTrue(list.isEmpty());

        // null elements (0, 1, 2, 3) automatically created...
        list.add(4, "abc");
        assertEquals(5, list.size());

        assertNull(list.get(0));
        assertNull(list.get(1));
        assertNull(list.get(2));
        assertNull(list.get(3));
        assertEquals("abc", list.get(4));

        // New element added and others moved to the right...
        list.add(2, "def");
        assertEquals(6, list.size());

        assertNull(list.get(0));
        assertNull(list.get(1));
        assertEquals("def", list.get(2));
        assertNull(list.get(3));
        assertNull(list.get(4));
        assertEquals("abc", list.get(5));
    }

    @Test public void testGet1() {
        AutoList<String> list1 = AutoList.create();
        AutoList<String> list2 = AutoList.create();

        list2.add("abc");
        list2.add("def");

        assertTrue(list1.isEmpty());
        assertFalse(list2.isEmpty());

        assertEquals(0, list1.size());
        assertEquals(2, list2.size());

        // null elements (0, 1, 2) automatically created...
        assertNull(list1.get(2));
        assertNull(list1.get(1));
        assertNull(list1.get(0));

        // null elements (2, 3) automatically created...
        assertNull(list2.get(3));
        assertNull(list2.get(2));
        assertEquals("def", list2.get(1));
        assertEquals("abc", list2.get(0));

        assertEquals(3, list1.size());
        assertEquals(4, list2.size());
    }

    @Test public void testGet2() {
        AutoList<Set<String>> list = AutoList.create(new HashSet<String>());
        assertTrue(list.isEmpty());

        // New empty HashSets automatically created in elements (0, 1, 2, 3)...
        assertTrue(list.get(3).isEmpty());
        assertEquals(4, list.size());

        assertTrue(list.get(2).isEmpty());
        assertTrue(list.get(1).isEmpty());
        assertTrue(list.get(0).isEmpty());
    }

    @Test public void testIterator() {
        AutoList<String> list = AutoList.create();

        list.set(2, "abc");
        list.set(4, "def");

        Iterator<String> iter = list.iterator();

        assertNull(iter.next());
        assertNull(iter.next());
        assertEquals("abc", iter.next());
        assertNull(iter.next());
        assertEquals("def", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test public void testMultiset() {
        AutoList<HashMultiset<Integer>> list = AutoList.create(MultisetUtil.hashFactory());
        assertTrue(list.isEmpty());

        Multiset<Integer> set = list.get(2);
        assertEquals(3, list.size());
        assertTrue(set.isEmpty());

        list.get(4).add(1);
        list.get(4).add(2);
        list.get(4).add(2);
        assertEquals(5, list.size());

        assertEquals(3, list.get(4).size());
        assertEquals(1, list.get(4).count(1));
        assertEquals(2, list.get(4).count(2));
    }

    @Test public void testRemove() {
        AutoList<String> list = AutoList.create();
        list.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        assertEquals(5, list.size());

        // A no-op to remove non-existent elements...
        assertNull(list.remove(5));
        assertEquals(5, list.size());

        assertNull(list.remove(500));
        assertEquals(5, list.size());

        assertEquals("C", list.remove(2));
        assertEquals(4, list.size());
        assertEquals(list, Arrays.asList("A", "B", "D", "E"));

        assertEquals("A", list.remove(0));
        assertEquals("D", list.remove(1));
        assertEquals(2, list.size());
        assertEquals(list, Arrays.asList("B", "E"));
    }

    @Test public void testResize() {
        AutoList<String> list = AutoList.create();
        list.addAll(Arrays.asList("A", "B"));

        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));

        list.resize(4);
        assertEquals(4, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertNull(list.get(2));
        assertNull(list.get(3));

        list.resize(1);
        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test public void testSet() {
        AutoList<String> list = AutoList.create();
        assertTrue(list.isEmpty());

        // null elements (0, 1, 2, 3) automatically created...
        list.set(4, "abc");
        assertEquals(5, list.size());

        assertNull(list.get(0));
        assertNull(list.get(1));
        assertNull(list.get(2));
        assertNull(list.get(3));
        assertEquals("abc", list.get(4));

        // Existing element changed, others remain in place...
        list.set(2, "def");
        assertEquals(5, list.size());

        assertNull(list.get(0));
        assertNull(list.get(1));
        assertEquals("def", list.get(2));
        assertNull(list.get(3));
        assertEquals("abc", list.get(4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.AutoListTest");
    }
}
