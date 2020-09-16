
package jam.io;

import java.io.File;
import jam.io.FileIndex;
import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class FileIndexTest {
    private static final String PARENT_NAME = System.getProperty("java.io.tmpdir", ".");
    private static final String BASE_PREFIX = "state_";
    private static final String BASE_SUFFIX = ".csv";
    private static final FileIndex INSTANCE = new FileIndex(PARENT_NAME, BASE_PREFIX, BASE_SUFFIX);

    @Test public void testResolveFile() {
	int[] indexes = new int[] { 0, 1, 998, 999, 1000, 1001, 33333, 999998, 999999 };

	for (int k : indexes)
	    assertEquals(expectedFile(k), INSTANCE.resolveFile(k));
    }

    private static File expectedFile(int fileIndex) {
	return new File(expectedName(fileIndex));
    }

    private static String expectedName(int fileIndex) {
	return FileUtil.join(PARENT_NAME, 
			     String.format("%03d", fileIndex / 1000), 
			     String.format("%s%06d%s", BASE_PREFIX, fileIndex, BASE_SUFFIX));
    }

    @Test(expected = RuntimeException.class)
    public void testNonPositiveIndex() {
	INSTANCE.resolveFile(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testTooLargeIndex() {
	INSTANCE.resolveFile(123456789);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.FileIndexTest");
    }
}
