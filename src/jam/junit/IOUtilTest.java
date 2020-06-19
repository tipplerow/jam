
package jam.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;

import jam.io.IOUtil;
import jam.util.RegexUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class IOUtilTest {
    @Test public void testGZip() throws IOException {
        File file = new File("test.gz");
        file.deleteOnExit();

        PrintWriter writer = IOUtil.openWriter(file);

        String line1 = "abcdefg";
        String line2 = "hijklmn";

        writer.println(line1);
        writer.println(line2);
        writer.close();

        BufferedReader reader = IOUtil.openReader(file);

        assertEquals(line1, reader.readLine());
        assertEquals(line2, reader.readLine());
        assertNull(reader.readLine());

        reader.close();
    }

    @Test public void testNextDataLine() {
        BufferedReader reader = IOUtil.openReader("data/test/comments123.txt");

        assertEquals("line 1", IOUtil.nextDataLine(reader, RegexUtil.PYTHON_COMMENT));
        assertEquals("line 2", IOUtil.nextDataLine(reader, RegexUtil.PYTHON_COMMENT));
        assertEquals("line 3", IOUtil.nextDataLine(reader, RegexUtil.PYTHON_COMMENT));
        assertNull(IOUtil.nextDataLine(reader, RegexUtil.PYTHON_COMMENT));
    }

    @Test public void testOpenReader() throws IOException {
        BufferedReader reader = IOUtil.openReader("data/test/lines123.txt");

        assertEquals("line 1", reader.readLine());
        assertEquals("line 2", reader.readLine());
        assertEquals("line 3", reader.readLine());
        assertNull(reader.readLine());

        IOUtil.close(reader);
    }

    @Test(expected = RuntimeException.class)
    public void testOpenReaderNotFound() {
        IOUtil.openReader("no such file");
    }

    @Test public void testOpenWriter() {
        File tmpFile = new File("tmp1.txt");
        tmpFile.deleteOnExit();

        PrintWriter writer = IOUtil.openWriter(tmpFile, false);
        writer.println("line 1");
        writer.close();

        writer = IOUtil.openWriter(tmpFile, false);
        writer.println("line 2");
        writer.close();

        writer = IOUtil.openWriter(tmpFile, true);
        writer.println("line 3");
        writer.close();

        assertEquals(Arrays.asList("line 2", "line 3"), IOUtil.readLines(tmpFile));
    }

    @Test public void testReadLines() {
        assertEquals(Arrays.asList("line 1", "line 2", "line 3"), IOUtil.readLines("data/test/lines123.txt"));
    }

    @Test public void testWriteLines() {
        File tmpFile = new File("tmp2.txt");
        tmpFile.deleteOnExit();

        IOUtil.writeLines(tmpFile, false, "foo", "bar");
        IOUtil.writeLines(tmpFile, false, "abc", "def");
        IOUtil.writeLines(tmpFile, true,  "ghi", "jkl");

        assertEquals(Arrays.asList("abc", "def", "ghi", "jkl"), IOUtil.readLines(tmpFile));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IOUtilTest");
    }
}
