
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.io.TableReader;

import org.junit.*;
import static org.junit.Assert.*;

public class TableReaderTest {
    @Test public void testCSV() {
        runTest("data/test/table.csv");
    }

    @Test public void testPSV() {
        runTest("data/test/table.psv");
    }

    @Test public void testTSV() {
        runTest("data/test/table.tsv");
    }

    private void runTest(String fileName) {
        runNextTest(fileName);
        runIteratorTest(fileName);
    }

    private void runNextTest(String fileName) {
        TableReader reader = TableReader.open(fileName);

        assertEquals(2, reader.ncol());
        assertEquals(List.of("Key", "Value"), reader.columnKeys());

        assertEquals(List.of("abc", "0"), reader.next());
        assertEquals(List.of("def", "1"), reader.next());
        assertEquals(List.of("ghi", "2"), reader.next());
        assertFalse(reader.hasNext());
    }

    private void runIteratorTest(String fileName) {
        TableReader reader = TableReader.open(fileName);

        assertEquals(2, reader.ncol());
        assertEquals(List.of("Key", "Value"), reader.columnKeys());

        List<List<String>> lines = new ArrayList<List<String>>();

        for (List<String> line : reader)
            lines.add(line);

        assertEquals(3, lines.size());
        assertEquals(List.of("abc", "0"), lines.get(0));
        assertEquals(List.of("def", "1"), lines.get(1));
        assertEquals(List.of("ghi", "2"), lines.get(2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TableReaderTest");
    }
}
