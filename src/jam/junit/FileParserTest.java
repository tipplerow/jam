
package jam.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import jam.io.FileParser;

import org.junit.*;
import static org.junit.Assert.*;

public class FileParserTest {
    // Lines that have been read by the parser...
    ArrayList<String> lines = new ArrayList<String>();

    private static final Pattern comment = Pattern.compile("#");

    private class ParserImpl extends FileParser {
        private ParserImpl(String fileName) {
            super(fileName, comment);
        }
 
        @Override protected void processLine(String dataLine) {
            lines.add(dataLine);
        }
    }

    @Test public void testProcessFile() {
        FileParser parser = new ParserImpl("data/comments123.txt");

        parser.processFile();
        assertEquals(Arrays.asList("line 1", "line 2", "line 3"), lines);
    }

    @Test(expected = IllegalStateException.class)
    public void testProcessFileTwice() {
        FileParser parser = new ParserImpl("data/comments123.txt");

        parser.processFile();
        assertEquals(Arrays.asList("line 1", "line 2", "line 3"), lines);
        parser.processFile();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FileParserTest");
    }
}
