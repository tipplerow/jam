
package jam.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class ConcatIteratorTest {
    @Test public void testAll() {
        List<String> list1 = List.of("A", "B", "C");
        List<String> list2 = new LinkedList<String>(List.of("D", "E"));
        List<String> list3 = List.of("F", "G", "H", "I");

        Iterator<String> iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("C", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("D", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("E", iterator.next());

        iterator.remove();
        assertEquals(List.of("D"), list2);

        assertTrue(iterator.hasNext());
        assertEquals("F", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("G", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("H", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("I", iterator.next());

        assertFalse(iterator.hasNext());
        
        iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertEquals("D", iterator.next());
        assertEquals("F", iterator.next()); // "E" was removed...
        assertEquals("G", iterator.next());
        assertEquals("H", iterator.next());
        assertEquals("I", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test public void testEmpty() {
        Iterator<String> iter1 = ConcatIterator.concat(List.of());
        Iterator<String> iter2 = ConcatIterator.over(List.of());

        assertFalse(iter1.hasNext());
        assertFalse(iter2.hasNext());
    }

    @Test public void testFirstEmpty() {
        List<String> list1 = List.of();
        List<String> list2 = List.of("A", "B");

        Iterator<String> iterator = ConcatIterator.over(List.of(list1, list2));

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());

        assertFalse(iterator.hasNext());

        iterator = ConcatIterator.over(List.of(list1, list2));

        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test public void testMiddleEmpty() {
        List<String> list1 = List.of("A", "B");
        List<String> list2 = List.of();
        List<String> list3 = List.of("C");

        Iterator<String> iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("C", iterator.next());

        assertFalse(iterator.hasNext());

        iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test public void testLastEmpty() {
        List<String> list1 = List.of("A", "B");
        List<String> list2 = List.of("C");
        List<String> list3 = List.of();

        Iterator<String> iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("C", iterator.next());

        assertFalse(iterator.hasNext());

        iterator = ConcatIterator.over(List.of(list1, list2, list3));

        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertFalse(iterator.hasNext());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.ConcatIteratorTest");
    }
}
