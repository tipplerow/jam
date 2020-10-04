
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class JamTableTestBase {
    public static final TestRecord rec1 = TestRecord.REC1;
    public static final TestRecord rec2 = TestRecord.REC2;
    public static final TestRecord rec3 = TestRecord.REC3;
    public static final TestRecord rec4 = TestRecord.REC4;
    public static final TestRecord rec5 = TestRecord.REC5;

    public static final TestRecord rec1B = new TestRecord(rec1.key, 11, 11.11, null, null);
    public static final TestRecord rec3B = new TestRecord(rec3.key, 33, 33.33, null, null);
    public static final TestRecord rec4B = new TestRecord(rec4.key, 44, 44.44, null, null);

    public static final String key1 = rec1.key;
    public static final String key2 = rec2.key;
    public static final String key3 = rec3.key;
    public static final String key4 = rec4.key;
    public static final String key5 = rec5.key;

    public static final List<String> keys = List.of(key1, key2, key3, key4, key5);

    private void assertSelect(JamTable<String, TestRecord> table, List<String> keys, List<TestRecord> expected) {
        //
        // The in-memory tables backed by maps will return collections rather than lists...
        //
        assertEquals(expected, new ArrayList<TestRecord>(table.select(keys)));
    }

    private void assertEqualCollections(Collection<TestRecord> expected, Collection<TestRecord> actual) {
        MapTable<String, TestRecord> expTable = MapTable.hash(expected, record -> record.key);
        MapTable<String, TestRecord> actTable = MapTable.hash(actual,   record -> record.key);

        assertTrue(expTable.equalsView(actTable));
    }

    public void runDeleteTest(JamTable<String, TestRecord> table) {
        // Start with a populated table...
        table.delete();
        table.insert(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertEquals(rec3, table.select(key3));
        assertEquals(rec4, table.select(key4));
        assertEquals(rec5, table.select(key5));

        assertEquals(List.of(rec1, rec3, rec5), table.select(List.of(key1, key3, key5)));
        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select(keys));

        // Delete two records...
        assertTrue(table.delete(key3));
        assertTrue(table.delete(key4));

        assertFalse(table.delete(key3));
        assertFalse(table.delete(key4));

        assertEquals(3, table.count());
        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertEquals(rec5, table.select(key5));

        assertEquals(List.of(rec1, rec2, rec5), table.select(keys));
        assertEquals(List.of(rec1, rec5), table.select(List.of(key1, key3, key5)));

        // Delete the remaining records...
        table.delete(List.of(key1, key2, key3, key4, key5));
        assertEquals(0, table.count());

        assertFalse(table.contains(key1));
        assertFalse(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertNull(table.select(key1));
        assertNull(table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertTrue(table.select().isEmpty());
        assertTrue(table.select(List.of(key1, key3, key5)).isEmpty());
    }

    public void runInsertTest(JamTable<String, TestRecord> table) {
        // Start with an empty table...
        table.delete();
        assertEquals(0, table.count());

        assertFalse(table.contains(key1));
        assertFalse(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertNull(table.select(key1));
        assertNull(table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertTrue(table.select().isEmpty());
        assertTrue(table.select(List.of(key1, key3, key5)).isEmpty());

        // Add the first two records...
        assertTrue(table.insert(rec1));
        assertTrue(table.insert(rec2));
        assertEquals(2, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertEquals(List.of(rec1, rec2), table.select(keys));
        assertEquals(List.of(rec1), table.select(List.of(key1, key3, key5)));

        // Add the remaining records...
        table.insert(List.of(rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertEquals(rec3, table.select(key3));
        assertEquals(rec4, table.select(key4));
        assertEquals(rec5, table.select(key5));

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select(keys));
        assertEquals(List.of(rec1, rec3, rec5), table.select(List.of(key1, key3, key5)));
    }

    public void runUpdateTest(JamTable<String, TestRecord> table) {
        // Start with an empty table...
        table.delete();
        assertEquals(0, table.count());

        assertFalse(table.contains(key1));
        assertFalse(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertNull(table.select(key1));
        assertNull(table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertTrue(table.select().isEmpty());
        assertTrue(table.select(List.of(key1, key3, key5)).isEmpty());

        // Update a missing record...
        assertFalse(table.update(rec1));
        assertFalse(table.update(rec1));
        assertEquals(0, table.count());

        // Add all records...
        table.insert(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertEquals(rec3, table.select(key3));
        assertEquals(rec4, table.select(key4));
        assertEquals(rec5, table.select(key5));

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select(keys));
        assertEquals(List.of(rec1, rec3, rec5), table.select(List.of(key1, key3, key5)));

        // Update with three new records...
        assertTrue(table.update(rec1B));
        table.update(List.of(rec3B, rec4B));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1B, table.select(key1));
        assertEquals(rec2,  table.select(key2));
        assertEquals(rec3B, table.select(key3));
        assertEquals(rec4B, table.select(key4));
        assertEquals(rec5,  table.select(key5));

        assertEquals(List.of(rec1B, rec2, rec3B, rec4B, rec5), table.select(List.of(key1, key2, key3, key4, key5)));
    }

    public void runUpsertTest(JamTable<String, TestRecord> table) {
        // Start with an empty table...
        table.delete();
        assertEquals(0, table.count());

        assertFalse(table.contains(key1));
        assertFalse(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertNull(table.select(key1));
        assertNull(table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertTrue(table.select().isEmpty());
        assertTrue(table.select(List.of(key1, key2, key3, key4, key5)).isEmpty());

        // Upsert two missing records...
        table.upsert(rec1);
        table.upsert(rec2);
        assertEquals(2, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertFalse(table.contains(key3));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertNull(table.select(key3));
        assertNull(table.select(key4));
        assertNull(table.select(key5));

        assertEquals(List.of(rec1, rec2), table.select(List.of(key1, key2, key3, key4, key5)));

        // Upsert the remaining original records...
        table.upsert(List.of(rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1, table.select(key1));
        assertEquals(rec2, table.select(key2));
        assertEquals(rec3, table.select(key3));
        assertEquals(rec4, table.select(key4));
        assertEquals(rec5, table.select(key5));

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select(List.of(key1, key2, key3, key4, key5)));

        // Upsert three new records...
        table.upsert(rec1B);
        table.upsert(List.of(rec3B, rec4B));
        assertEquals(5, table.count());

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key2));
        assertTrue(table.contains(key3));
        assertTrue(table.contains(key4));
        assertTrue(table.contains(key5));

        assertEquals(rec1B, table.select(key1));
        assertEquals(rec2,  table.select(key2));
        assertEquals(rec3B, table.select(key3));
        assertEquals(rec4B, table.select(key4));
        assertEquals(rec5,  table.select(key5));

        assertEquals(List.of(rec1B, rec2, rec3B, rec4B, rec5), table.select(List.of(key1, key2, key3, key4, key5)));
    }

    public void runSelectFilterTest(JamTable<String, TestRecord> table) {
        // Start with a populated table...
        table.delete();
        table.insert(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertEqualCollections(List.of(rec2, rec3, rec4), table.select(rec -> rec.dval > 2.0));
    }
}
