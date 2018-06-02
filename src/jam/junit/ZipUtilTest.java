
package jam.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import jam.io.ZipUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class ZipUtilTest {
    @Test public void testReaderWriter() throws IOException {
        File file = new File("test.gz");
        file.deleteOnExit();
        
        PrintWriter writer = ZipUtil.openGZipWriter(file);

        String line1 = "abcdefg";
        String line2 = "hijklmn";

        writer.println(line1);
        writer.println(line2);
        writer.close();

        BufferedReader reader = ZipUtil.openGZipReader(file);

        assertEquals(line1, reader.readLine());
        assertEquals(line2, reader.readLine());
        assertNull(reader.readLine());

        reader.close();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ZipUtilTest");
    }
}
