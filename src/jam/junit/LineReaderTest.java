
package jam.junit;

import java.util.ArrayList;
import java.util.Arrays;

import jam.io.LineReader;

import org.junit.*;
import static org.junit.Assert.*;

public class LineReaderTest {

    @Test public void testIterator() {
        ArrayList<String> lines = new ArrayList<String>();
        LineReader reader = LineReader.open("data/lines123.txt");

        for (String line : reader)
            lines.add(line);

        reader.close();
        assertEquals(Arrays.asList("line 1", "line 2", "line 3"), lines);
    }

    @Test public void testNext() {
        LineReader reader = LineReader.open("data/lines123.txt");

        assertEquals("line 1", reader.next());
        assertEquals("line 2", reader.next());
        assertEquals("line 3", reader.next());
        assertFalse(reader.hasNext());

        reader.close();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LineReaderTest");
    }
}
