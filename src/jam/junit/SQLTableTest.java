
package jam.junit;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import jam.math.DoubleComparator;
import jam.sql.SQLDb;
import jam.sql.SQLiteDb;
import jam.sql.SQLTable;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLTableTest {
    private static final TestRecord rec1 = new TestRecord("key1", 1.0, 11);
    private static final TestRecord rec2 = new TestRecord("key2", Double.NaN, 22);
    private static final TestRecord rec3 = new TestRecord("key3", 3.0, 33);

    private static final String FILE_NAME = "data/test/sql_table_test.db";
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

    @Test public void testLoadStore() {
        TestTable table = new TestTable(DB);

        assertFalse(DB.tableExists(table.getTableName()));
        assertTrue(table.load().isEmpty());

        table.store(List.of(rec1, rec2));
        assertTrue(DB.tableExists(table.getTableName()));

        Map<String, TestRecord> map = table.load();

        assertEquals(2, map.size());
        assertEquals(rec1, map.get("key1"));
        assertEquals(rec2, map.get("key2"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLTableTest");
    }

    private static final class TestRecord {
        public final String key;
        public final double foo;
        public final int    bar;

        public TestRecord(String key, double foo, int bar) {
            this.key = key;
            this.foo = foo;
            this.bar = bar;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
        }

        private boolean equalsRecord(TestRecord that) {
            return this.key.equals(that.key)
                && DoubleComparator.DEFAULT.equals(this.foo, that.foo)
                && this.bar == that.bar;
        }

        @Override public String toString() {
            return String.format("TestRecord(%s, %f, %d)", key, foo, bar);
        }
    }

    private static final class TestTable extends SQLTable<String, TestRecord> {
        public TestTable(SQLDb db) {
            super(db);
        }

        @Override public List<String> getColumnNames() {
            return List.of("key", "foo", "bar");
        }

        @Override public String getKey(TestRecord record) {
            return record.key;
        }

        @Override public TestRecord getRow(ResultSet resultSet) throws SQLException {
            String key = resultSet.getString(1);
            double foo = getDouble(resultSet,2);
            int    bar = resultSet.getInt(3);

            return new TestRecord(key, foo, bar);
        }

        @Override public String getTableName() {
            return "test_table";
        }

        @Override public String getTableSchema() {
            return "key string PRIMARY KEY, foo double, bar int";
        }

        @Override public void prepareInsertStatement(PreparedStatement statement, TestRecord record) throws SQLException {
            statement.setString(1, record.key);
            statement.setDouble(2, record.foo);
            statement.setInt(3, record.bar);
        }
    }
}
