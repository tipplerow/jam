
package jam.junit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jam.util.ConcatIterator;

import org.junit.*;
import static org.junit.Assert.*;

public class ConcatIteratorTest extends NumericTestBase {
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
    }

    @Test public void testEmpty() {
        Iterator<String> iter1 = ConcatIterator.concat(List.of());
        Iterator<String> iter2 = ConcatIterator.over(List.of());

        assertFalse(iter1.hasNext());
        assertFalse(iter2.hasNext());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ConcatIteratorTest");
    }
}
