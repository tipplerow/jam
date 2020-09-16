
package jam.io;

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

    @Test public void testPSV2() {
        TableReader reader = TableReader.open("data/test/table2.psv");

        assertEquals(List.of("abc",  "|0"), reader.next());
        assertEquals(List.of("|def", "1"),  reader.next());
        assertEquals(List.of("ghi",  "2"),  reader.next());
        assertFalse(reader.hasNext());
            
        reader.close();
    }

    @Test public void testTSV() {
        runTest("data/test/table.tsv");
    }

    @Test(expected = RuntimeException.class)
    public void testMissingIndex() {
        TableReader reader = TableReader.open("data/test/table.csv");

        try {
            reader.requireColumn("Not_Found");
        }
        finally {
            reader.close();
        }
    }

    private void runTest(String fileName) {
        runNextTest(fileName);
        runIteratorTest(fileName);
    }

    private void runNextTest(String fileName) {
        TableReader reader = TableReader.open(fileName);

        assertEquals(2, reader.ncol());
        assertEquals(List.of("Key", "Value"), reader.columnKeys());

        assertEquals( 0, reader.findColumn("Key"));
        assertEquals( 1, reader.findColumn("Value"));
        assertEquals(-1, reader.findColumn("Not_Found"));

        assertEquals(List.of("abc", "0"), reader.next());
        assertEquals("abc", reader.columnFor("Key"));
        assertEquals("0", reader.columnFor("Value"));

        assertEquals(List.of("def", "1"), reader.next());
        assertEquals("def", reader.columnFor("Key"));
        assertEquals("1", reader.columnFor("Value"));

        assertEquals(List.of("ghi", "2"), reader.next());
        assertEquals("ghi", reader.columnFor("Key"));
        assertEquals("2", reader.columnFor("Value"));

        assertFalse(reader.hasNext());
        assertEquals("ghi", reader.columnFor("Key"));
        assertEquals("2", reader.columnFor("Value"));

        reader.close();
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

        reader.close();
    }

    @Test public void testRagged() {
        TableReader reader = TableReader.ragged("data/test/ragged_table.csv");

        assertEquals(List.of("Key", "Value"), reader.columnKeys());
        assertEquals(List.of("abc", "0"), reader.next());
        assertEquals(List.of("def"), reader.next());
        assertEquals(List.of("ghi", "2", "44"), reader.next());
        assertFalse(reader.hasNext());

        reader.close();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.TableReaderTest");
    }
}
