
package jam.junit;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLCacheTest extends SQLTestBase {
    public SQLCacheTest() {
        super("data/test/sql_cache_test.db");
    }

    @Test public void testGet() {
        TestTable table = new TestTable(db);
        TestStore store = new TestStore(table);
        TestCache cache = new TestCache(store);

        // Database should be empty...
        assertFalse(table.exists());

        // Compute on demand and cache...
        assertEquals(List.of(rec1, rec3), cache.get(List.of(key1, key3)));
        assertEquals(List.of(rec1, rec3), cache.get(List.of(key1, key3)));

        // Database should contain the records...
        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec3, table.fetch(key3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLCacheTest");
    }
}
