
package jam.junit;

import java.util.ArrayList;
import java.util.HashSet;
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
        fillTable();
        assertEquals(List.of(-2, 1), new ArrayList<Integer>(table.viewInnerKeys("abc")));
        assertEquals(List.of(-3, 2, 4), new ArrayList<Integer>(table.viewInnerKeys("def")));
        assertEquals(List.of("abc", "def", "ghi"), new ArrayList<String>(table.viewOuterKeys()));
    }

    private void runTest() {
        fillTable();
        testContains();
        testGet();
        testKeySets();
        testRemove();
        testRemoveOuter();
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
        assertEquals(Set.of("abc", "def", "ghi"), table.viewOuterKeys());

        assertEquals(Set.of(5), table.viewInnerKeys("ghi"));
        assertEquals(Set.of(1, -2), table.viewInnerKeys("abc"));
        assertEquals(Set.of(2, -3, 4), table.viewInnerKeys("def"));
    }

    private void testRemove() {
        assertTrue(table.contains("ghi", 5));
        assertEquals("ghi.5", table.get("ghi", 5));
        assertEquals("ghi.5", table.remove("ghi", 5));

        assertFalse(table.contains("ghi", 5));
        assertNull(table.get("ghi", 5));
        assertNull("ghi.5", table.remove("ghi", 5));
    }

    private void testRemoveOuter() {
        fillTable();

        assertTrue(table.removeOuter("foo").isEmpty());

        assertEquals(6, table.size());
        assertTrue(table.contains("abc", 1));
        assertTrue(table.contains("abc", -2));

        assertEquals(Set.of("abc.1", "abc.2"), new HashSet<String>(table.removeOuter("abc")));

        assertEquals(4, table.size());
        assertFalse(table.contains("abc", 1));
        assertFalse(table.contains("abc", -2));

        assertTrue(table.contains("ghi", 5));

        assertEquals(Set.of("ghi.5"), new HashSet<String>(table.removeOuter("ghi")));

        assertEquals(3, table.size());
        assertFalse(table.contains("ghi", 5));
        /*
        table.put("abc",  1, "abc.1");
        table.put("abc", -2, "abc.2");
        table.put("def",  2, "def.2");
        table.put("def", -3, "def.3");
        table.put("def",  4, "def.4");
        table.put("ghi",  5, "ghi.5");
        */
    }

    @Test public void testSize() {
        table = PairKeyTable.tree();

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());

        table.put("def", 2, "def.2");

        assertEquals(1, table.size());
        assertFalse(table.isEmpty());

        table.put("abc", 1, "abc.1");

        assertEquals(2, table.size());
        assertFalse(table.isEmpty());

        table.put("abc", 3, "abc.3");

        assertEquals(3, table.size());
        assertFalse(table.isEmpty());

        table.put("abc", 1, "abc.1A");
        table.put("def", 2, "def.2A");

        assertEquals(3, table.size());
        assertFalse(table.isEmpty());

        table.remove("abc", 1);

        assertEquals(2, table.size());
        assertFalse(table.isEmpty());

        table.remove("abc", 1);
        table.remove("qed", 11);

        assertEquals(2, table.size());
        assertFalse(table.isEmpty());

        table.remove("def", 2);
        table.remove("def", 2);

        assertEquals(1, table.size());
        assertFalse(table.isEmpty());

        table.remove("abc", 3);

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());

        table.remove("foo", 88);

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }

    @Test public void testValues() {
        table = PairKeyTable.tree();
        assertEquals(List.of(), table.values());

        table.put("def", 2, "def.2");
        assertEquals(List.of("def.2"), table.values());

        table.put("abc", 1, "abc.1");
        assertEquals(List.of("abc.1", "def.2"), table.values());

        table.put("abc", 1, "abc.1A");
        assertEquals(List.of("abc.1A", "def.2"), table.values());

        table.put("abc", 3, "abc.3");
        assertEquals(List.of("abc.1A", "abc.3", "def.2"), table.values());

        table.remove("abc", 1);
        assertEquals(List.of("abc.3", "def.2"), table.values());

        table.remove("abc", 3);
        assertEquals(List.of("def.2"), table.values());

        table.remove("def", 2);
        assertEquals(List.of(), table.values());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PairKeyTableTest");
    }
}
