
package jam.sql;

import java.sql.SQLException;
import java.util.List;

import jam.app.JamEnv;
import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public final class SQLTableTest {
    SQLDb postgres = PostgreSQLDb.test();

    @Test public void testBulkCopy() throws SQLException {
        if (PostgreSQLDb.isInstalled())
            runBulkCopyTest(PostgreSQLDb.test());
    }

    private void runBulkCopyTest(SQLDb db) throws SQLException {
        TestTable table = new TestTable(db, "test_bulk_copy");
        table.delete();
        
        String fileName   = FileUtil.join(JamEnv.getRequired("JAM_HOME"), "data", "test", "bulk_insert.psv");
        char   delimiter  = BulkRecord.DELIMITER_CHAR;
        String nullString = BulkRecord.NULL_STRING;

        table.copy(fileName, delimiter, nullString);
        List<TestRecord> records = table.select();

        assertEquals(List.of(TestRecord.REC1,
                             TestRecord.REC2,
                             TestRecord.REC3,
                             TestRecord.REC4,
                             TestRecord.REC5), records);
        table.drop();
    }

    @Test public void testDelete() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_delete");

        TestRecord rec1 = TestRecord.REC1;
        TestRecord rec2 = TestRecord.REC2;
        TestRecord rec3 = TestRecord.REC3;
        TestRecord rec4 = TestRecord.REC4;
        TestRecord rec5 = TestRecord.REC5;

        String key1 = rec1.key;
        String key2 = rec2.key;
        String key3 = rec3.key;
        String key4 = rec4.key;
        String key5 = rec5.key;

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

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select());
        assertEquals(List.of(rec1, rec3, rec5), table.select(List.of(key1, key3, key5)));

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

        assertEquals(List.of(rec1, rec2, rec5), table.select());
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

        assertEquals(List.of(), table.select());
        assertEquals(List.of(), table.select(List.of(key1, key3, key5)));
        table.drop();
    }

    @Test public void testInsert() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_insert");

        TestRecord rec1 = TestRecord.REC1;
        TestRecord rec2 = TestRecord.REC2;
        TestRecord rec3 = TestRecord.REC3;
        TestRecord rec4 = TestRecord.REC4;
        TestRecord rec5 = TestRecord.REC5;

        String key1 = rec1.key;
        String key2 = rec2.key;
        String key3 = rec3.key;
        String key4 = rec4.key;
        String key5 = rec5.key;

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

        assertEquals(List.of(), table.select());
        assertEquals(List.of(), table.select(List.of(key1, key3, key5)));

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

        assertEquals(List.of(rec1, rec2), table.select());
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

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select());
        assertEquals(List.of(rec1, rec3, rec5), table.select(List.of(key1, key3, key5)));
        table.drop();
    }

    @Test public void testUpdate() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_update");

        TestRecord rec1 = TestRecord.REC1;
        TestRecord rec2 = TestRecord.REC2;
        TestRecord rec3 = TestRecord.REC3;
        TestRecord rec4 = TestRecord.REC4;
        TestRecord rec5 = TestRecord.REC5;

        TestRecord rec1B = new TestRecord(rec1.key, 11, 11.11, null, null);
        TestRecord rec3B = new TestRecord(rec3.key, 33, 33.33, null, null);
        TestRecord rec4B = new TestRecord(rec4.key, 44, 44.44, null, null);

        String key1 = rec1.key;
        String key2 = rec2.key;
        String key3 = rec3.key;
        String key4 = rec4.key;
        String key5 = rec5.key;

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

        assertEquals(List.of(), table.select());
        assertEquals(List.of(), table.select(List.of(key1, key3, key5)));

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

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), table.select());
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
        table.drop();
    }

    @Test public void testUpsert() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_upsert");

        TestRecord rec1 = TestRecord.REC1;
        TestRecord rec2 = TestRecord.REC2;
        TestRecord rec3 = TestRecord.REC3;
        TestRecord rec4 = TestRecord.REC4;
        TestRecord rec5 = TestRecord.REC5;

        TestRecord rec1B = new TestRecord(rec1.key, 11, 11.11, null, null);
        TestRecord rec3B = new TestRecord(rec3.key, 33, 33.33, null, null);
        TestRecord rec4B = new TestRecord(rec4.key, 44, 44.44, null, null);

        String key1 = rec1.key;
        String key2 = rec2.key;
        String key3 = rec3.key;
        String key4 = rec4.key;
        String key5 = rec5.key;

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

        assertEquals(List.of(), table.select());
        assertEquals(List.of(), table.select(List.of(key1, key2, key3, key4, key5)));

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
        table.drop();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLTableTest");
    }
}
