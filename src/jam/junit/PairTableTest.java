
package jam.junit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jam.util.PairTable;

import org.junit.*;
import static org.junit.Assert.*;

public final class PairTableTest {
    @Test public void testAdd() {
        PairTable<String, Integer> table = PairTable.create();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());

        assertFalse(table.containsFirst("abc"));
        assertFalse(table.containsFirst("def"));
        assertFalse(table.containsSecond(1));
        assertFalse(table.containsSecond(2));
        assertFalse(table.containsSecond(3));
        assertFalse(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));
        assertFalse(table.contains("def", 2));
        assertFalse(table.contains("def", 3));

        assertTrue(table.add("abc", 1));
        assertFalse(table.add("abc", 1));

        assertFalse(table.isEmpty());
        assertEquals(1, table.size());

        assertTrue(table.containsFirst("abc"));
        assertFalse(table.containsFirst("def"));
        assertTrue(table.containsSecond(1));
        assertFalse(table.containsSecond(2));
        assertFalse(table.containsSecond(3));
        assertTrue(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));
        assertFalse(table.contains("def", 2));
        assertFalse(table.contains("def", 3));

        assertTrue(table.add("abc", 2));
        assertFalse(table.add("abc", 2));

        assertFalse(table.isEmpty());
        assertEquals(2, table.size());

        assertTrue(table.containsFirst("abc"));
        assertFalse(table.containsFirst("def"));
        assertTrue(table.containsSecond(1));
        assertTrue(table.containsSecond(2));
        assertFalse(table.containsSecond(3));
        assertTrue(table.contains("abc", 1));
        assertTrue(table.contains("abc", 2));
        assertFalse(table.contains("def", 2));
        assertFalse(table.contains("def", 3));

        assertTrue(table.add("def", 2));
        assertFalse(table.add("def", 2));

        assertFalse(table.isEmpty());
        assertEquals(3, table.size());

        assertTrue(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsSecond(1));
        assertTrue(table.containsSecond(2));
        assertFalse(table.containsSecond(3));
        assertTrue(table.contains("abc", 1));
        assertTrue(table.contains("abc", 2));
        assertTrue(table.contains("def", 2));
        assertFalse(table.contains("def", 3));

        assertTrue(table.add("def", 3));
        assertFalse(table.add("def", 3));

        assertFalse(table.isEmpty());
        assertEquals(4, table.size());

        assertTrue(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsSecond(1));
        assertTrue(table.containsSecond(2));
        assertTrue(table.containsSecond(3));
        assertTrue(table.contains("abc", 1));
        assertTrue(table.contains("abc", 2));
        assertTrue(table.contains("def", 2));
        assertTrue(table.contains("def", 3));
    }

    @Test public void testFor() {
        PairTable<String, Integer> table = createTable();

        assertCollection(Set.of(1, 2, 3), table.forFirst("abc"));
        assertCollection(Set.of(3, 4),    table.forFirst("def"));
        assertCollection(Set.of(3, 4, 5), table.forFirst("ghi"));

        assertCollection(Set.of("abc"), table.forSecond(1));
        assertCollection(Set.of("abc"), table.forSecond(2));
        assertCollection(Set.of("abc", "def", "ghi"), table.forSecond(3));
        assertCollection(Set.of("def", "ghi"), table.forSecond(4));
        assertCollection(Set.of("ghi"), table.forSecond(5));
    }

    private PairTable<String, Integer> createTable() {
        PairTable<String, Integer> table = PairTable.create();

        table.add("abc", 1);
        table.add("abc", 2);
        table.add("abc", 3);
        table.add("def", 3);
        table.add("def", 4);
        table.add("ghi", 3);
        table.add("ghi", 4);
        table.add("ghi", 5);

        return table;
    }

    @Test public void testRemove() {
        PairTable<String, Integer> table = createTable();

        assertTrue(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsFirst("ghi"));

        assertTrue(table.containsSecond(1));
        assertTrue(table.containsSecond(2));
        assertTrue(table.containsSecond(3));
        assertTrue(table.containsSecond(4));
        assertTrue(table.containsSecond(5));

        assertTrue(table.contains("abc", 1));
        assertTrue(table.contains("abc", 2));
        assertTrue(table.contains("abc", 3));
        assertTrue(table.contains("def", 3));
        assertTrue(table.contains("def", 4));
        assertTrue(table.contains("ghi", 3));
        assertTrue(table.contains("ghi", 4));
        assertTrue(table.contains("ghi", 5));

        table.remove("abc", 1);

        assertTrue(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsFirst("ghi"));

        assertFalse(table.containsSecond(1));
        assertTrue(table.containsSecond(2));
        assertTrue(table.containsSecond(3));
        assertTrue(table.containsSecond(4));
        assertTrue(table.containsSecond(5));

        assertFalse(table.contains("abc", 1));
        assertTrue(table.contains("abc", 2));
        assertTrue(table.contains("abc", 3));
        assertTrue(table.contains("def", 3));
        assertTrue(table.contains("def", 4));
        assertTrue(table.contains("ghi", 3));
        assertTrue(table.contains("ghi", 4));
        assertTrue(table.contains("ghi", 5));

        table.removeFirst("abc");

        assertFalse(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsFirst("ghi"));

        assertFalse(table.containsSecond(1));
        assertFalse(table.containsSecond(2));
        assertTrue(table.containsSecond(3));
        assertTrue(table.containsSecond(4));
        assertTrue(table.containsSecond(5));

        assertFalse(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));
        assertFalse(table.contains("abc", 3));
        assertTrue(table.contains("def", 3));
        assertTrue(table.contains("def", 4));
        assertTrue(table.contains("ghi", 3));
        assertTrue(table.contains("ghi", 4));
        assertTrue(table.contains("ghi", 5));

        table.removeSecond(4);

        assertFalse(table.containsFirst("abc"));
        assertTrue(table.containsFirst("def"));
        assertTrue(table.containsFirst("ghi"));

        assertFalse(table.containsSecond(1));
        assertFalse(table.containsSecond(2));
        assertTrue(table.containsSecond(3));
        assertFalse(table.containsSecond(4));
        assertTrue(table.containsSecond(5));

        assertFalse(table.contains("abc", 1));
        assertFalse(table.contains("abc", 2));
        assertFalse(table.contains("abc", 3));
        assertTrue(table.contains("def", 3));
        assertFalse(table.contains("def", 4));
        assertTrue(table.contains("ghi", 3));
        assertFalse(table.contains("ghi", 4));
        assertTrue(table.contains("ghi", 5));
    }

    private static <V> void assertCollection(Set<V> expected, Collection<V> actual) {
        assertEquals(expected, new HashSet<V>(actual));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PairTableTest");
    }
}

