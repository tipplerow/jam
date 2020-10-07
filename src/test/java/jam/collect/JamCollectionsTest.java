
package jam.collect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.*;
import static org.junit.Assert.*;

public final class JamCollectionsTest {
    @Test public void testEqualsContents() {
        Set<String> col1 = new HashSet<String>();
        Set<String> col2 = new TreeSet<String>();
        List<String> col3 = new ArrayList<String>();
        List<String> col4 = new LinkedList<String>();

        col1.addAll(List.of("A", "B", "C", "D"));
        col2.addAll(List.of("B", "C", "D", "A"));
        col3.addAll(List.of("C", "D", "A", "B"));
        col4.addAll(List.of("D", "A", "B" ,"C"));

        assertTrue(JamCollections.equalsContents(col1, col1));
        assertTrue(JamCollections.equalsContents(col1, col2));
        assertTrue(JamCollections.equalsContents(col1, col3));
        assertTrue(JamCollections.equalsContents(col1, col4));

        assertTrue(JamCollections.equalsContents(col2, col1));
        assertTrue(JamCollections.equalsContents(col2, col2));
        assertTrue(JamCollections.equalsContents(col2, col3));
        assertTrue(JamCollections.equalsContents(col2, col4));

        assertTrue(JamCollections.equalsContents(col3, col1));
        assertTrue(JamCollections.equalsContents(col3, col2));
        assertTrue(JamCollections.equalsContents(col3, col3));
        assertTrue(JamCollections.equalsContents(col3, col4));

        assertTrue(JamCollections.equalsContents(col4, col1));
        assertTrue(JamCollections.equalsContents(col4, col2));
        assertTrue(JamCollections.equalsContents(col4, col3));
        assertTrue(JamCollections.equalsContents(col4, col4));

        assertFalse(JamCollections.equalsContents(List.of("A", "B", "C"), List.of("A", "B")));
        assertFalse(JamCollections.equalsContents(List.of("A", "B", "C"), List.of("A", "B", "C", "D")));
    }

    @Test public void testEqualsOrdered() {
        Set<String> col1 = new TreeSet<String>();
        List<String> col2 = new ArrayList<String>();
        List<String> col3 = new LinkedList<String>();

        col1.addAll(List.of("A", "B", "C", "D"));
        col2.addAll(List.of("A", "B", "C", "D"));
        col3.addAll(List.of("C", "D", "A", "B"));

        assertTrue(JamCollections.equalsOrdered(col1, col1));
        assertTrue(JamCollections.equalsOrdered(col1, col2));
        assertFalse(JamCollections.equalsOrdered(col1, col3));

        assertTrue(JamCollections.equalsOrdered(col2, col1));
        assertTrue(JamCollections.equalsOrdered(col2, col2));
        assertFalse(JamCollections.equalsOrdered(col2, col3));

        assertFalse(JamCollections.equalsOrdered(col3, col1));
        assertFalse(JamCollections.equalsOrdered(col3, col2));
        assertTrue(JamCollections.equalsOrdered(col3, col3));

        assertFalse(JamCollections.equalsOrdered(List.of("A", "B", "C"), List.of("A", "B")));
        assertFalse(JamCollections.equalsOrdered(List.of("A", "B", "C"), List.of("A", "B", "C", "D")));
    }

    @Test public void testPeek() {
        assertNull(JamCollections.peek(List.of()));
        assertEquals("abc", JamCollections.peek(List.of("abc")));
        assertEquals("abc", JamCollections.peek(List.of("abc", "def", "ghi")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.JamCollectionsTest");
    }
}
