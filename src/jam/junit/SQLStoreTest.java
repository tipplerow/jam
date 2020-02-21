
package jam.junit;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import jam.sql.SQLDb;
import jam.sql.SQLiteDb;
import jam.sql.SQLStore;
import jam.sql.SQLTable;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLStoreTest {
    private static final String key1 = "key1";
    private static final String key2 = "key2";
    private static final String key3 = "key3";

    private static final TestRecord rec1 = new TestRecord(key1, 11);
    private static final TestRecord rec2 = new TestRecord(key2, 22);
    private static final TestRecord rec3 = new TestRecord(key3, 4); // string length

    private static final String FILE_NAME = "data/test/sql_store_test.db";
    private static final SQLDb DB = SQLiteDb.instance(FILE_NAME);

    @Before public void setUp() {
        deleteDbFile();
    }

    @AfterClass public static void tearDownClass() {
        deleteDbFile();
    }

    private static void deleteDbFile() {
        File dbFile = new File(FILE_NAME);

        if (dbFile.exists())
            dbFile.delete();
    }

    @Test public void testGet() {
        TestTable table = new TestTable(DB);
        TestStore store = new TestStore(table);

        assertFalse(DB.tableExists(table.getTableName()));
        assertTrue(table.load().isEmpty());

        table.store(List.of(rec1, rec2));

        assertTrue(DB.tableExists(table.getTableName()));
        assertRecords(List.of(rec1, rec2), table.load());

        // Retrieve pre-computed records...
        assertRecords(List.of(rec1, rec2), store.hash(List.of(key1, key2)));

        // Compute on demand and store...
        assertRecords(List.of(rec1, rec2, rec3), store.hash(List.of(key1, key2, key3)));
        assertRecords(List.of(rec1, rec2, rec3), table.load());
    }

    private void assertRecords(List<TestRecord> expected, Map<String, TestRecord> actual) {
        assertEquals(expected.size(), actual.size());

        for (TestRecord record : expected)
            assertEquals(record, actual.get(record.key));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLStoreTest");
    }

    private static final class TestRecord {
        public final String key;
        public final int    value;

        public TestRecord(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
        }

        private boolean equalsRecord(TestRecord that) {
            return this.key.equals(that.key) && this.value == that.value;
        }
    }

    private static final class TestTable extends SQLTable<String, TestRecord> {
        public TestTable(SQLDb db) {
            super(db);
        }

        @Override public List<String> getColumnNames() {
            return List.of("key", "value");
        }

        @Override public String getKey(TestRecord record) {
            return record.key;
        }

        @Override public TestRecord getRow(ResultSet resultSet) throws SQLException {
            return new TestRecord(resultSet.getString(1), resultSet.getInt(2));
        }

        @Override public String getTableName() {
            return "test_table";
        }

        @Override public String getTableSchema() {
            return "key string PRIMARY KEY, value int";
        }

        @Override public void prepareInsertStatement(PreparedStatement statement, TestRecord record) throws SQLException {
            statement.setString(1, record.key);
            statement.setInt(2, record.value);
        }
    }

    private static final class TestStore extends SQLStore<String, TestRecord> {
        public TestStore(TestTable table) {
            super(table);
        }

        @Override protected TestRecord compute(String key) {
            return new TestRecord(key, key.length());
        }
    }
}
