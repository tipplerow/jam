
package jam.junit;

import java.io.File;
import java.util.List;

import jam.io.LineReader;
import jam.io.TableWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class TableWriterTest {
    @Test public void testCSV() {
        runTest("data/test/table_writer.csv", ",");
    }

    @Test public void testPSV() {
        runTest("data/test/table_writer.psv", "|");
    }

    @Test public void testTSV() {
        runTest("data/test/table_writer.tsv", "\t");
    }

    @Test public void testTXT() {
        runTest("data/test/table_writer.txt", "\t");
    }

    private void runTest(String fileName, String delim) {
        assertEquals(delim, TableWriter.resolveDelim(fileName));

        File file = new File(fileName);
        TableWriter writer = TableWriter.open(file);

        writer.println("key1", "value1");
        writer.println("key2", "value2");
        writer.close();

        LineReader reader = LineReader.open(file);

        assertEquals("key1" + delim + "value1", reader.next());
        assertEquals("key2" + delim + "value2", reader.next());
        assertFalse(reader.hasNext());
        reader.close();

        file.delete();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TableWriterTest");
    }
}
