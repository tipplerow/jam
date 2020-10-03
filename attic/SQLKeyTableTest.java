
package jam.sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLKeyTableTest extends SQLTestBase {
    public SQLKeyTableTest() {
        super("data/test/sql_table_test.db");
    }

    @Test public void testAll() {
        TestTable table = new TestTable(db);

        assertFalse(table.exists());
        table.require();
        assertTrue(table.exists());

        assertFalse(table.contains(key1));
        assertFalse(table.contains(key2));
        assertTrue(table.load().isEmpty());
        
        table.store(List.of(rec1, rec2));
        
        assertTrue(table.contains(key1));
        assertEquals(rec1, table.fetch(key1));
        assertEquals(Set.of(key1, key2), table.loadKeys());

        Map<String, TestRecord> map = table.load();

        assertEquals(2, map.size());
        assertEquals(rec1, map.get(key1));
        assertEquals(rec2, map.get(key2));

        assertEquals(rec1, table.fetch(key1));
        assertEquals(rec2, table.fetch(key2));
        assertEquals(List.of(rec2, rec1), table.fetch(List.of(key2, key1)));

        table.update(List.of(rec1U, rec2U));
        assertEquals(List.of(rec1U, rec2U), table.fetch(List.of(key1, key2)));

        table.remove(key1);
        assertEquals(Set.of(key2), table.loadKeys());

        assertTrue(table.exists());
        assertFalse(table.contains(key1));
        assertNull(table.fetch(key1));

        table.store(rec3);
        assertTrue(table.contains(key3));
        assertEquals(List.of(rec2U, rec3), table.fetch(List.of(key2, key3)));
        assertEquals(Set.of(key2, key3), table.loadKeys());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLKeyTableTest");
    }
}
