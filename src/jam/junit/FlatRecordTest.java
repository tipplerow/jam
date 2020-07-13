
package jam.junit;

import java.util.List;

import jam.flat.FlatRecord;
import jam.lang.ObjectUtil;
import jam.math.DoubleComparator;

import org.junit.*;
import static org.junit.Assert.*;

public class FlatRecordTest {
    private static class TestRecord implements FlatRecord<String> {
        public final String key;
        public final int    val1;
        public final double val2;

        public TestRecord(String key, int val1, double val2) {
            this.key = key;
            this.val1 = val1;
            this.val2 = val2;
        }

        public static TestRecord parse(String line) {
            String[] fields = FlatRecord.split(line, 3);

            return new TestRecord(FlatRecord.parseString(fields[0]),
                                  FlatRecord.parseInt(fields[1]),
                                  FlatRecord.parseDouble(fields[2]));
        }

        @Override public List<String> formatFields() {
            return List.of(format(key), format(val1), format(val2));
        }

        @Override public String getPrimaryKey() {
            return key;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
        }

        private boolean equalsRecord(TestRecord that) {
            return ObjectUtil.equals(this.key, that.key)
                && this.val1 == that.val1
                && DoubleComparator.DEFAULT.EQ(this.val2, that.val2);
        }
    }

    @Test public void testFormat() {
        TestRecord rec1 = new TestRecord("abc", 1, 1.0);
        TestRecord rec2 = new TestRecord("def", 2, Double.NaN);
        TestRecord rec3 = new TestRecord(null, 3, 3.0);

        assertEquals("abc|1|1.0", rec1.format());
        assertEquals("def|2|(null)", rec2.format());
        assertEquals("(null)|3|3.0", rec3.format());
    }

    @Test public void testParse() {
        TestRecord rec1 = new TestRecord("abc", 1, 1.0);
        TestRecord rec2 = new TestRecord("def", 2, Double.NaN);
        TestRecord rec3 = new TestRecord(null, 3, 3.0);

        assertEquals(rec1, TestRecord.parse("abc|1|1.0"));
        assertEquals(rec2, TestRecord.parse("def|2|(null)"));
        assertEquals(rec3, TestRecord.parse("(null)|3|3.0"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FlatRecordTest");
    }
}
