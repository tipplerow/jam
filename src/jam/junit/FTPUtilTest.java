
package jam.junit;

import java.io.File;
import java.util.List;

import jam.io.FTPUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class FTPUtilTest {
    private static final File localFile = new File("data/test/README.txt");

    private static final String remoteDirName = "ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/";
    private static final String remoteFileName = remoteDirName + "README.txt";

    @Test public void testDownload() {
        localFile.deleteOnExit();
        assertFalse(localFile.exists());

        assertTrue(FTPUtil.download(localFile, remoteFileName));
        assertTrue(localFile.exists());
    }

    @Test public void testList() {
        List<String> fileNames = FTPUtil.list(remoteDirName);

        assertFalse(fileNames.isEmpty());

        for (String fileName : fileNames)
            assertTrue(fileName.startsWith("pubmed20n") || fileName.equals("README.txt"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FTPUtilTest");
    }
}
