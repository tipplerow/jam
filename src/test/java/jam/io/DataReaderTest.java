
package jam.io;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jam.io.DataReader;

import org.junit.*;
import static org.junit.Assert.*;

public class DataReaderTest {
    private static final Pattern comment = Pattern.compile("#");

    @Test public void testContinuation() {
        assertLines("data/test/continuation.txt", Arrays.asList("line 1", "line 2", "line 3", "abc def ghi"));
    }

    @Test public void testIterator() {
        assertLines("data/test/comments123.txt", Arrays.asList("line 1", "line 2", "line 3"));
    }

    private void assertLines(String fileName, List<String> expected) {
        assertEquals(expected, DataReader.read(fileName, comment));
    }

    @Test public void testNext() {
        DataReader reader = DataReader.open("data/test/comments123.txt", comment);

        assertEquals("line 1", reader.next());
        assertEquals("line 2", reader.next());
        assertEquals("line 3", reader.next());
        assertFalse(reader.hasNext());

        reader.close();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.DataReaderTest");
    }
}
