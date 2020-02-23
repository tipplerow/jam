
package jam.junit;

import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLTableTest extends SQLTestBase {
    public SQLTableTest() {
        super("data/test/sql_table_test.db");
    }

    @Test public void testLoadStore() {
        TestTable table = new TestTable(db);

        assertFalse(table.exists());
        assertFalse(table.contains(key1));
        assertNull(table.fetch(key1));
        assertTrue(table.load().isEmpty());

        table.store(List.of(rec1, rec2));

        assertTrue(table.exists());
        assertTrue(table.contains(key1));
        assertEquals(rec1, table.fetch(key1));

        Map<String, TestRecord> map = table.load();

        assertEquals(2, map.size());
        assertEquals(rec1, map.get(key1));
        assertEquals(rec2, map.get(key2));

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertEquals(List.of(rec2, rec1), table.fetch(List.of(key2, key1)));

        table.remove(key1);

        assertTrue(table.exists());
        assertFalse(table.contains(key1));
        assertNull(table.fetch(key1));

        table.store(rec3);
        assertTrue(table.contains(key3));
        assertEquals(List.of(rec2, rec3), table.fetch(List.of(key2, key3)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLTableTest");
    }
}
