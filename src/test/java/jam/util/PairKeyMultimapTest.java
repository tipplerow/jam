
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.*;
import static org.junit.Assert.*;

public final class PairKeyMultimapTest {
    private PairKeyMultimap<String, Integer, String> table;

    @Test public void testHash() {
        table = PairKeyMultimap.hash();
        runTest();
    }

    @Test public void testTree() {
        table = PairKeyMultimap.tree();
        runTest();

        // Check ordering of keys...
        assertEquals(List.of(-2, 1), new ArrayList<Integer>(table.innerKeySet("abc")));
        assertEquals(List.of(-3, 2, 4), new ArrayList<Integer>(table.innerKeySet("def")));
        assertEquals(List.of("abc", "def", "ghi"), new ArrayList<String>(table.outerKeySet()));
    }

    private void runTest() {
        fillTable();
        testContains();
        testGet();
        testKeySets();
        testSize();
        testRemove();
    }

    private void fillTable() {
        table.put("abc",  1, "abc.1.A");
        table.put("abc",  1, "abc.1.B");
        table.put("abc", -2, "abc.2");
        table.put("def",  2, "def.2");
        table.put("def", -3, "def.3");
        table.put("def",  4, "def.4");
        table.put("ghi",  5, "ghi.5.A");
        table.put("ghi",  5, "ghi.5.B");
        table.put("ghi",  5, "ghi.5.C");
    }

    private void testContains() {
        assertTrue(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));

        assertTrue(table.contains("def", 2));
        assertFalse(table.contains("def", 1));
    }

    private void testGet() {
        assertValues(List.of("abc.1.A", "abc.1.B"), table.get("abc", 1));
        assertTrue(table.get("abc", 2).isEmpty());

        assertValues(List.of("def.2"), table.get("def", 2));
        assertTrue(table.get("def", 1).isEmpty());

        assertValues(List.of("ghi.5.A", "ghi.5.B", "ghi.5.C"), table.get("ghi", 5));
        assertTrue(table.get("ghi", 2).isEmpty());
    }

    private void assertValues(List<String> expected, Collection<String> actual) {
        assertEquals(new TreeSet<String>(expected), new TreeSet<String>(actual));
    }

    private void testKeySets() {
        assertEquals(Set.of("abc", "def", "ghi"), table.outerKeySet());

        assertEquals(Set.of(5), table.innerKeySet("ghi"));
        assertEquals(Set.of(1, -2), table.innerKeySet("abc"));
        assertEquals(Set.of(2, -3, 4), table.innerKeySet("def"));
    }

    private void testRemove() {
        assertTrue(table.contains("ghi", 5));
        assertEquals(9, table.size());

        table.remove("ghi", 5);
        
        assertFalse(table.contains("ghi", 5));
        assertValues(List.of(), table.get("ghi", 5));
        assertEquals(6, table.size());
    }

    private void testSize() {
        assertEquals(9, table.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.PairKeyMultimapTest");
    }
}

