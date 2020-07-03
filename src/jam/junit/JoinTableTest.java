
package jam.junit;

import java.io.File;
import java.util.List;

import jam.flat.FlatRecord;
import jam.flat.JoinRecord;
import jam.flat.JoinTable;
import jam.util.IterableUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class JoinTableTest {
    private static class TestRecord implements JoinRecord<String, String> {
        public final String primary;
        public final String foreign;
        public final int value;

        public TestRecord(String primary, String foreign, int value) {
            this.primary = primary;
            this.foreign = foreign;
            this.value = value;
        }

        public static TestRecord parse(String line) {
            String[] fields = FlatRecord.split(line, 3);
            return new TestRecord(fields[0], fields[1], Integer.parseInt(fields[2]));
        }

        @Override public List<String> formatFields() {
            return List.of(format(primary), format(foreign), format(value));
        }

        @Override public String getPrimaryKey() {
            return primary;
        }

        @Override public String getForeignKey() {
            return foreign;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
        }

        private boolean equalsRecord(TestRecord that) {
            return this.primary.equals(that.primary)
                && this.foreign.equals(that.foreign)
                && this.value == that.value;
        }

        @Override public String toString() {
            return String.format("TestRecord(%s, %s, %d)", primary, foreign, value);
        }
    }

    private static class TestTable extends JoinTable<String, String, TestRecord> {
        public static TestTable load(File file) {
            TestTable table = new TestTable();
            table.parse(file);
            return table;
        }

        public void insert(String primary, String foreign, int value) {
            insert(new TestRecord(primary, foreign, value));
        }

        @Override public TestRecord parse(String line) {
            return TestRecord.parse(line);
        }
    }
    /*
    @Test public void testFile() {
        File file = new File("data/test/__join_table_test.psv");
        file.deleteOnExit();

        TestTable table = new TestTable();

        table.insert("A", "alpha", 11);
        table.insert("A", "beta",  12);
        table.insert("B", "alpha", 21);
        table.insert("B", "beta",  22);
        table.insert("B", "gamma", 23);
        table.insert("C", "delta", 34);

        table.store(file);
        assertEquals(table, TestTable.load(file));
    }
    */
    @Test public void testInsertSelectDelete() {
        TestTable table = new TestTable();

        assertEquals(0, table.count());
        assertFalse(table.containsPrimary("A"));
        assertFalse(table.containsForeign("alpha"));
        assertTrue(table.selectPrimary("A").isEmpty());
        assertTrue(table.selectForeign("alpha").isEmpty());

        table.insert("A", "alpha", 11);
        table.insert("A", "beta",  12);
        table.insert("A", "alpha", 1111);
        table.insert("B", "alpha", 21);
        table.insert("B", "beta",  22);
        table.insert("B", "beta",  2222);
        table.insert("B", "gamma", 23);

        assertEquals(5, table.count());
        assertEquals(2, table.countPrimary("A"));
        assertEquals(3, table.countPrimary("B"));
        assertEquals(2, table.countForeign("alpha"));
        assertEquals(2, table.countForeign("beta"));
        assertEquals(1, table.countForeign("gamma"));

        assertNull(table.select("A", "rho"));
        assertNull(table.select("E", "alpha"));
        assertNull(table.select("E", "rho"));

        assertTrue(table.selectPrimary("E").isEmpty());
        assertTrue(table.selectForeign("rho").isEmpty());

        TestRecord rec1 = new TestRecord("A", "alpha", 1111);
        TestRecord rec2 = new TestRecord("A", "beta", 12);
        TestRecord rec3 = new TestRecord("B", "alpha", 21);
        TestRecord rec4 = new TestRecord("B", "beta", 2222);
        TestRecord rec5 = new TestRecord("B", "gamma", 23);

        assertEquals(rec1, table.select("A", "alpha"));
        assertEquals(rec2, table.select("A", "beta"));
        assertEquals(rec3, table.select("B", "alpha"));
        assertEquals(rec4, table.select("B", "beta"));
        assertEquals(rec5, table.select("B", "gamma"));

        assertTrue(IterableUtil.equals(List.of(rec1, rec2), table.selectPrimary("A")));
        assertTrue(IterableUtil.equals(List.of(rec3, rec4, rec5), table.selectPrimary("B")));

        assertTrue(IterableUtil.equals(List.of(rec1, rec3), table.selectForeign("alpha")));
        assertTrue(IterableUtil.equals(List.of(rec2, rec4), table.selectForeign("beta")));
        assertTrue(IterableUtil.equals(List.of(rec5), table.selectForeign("gamma")));

        table.delete("A", "rho");
        table.delete("E", "beta");
        table.delete("E", "rho");

        assertEquals(5, table.count());
        assertEquals(2, table.countPrimary("A"));

        table.delete("A", "beta");

        assertEquals(4, table.count());
        assertEquals(1, table.countPrimary("A"));

        table.delete("A", "alpha");

        assertEquals(3, table.count());
        assertEquals(0, table.countPrimary("A"));

        table.insert(rec2);
        table.insert(rec1);

        assertEquals(List.of(rec3, rec4, rec5, rec2, rec1), table.list());

        table.deletePrimary("foo");
        assertEquals(List.of(rec3, rec4, rec5, rec2, rec1), table.list());

        table.deletePrimary("B");
        assertEquals(List.of(rec2, rec1), table.list());

        table.deleteForeign("foo");
        assertEquals(List.of(rec2, rec1), table.list());
        
        table.insert("def", "beta", 32);
        table.deleteForeign("beta");

        assertEquals(List.of(rec1), table.list());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JoinTableTest");
    }
}
