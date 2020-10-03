
package jam.sql;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLCacheTest extends SQLTestBase {
    public SQLCacheTest() {
        super("data/test/sql_cache_test.db");
    }

    @Test public void testAll() {
        TestTable table = new TestTable(db);
        TestCache cache = new TestCache(table);

        assertNull(cache.fetch(key1));
        assertNull(cache.fetch(key3));

        // Compute on demand and cache...
        assertEquals(List.of(rec1, rec3), cache.require(List.of(key1, key3)));
        assertEquals(List.of(rec1, rec3), cache.require(List.of(key1, key3)));
        assertEquals(List.of(rec1, rec3), cache.require(List.of(key1, key3)));

        // Cache and database should contain the records...
        assertEquals(rec1, cache.fetch(key1));
        assertEquals(rec3, cache.fetch(key3));

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec3, table.fetch(key3));

        assertTrue(cache.contains(key1));
        assertTrue(cache.contains(key3));
        assertFalse(cache.contains(key2));
        assertFalse(cache.contains(key4));
        assertFalse(cache.contains(key5));

        assertTrue(table.contains(key1));
        assertTrue(table.contains(key3));
        assertFalse(table.contains(key2));
        assertFalse(table.contains(key4));
        assertFalse(table.contains(key5));

        assertTrue(cache.contains(rec1));
        assertTrue(cache.contains(rec3));
        assertFalse(cache.contains(rec2));
        assertFalse(cache.contains(rec4));
        assertFalse(cache.contains(rec5));

        // Add directly to the cache (and database)...
        assertFalse(cache.add(rec1));
        assertFalse(cache.add(rec3));
        assertTrue(cache.add(rec2));

        assertTrue(table.contains(key2));
        assertTrue(cache.contains(key2));
        assertTrue(cache.contains(rec2));

        assertEquals(rec2, cache.fetch(key2));
        assertEquals(rec2, table.fetch(key2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLCacheTest");
    }
}
