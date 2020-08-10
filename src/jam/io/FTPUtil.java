
package jam.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.process.JamProcess;

/**
 * Executes FTP commands.
 */
public final class FTPUtil {
    /**
     * Prefix for valid FTP paths.
     */
    public static final String URL_PREFIX = "ftp://";

    /**
     * Downloads a remote file (but does not overwite an existing file).
     *
     * @param localFile the local destination file.
     *
     * @param remoteName the name of the remote source file.
     *
     * @return {@code true} iff the file was successfully downloaded.
     */
    public static boolean download(File localFile, String remoteName) {
        validateRemoteFile(remoteName);

        if (localFile.exists()) {
            JamLogger.warn("Local file [%s] already exists; not downloading.", localFile);
            return false;
        }

        JamProcess process = JamProcess.create("curl", "-o", FileUtil.getCanonicalPath(localFile), remoteName);
        process.run();

        return localFile.exists();
    }

    /**
     * Lists the contents on a remote FTP server.
     *
     * @param dirName the fully specified remote directory name.
     *
     * @return a directory listing for the specified FTP directory.
     */
    public static List<String> list(String dirName) {
        validateRemoteFile(dirName);

        if (!dirName.endsWith("/"))
            dirName = dirName + "/";

        JamProcess process = JamProcess.create("curl", "--list-only", dirName);
        process.run();

        List<String> fileNames = new ArrayList<String>();

        for (String fileName : process.stdout())
            fileNames.add(fileName);

        return fileNames;
    }

    /**
     * Validates remote FTP file names.
     *
     * @param fileName the name of the file to validate.
     *
     * @throws RuntimeException unless the file name begins with the
     * required prefix.
     */
    public static void validateRemoteFile(String fileName) {
        if (!fileName.startsWith(URL_PREFIX))
            throw JamException.runtime("Invalid FTP file name: [%s].", fileName);
    }
}
