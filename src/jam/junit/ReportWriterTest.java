
package jam.junit;

import java.io.File;
import java.util.List;

import jam.io.IOUtil;
import jam.report.ReportRecord;
import jam.report.ReportWriter;

import org.junit.*;
import static org.junit.Assert.*;

class TestRecord implements ReportRecord {
    public final String key;
    public final int value;

    public TestRecord(String key, int value) {
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

public class ReportWriterTest {
    @Test public void testWriter() {
        File workDir = new File("__workdir__");
        File rptFile = new File(workDir, TestRecord.BASE_NAME);

        assertFalse(workDir.exists());
        assertFalse(rptFile.exists());

        // Verify that the report directory is created on demand...
        ReportWriter<TestRecord> writer = ReportWriter.create(workDir);

        assertTrue(workDir.isDirectory());
        assertFalse(rptFile.exists());

        TestRecord rec1 = new TestRecord("abc", 1);
        TestRecord rec2 = new TestRecord("def", 2);

        // Verify that the file is created on the first "write" call...
        writer.write(rec1);
        assertTrue(rptFile.isFile());

        writer.write(rec2);
        writer.close();

        List<String> lines = IOUtil.readLines(rptFile);
        assertEquals(3, lines.size());
        assertEquals("key,value", lines.get(0));
        assertEquals("abc,1", lines.get(1));
        assertEquals("def,2", lines.get(2));

        assertTrue(rptFile.delete());
        assertTrue(workDir.delete());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ReportWriterTest");
    }
}
