
package jam.util;

import java.util.Iterator;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class UniqueListTest {
    @Test public void testAdd() {
        List<String> list = UniqueList.create();

        assertTrue(list.add("A"));
        assertTrue(list.add("B"));
        assertFalse(list.add("A"));
        assertTrue(list.add("C"));
        assertFalse(list.add("C"));

        assertEquals(List.of("A", "B", "C"), list);

        list.add(1, "D");
        assertEquals(List.of("A", "D", "B", "C"), list);

        list.add(2, "A");
        assertEquals(List.of("A", "D", "B", "C"), list);
    }

    @Test public void testContains() {
        List<String> list = UniqueList.create("A", "A", "B", "C", "B");

        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
        assertTrue(list.contains("C"));
        assertFalse(list.contains("D"));
        assertFalse(list.contains("E"));
    }

    @Test public void testGet() {
        List<String> list = UniqueList.create("A", "A", "B", "C", "B");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test public void testIterator() {
        List<String> list = UniqueList.create("A", "A", "B", "C", "B");
        Iterator<String> iter = list.iterator();

        assertEquals("A", iter.next());
        assertEquals("B", iter.next());
        assertEquals("C", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test public void testRemoveIndex() {
        List<String> list = UniqueList.create("A", "A", "B", "C", "B");

        assertTrue(list.contains("B"));
        assertEquals("B", list.remove(1));

        assertFalse(list.contains("B"));
        assertEquals(List.of("A", "C"), list);

        assertTrue(list.contains("A"));
        assertEquals("A", list.remove(0));

        assertFalse(list.contains("A"));
        assertEquals(List.of("C"), list);

        list.add("A");
        assertEquals(List.of("C", "A"), list);
    }

    @Test public void testRemoveObject() {
        List<String> list = UniqueList.create("A", "A", "B", "C", "B");

        assertTrue(list.contains("B"));
        assertTrue(list.remove("B"));

        assertFalse(list.contains("B"));
        assertEquals(List.of("A", "C"), list);

        assertFalse(list.remove("B"));
        assertEquals(List.of("A", "C"), list);

        list.add("B");
        assertTrue(list.contains("B"));
        assertEquals(List.of("A", "C", "B"), list);
    }

    @Test public void testSize() {
        List<String> list = UniqueList.create();
        assertEquals(0, list.size());

        assertTrue(list.add("A"));
        assertEquals(1, list.size());

        assertTrue(list.add("B"));
        assertEquals(2, list.size());

        assertFalse(list.add("A"));
        assertEquals(2, list.size());

        assertTrue(list.add("C"));
        assertEquals(3, list.size());

        assertFalse(list.add("C"));
        assertEquals(3, list.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.UniqueListTest");
    }
}
