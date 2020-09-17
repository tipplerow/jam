
package jam.sql;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jam.math.DoubleComparator;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class SQLTestBase {
    protected final SQLDb db;
    protected final String dbFile;

    protected SQLTestBase(String dbFile) {
        this.db = SQLiteDb.instance(dbFile);
        this.dbFile = dbFile;
    }
    
    public static final String key1 = "k1";
    public static final String key2 = "key2";
    public static final String key3 = "key3";
    public static final String key4 = "key4";
    public static final String key5 = "key5";

    public static final TestRecord rec1 = new TestRecord(key1, 4.0, 2);
    public static final TestRecord rec2 = new TestRecord(key2, Double.NaN, -1);
    public static final TestRecord rec3 = new TestRecord(key3, 16.0, 4);
    public static final TestRecord rec4 = new TestRecord(key4, 25.0, 5);
    public static final TestRecord rec5 = new TestRecord(key5, 36.0, 6);

    public static final TestRecord rec1U = new TestRecord(key1, 4.1, 21);
    public static final TestRecord rec2U = new TestRecord(key2, 8.2, 42);

    @Before public void setUp() {
        deleteDbFile();
    }

    @After public void tearDown() {
        deleteDbFile();
    }

    public void deleteDbFile() {
        File dbFileObj = new File(dbFile);

        if (dbFileObj.exists())
            dbFileObj.delete();
    }

    public void assertRecords(List<TestRecord> expected, Map<String, TestRecord> actual) {
        assertEquals(expected.size(), actual.size());

        for (TestRecord record : expected)
            assertEquals(record, actual.get(record.key));
    }

    public static final class TestRecord {
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

    public static final class TestTable extends SQLKeyTable<String, TestRecord> {
        public TestTable(SQLDb db) {
            super(db);
        }

        private static SQLColumn KEY_COLUMN = SQLColumn.create("key", "string").primaryKey();
        private static SQLColumn FOO_COLUMN = SQLColumn.create("foo", "double");
        private static SQLColumn BAR_COLUMN = SQLColumn.create("bar", "int");

        @Override public List<SQLColumn> getColumns() {
            return List.of(KEY_COLUMN, FOO_COLUMN, BAR_COLUMN);
        }

        @Override public String getKey(TestRecord record) {
            return record.key;
        }

        @Override public String getKey(ResultSet resultSet, String columnLabel) throws SQLException {
            return resultSet.getString(columnLabel);
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

        @Override public void prepareColumn(PreparedStatement statement, int index,
                                            TestRecord record, String columnName) throws SQLException {
            switch (columnName) {
            case "key":
                statement.setString(index, record.key);
                break;

            case "foo":
                statement.setDouble(index, record.foo);
                break;

            case "bar":
                statement.setInt(index, record.bar);
                break;

            default:
                throw invalidColumn(columnName);
            }
        }

        @Override public void prepareKey(PreparedStatement statement, int index, String key) throws SQLException {
            statement.setString(index, key);
        }
    }

    public static final class TestStore extends SQLStore<String, TestRecord> {
        public TestStore(TestTable table) {
            super(table);
        }

        @Override protected TestRecord compute(String key) {
            int keylen = key.length();
            return new TestRecord(key, keylen * keylen, keylen);
        }
    }

    public static final class TestCache extends SQLCache<String, TestRecord> {
        public TestCache(TestTable table) {
            super(table);
        }

        @Override protected TestRecord compute(String key) {
            int keylen = key.length();
            return new TestRecord(key, keylen * keylen, keylen);
        }

        @Override public Class getKeyClass() {
            return String.class;
        }

        @Override public String getName() {
            return "test_cache";
        }

        @Override public Class getRecordClass() {
            return TestRecord.class;
        }
    }
}