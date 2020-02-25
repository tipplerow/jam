
package jam.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jam.lang.JamException;
import jam.util.StringUtil;

/**
 * Provides utility methods for I/O operations on compressed files.
 */
public final class ZipUtil {
    /**
     * The suffix that identifies GZIP files.
     */
    public static final String GZIP_SUFFIX = ".gz";

    /**
     * Identifies valid GZIP files.
     *
     * @param file the file to test.
     *
     * @return {@code true} iff the file name ends with the GZIP
     * suffix.
     */
    public static boolean isGZipFile(File file) {
        return isGZipFileName(file.getName());
    }

    /**
     * Identifies valid names for GZIP files.
     *
     * @param fileName the file name to test.
     *
     * @return {@code true} iff the file name ends with the GZIP
     * suffix.
     */
    public static boolean isGZipFileName(String fileName) {
        return fileName.endsWith(GZIP_SUFFIX);
    }

    /**
     * Opens a reader for a GZIP file.
     *
     * @param file the file to read.
     *
     * @return a reader for the specified GZIP file.
     *
     * @throws RuntimeException unless the file is a valid GZIP file
     * and is open for reading.
     */
    public static BufferedReader openGZipReader(File file) {
        try {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
    }

    /**
     * Opens a writer for a GZIP file and creates any subdirectories
     * in the file path that do not already exist; if the file already
     * exists its contents will be deleted.
     *
     * @param file the file to write.
     *
     * @return a writer for the specified GZIP file.
     *
     * @throws RuntimeException unless the file is a valid GZIP file
     * and is open for writing.
     */
    public static PrintWriter openGZipWriter(File file) {
        if (!isGZipFile(file))
            throw JamException.runtime("File [%s] is not a GZIP file.");

        try {
            FileUtil.ensureParentDirs(file);
            return new PrintWriter(new GZIPOutputStream(new FileOutputStream(file)));
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
    }

    /**
     * Removes the {@code .gz} suffix from a file name (if present).
     *
     * @param fileName the file name to strip.
     *
     * @return the file name with the suffix removed (or the original
     * file name if the suffix was not present).
     */
    public static String removeSuffix(String fileName) {
        return StringUtil.removeSuffix(fileName, GZIP_SUFFIX);
    }
}
