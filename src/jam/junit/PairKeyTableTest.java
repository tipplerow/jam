
package jam.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jam.util.PairKeyTable;

import org.junit.*;
import static org.junit.Assert.*;

public final class PairKeyTableTest {
    private PairKeyTable<String, Integer, String> table;
    @Test public void testHash() {
        table = PairKeyTable.hash();
        runTest();
    }

    @Test public void testTree() {
        table = PairKeyTable.tree();
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
        testRemove();
    }

    private void fillTable() {
        table.put("abc",  1, "abc.1");
        table.put("abc", -2, "abc.2");
        table.put("def",  2, "def.2");
        table.put("def", -3, "def.3");
        table.put("def",  4, "def.4");
        table.put("ghi",  5, "ghi.5");
    }

    private void testContains() {
        assertTrue(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));

        assertTrue(table.contains("def", 2));
        assertFalse(table.contains("def", 1));
    }

    private void testGet() {
        assertEquals("abc.1", table.get("abc", 1));
        assertNull(table.get("abc", 2));

        assertEquals("def.2", table.get("def", 2));
        assertNull(table.get("def", 1));
    }

    private void testKeySets() {
        assertEquals(Set.of("abc", "def", "ghi"), table.outerKeySet());

        assertEquals(Set.of(5), table.innerKeySet("ghi"));
        assertEquals(Set.of(1, -2), table.innerKeySet("abc"));
        assertEquals(Set.of(2, -3, 4), table.innerKeySet("def"));
    }

    private void testRemove() {
        assertTrue(table.contains("ghi", 5));
        assertEquals("ghi.5", table.get("ghi", 5));
        assertEquals("ghi.5", table.remove("ghi", 5));

        assertFalse(table.contains("ghi", 5));
        assertNull(table.get("ghi", 5));
        assertNull("ghi.5", table.remove("ghi", 5));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PairKeyTableTest");
    }
}

