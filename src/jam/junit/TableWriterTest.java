
package jam.junit;

import java.io.File;

import jam.io.Delimiter;
import jam.io.LineReader;
import jam.io.TableWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class TableWriterTest {
    @Test public void testCSV() {
        runTest("data/test/table_writer.csv", Delimiter.COMMA);
    }

    @Test public void testPSV() {
        runTest("data/test/table_writer.psv", Delimiter.PIPE);
    }

    @Test public void testTSV() {
        runTest("data/test/table_writer.tsv", Delimiter.TAB);
    }

    @Test public void testTXT() {
        runTest("data/test/table_writer.txt", Delimiter.TAB);
    }

    private void runTest(String fileName, Delimiter delim) {
        assertEquals(delim, TableWriter.resolveDelim(fileName));

        File file = new File(fileName);
        TableWriter writer = TableWriter.open(file);

        String key1 = "key1,";
        String key2 = "|key2";
        String key3 = "key3";
        String value1 = "|value1";
        String value2 = "value2,";
        String value3 = "value3";

        writer.println(key1, value1);
        writer.println(key2, value2);
        writer.println(key3, value3);
        writer.close();

        LineReader reader = LineReader.open(file);

        assertEquals(delim.join(key1, value1), reader.next());
        assertEquals(delim.join(key2, value2), reader.next());
        assertEquals(delim.join(key3, value3), reader.next());
        assertFalse(reader.hasNext());
        reader.close();

        file.delete();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TableWriterTest");
    }
}
