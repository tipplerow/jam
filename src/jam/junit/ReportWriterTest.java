
package jam.junit;

import java.io.File;
import java.util.List;

import jam.io.IOUtil;
import jam.report.ReportRecord;
import jam.report.ReportWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class ReportWriterTest {
    private static final class TestRecord implements ReportRecord {
        public final String key;
        public final int value;

        private TestRecord(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public static final String BASE_NAME = "report-writer-test.txt";

        @Override public String getBaseName() {
            return BASE_NAME;
        }

        @Override public String getHeaderLine() {
            return "key,value";
        }

        @Override public String formatLine() {
            return String.format("%s,%d", key, value);
        }
    }

    private static final File WORK_DIR = new File("__workdir__");
    private static final File RPT_FILE = new File(WORK_DIR, TestRecord.BASE_NAME);

    private static final TestRecord rec1 = new TestRecord("abc", 1);
    private static final TestRecord rec2 = new TestRecord("def", 2);
    private static final TestRecord rec3 = new TestRecord("ghi", 3);

    @Test public void testStaticWrite() {
        assertFalse(WORK_DIR.exists());
        assertFalse(RPT_FILE.exists());

        ReportWriter.write(WORK_DIR, List.of(rec1, rec2, rec3));
        assertReportFile();
    }

    @Test public void testWriter() {
        assertFalse(WORK_DIR.exists());
        assertFalse(RPT_FILE.exists());

        // Verify that the report directory is created on demand...
        ReportWriter<TestRecord> writer = ReportWriter.create(WORK_DIR);

        assertTrue(WORK_DIR.isDirectory());
        assertFalse(RPT_FILE.exists());

        // Verify that the file is created on the first "write" call...
        writer.write(rec1);
        assertTrue(RPT_FILE.isFile());

        writer.write(rec2);
        writer.write(rec3);
        writer.close();

        assertReportFile();
    }

    private void assertReportFile() {
        assertEquals(List.of("key,value", "abc,1", "def,2", "ghi,3"), IOUtil.readLines(RPT_FILE));
        assertTrue(RPT_FILE.delete());
        assertTrue(WORK_DIR.delete());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ReportWriterTest");
    }
}
