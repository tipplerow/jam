
package jam.junit;

import java.io.File;
import java.util.List;

import jam.flat.FlatRecord;
import jam.flat.FlatTable;

import org.junit.*;
import static org.junit.Assert.*;

public class FlatTableTest {
    private static class TestRecord implements FlatRecord<String> {
        public final String key;
        public final int value;

        public TestRecord(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public static TestRecord parse(String line) {
            String[] fields = FlatRecord.split(line, 2);
            return new TestRecord(fields[0], Integer.parseInt(fields[1]));
        }

        @Override public List<String> formatFields() {
            return List.of(format(key), format(value));
        }

        @Override public String getPrimaryKey() {
            return key;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
        }

        private boolean equalsRecord(TestRecord that) {
            return this.key.equals(that.key) && this.value == that.value;
        }

        @Override public String toString() {
            return String.format("TestRecord(%s, %d)", key, value);
        }
    }

    private static class TestTable extends FlatTable<String, TestRecord> {
        public static TestTable load(File file) {
            TestTable table = new TestTable();
            table.parse(file);
            return table;
        }

        public void insert(String key, int value) {
            insert(new TestRecord(key, value));
        }

        @Override public TestRecord parse(String line) {
            return TestRecord.parse(line);
        }
    }

    @Test public void testFile() {
        File file = new File("data/test/__flat_table_test.psv");
        file.deleteOnExit();

        TestTable table = new TestTable();

        table.insert("abc", 1);
        table.insert("def", 2);
        table.insert("ghi", 3);

        table.store(file);
        assertEquals(table, TestTable.load(file));
    }

    @Test public void testInsertSelectDelete() {
        TestTable table = new TestTable();

        assertEquals(0, table.count());
        assertFalse(table.contains("abc"));
        assertNull(table.select("abc"));

        TestRecord rec11 = new TestRecord("abc", 1);
        TestRecord rec13 = new TestRecord("abc", 3);
        TestRecord rec22 = new TestRecord("def", 2);

        table.insert("abc", 1);
        table.insert("def", 2);
        table.insert("abc", 3);

        assertEquals(2, table.count());

        assertTrue(table.contains("abc"));
        assertTrue(table.contains("def"));
        assertFalse(table.contains("ghi"));

        assertEquals(rec13, table.select("abc"));
        assertEquals(rec22, table.select("def"));

        assertEquals(List.of(rec13, rec22), table.list());

        assertEquals(List.of(rec22, rec13), table.select(List.of("foo", "def", "bar", "abc", "ghi")));

        table.delete("abc");

        assertEquals(1, table.count());

        assertFalse(table.contains("abc"));
        assertTrue(table.contains("def"));
        assertFalse(table.contains("ghi"));

        assertEquals(List.of(rec22), table.list());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FlatTableTest");
    }
}
