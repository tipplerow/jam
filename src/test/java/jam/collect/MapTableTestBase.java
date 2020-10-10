
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class MapTableTestBase {
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

    private void assertFetch(MapTable<String, TestRecord> table, List<String> keys, List<TestRecord> expected) {
        //
        // The in-memory tables backed by maps will return collections rather than lists...
        //
        assertEquals(expected, new ArrayList<TestRecord>(table.fetch(keys)));
    }

    private void assertEqualCollections(Collection<TestRecord> expected, Collection<TestRecord> actual) {
        MapTable<String, TestRecord> expTable = MapTable.hash(expected, record -> record.key);
        MapTable<String, TestRecord> actTable = MapTable.hash(actual,   record -> record.key);

        assertTrue(expTable.equalsView(actTable));
    }

    public void runDeleteTest(MapTable<String, TestRecord> table) {
        // Start with a populated table...
        table.delete();
        table.store(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertEquals(rec3, table.fetch(key3));
        assertEquals(rec4, table.fetch(key4));
        assertEquals(rec5, table.fetch(key5));

        assertEquals(List.of(rec1, rec3, rec5), table.fetch(List.of(key1, key3, key5)));
        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.fetch(keys));

        // Delete two records...
        assertTrue(table.delete(rec3));
        assertTrue(table.delete(rec4));

        assertFalse(table.delete(rec3));
        assertFalse(table.delete(rec4));

        assertEquals(3, table.count());

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertNull(table.fetch(key3));
        assertNull(table.fetch(key4));
        assertEquals(rec5, table.fetch(key5));

        assertEquals(List.of(rec1, rec2, rec5), table.fetch(keys));
        assertEquals(List.of(rec1, rec5), table.fetch(List.of(key1, key3, key5)));

        // Delete the remaining records...
        table.delete(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(0, table.count());

        assertNull(table.fetch(key1));
        assertNull(table.fetch(key2));
        assertNull(table.fetch(key3));
        assertNull(table.fetch(key4));
        assertNull(table.fetch(key5));

        assertTrue(table.fetch().isEmpty());
        assertTrue(table.fetch(List.of(key1, key3, key5)).isEmpty());
    }

    public void runStoreTest(MapTable<String, TestRecord> table) {
        // Start with an empty table...
        table.delete();
        assertEquals(0, table.count());

        assertNull(table.fetch(key1));
        assertNull(table.fetch(key2));
        assertNull(table.fetch(key3));
        assertNull(table.fetch(key4));
        assertNull(table.fetch(key5));

        assertTrue(table.fetch().isEmpty());
        assertTrue(table.fetch(List.of(key1, key2, key3, key4, key5)).isEmpty());

        // Store two missing records...
        table.store(rec1);
        table.store(rec2);
        assertEquals(2, table.count());

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertNull(table.fetch(key3));
        assertNull(table.fetch(key4));
        assertNull(table.fetch(key5));

        assertEquals(List.of(rec1, rec2), table.fetch(List.of(key1, key2, key3, key4, key5)));

        // Store the remaining original records...
        table.store(List.of(rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertEquals(rec3, table.fetch(key3));
        assertEquals(rec4, table.fetch(key4));
        assertEquals(rec5, table.fetch(key5));

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.fetch(List.of(key1, key2, key3, key4, key5)));

        // Store three new records...
        table.store(rec1B);
        table.store(List.of(rec3B, rec4B));
        assertEquals(5, table.count());

        assertEquals(rec1B, table.fetch(key1));
        assertEquals(rec2,  table.fetch(key2));
        assertEquals(rec3B, table.fetch(key3));
        assertEquals(rec4B, table.fetch(key4));
        assertEquals(rec5,  table.fetch(key5));

        assertEquals(List.of(rec1B, rec2, rec3B, rec4B, rec5), table.fetch(List.of(key1, key2, key3, key4, key5)));
    }

    public void runFetchFilterTest(MapTable<String, TestRecord> table) {
        // Start with a populated table...
        table.delete();
        table.store(List.of(rec1, rec2, rec3, rec4, rec5));
        assertEquals(5, table.count());

        assertEqualCollections(List.of(rec2, rec3, rec4), table.fetch(rec -> rec.dval > 2.0));
    }
}
