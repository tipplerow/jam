
package jam.junit;

import jam.sql.BulkRecord;
import jam.util.StringUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class BulkRecordTest {
    private static final BulkRecord record = new MyRecord();

    private static class MyRecord implements BulkRecord {
        @Override public String formatBulk() {
            return "";
        }
    }

    @Test public void testCleanField() {
        assertEquals("abc", record.cleanField("abc"));
        assertEquals(3, record.cleanField("abc").length());

        assertEquals("a\\|c", record.cleanField("a|c"));
        assertEquals(4, record.cleanField("a|c").length());

        String unclean = "a" + StringUtil.BACK_SLASH + "c";
        String cleaned = "a" + StringUtil.DOUBLE_BACK_SLASH + "c";

        assertEquals(cleaned, record.cleanField(unclean));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BulkRecordTest");
    }
}
