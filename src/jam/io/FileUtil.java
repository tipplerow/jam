
package jam.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import jam.lang.JamException;

/**
 * Provides utility methods for operating on files and file names.
 *
 * <p>All methods wrap checked exceptions ({@code IOException}s) in
 * runtime exceptions.
 */
public final class FileUtil {
    private FileUtil() {}

    /**
     * Returns the canonical path of the specified file but never
     * throws checked exceptions.
     *
     * @param file a file to examine.
     *
     * @return the canonical path of the specified file.
     *
     * @throws RuntimeException if {@code file.getCanonicalPath}
     * throws an {@code IOException}.
     */
    public static String getCanonicalPath(File file) {
        String result = null;

        try {
            result = file.getCanonicalPath();
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }

        return result;
    }

    /**
     * Returns the name of the parent directory of a file, or
     * {@code .} (dot) if the file does not specify a parent.
     *
     * <p>Unlike the method {@code java.io.File.getParent}, this
     * method never returns {@code null}.
     *
     * @param file a file to examine.
     *
     * @return the name of the parent directory of the specified file,
     * or {@code .} (dot) if the file does not specify a parent.
     */
    public static String getParentName(File file) {
	String result = file.getParent();

	if (result == null)
	    result = ".";

	return result;
    }

    /**
     * Returns the parent directory of a file, or the "dot" directory
     * if the file does not specify a parent.
     *
     * <p>Unlike the method {@code java.io.File.getParentFile}, this
     * method never returns {@code null}.
     *
     * @param file a file to examine.
     *
     * @return the parent directory of the specified file, or the
     * "dot" directory if the file does not specify a parent.
     */
    public static File getParentFile(File file) {
	return new File(getParentName(file));
    }

    /**
     * Builds a composite file name by joining components with the
     * system-dependent name separator.
     *
     * @param paths file name components.
     *
     * @return the composite file name.
     */
    public static String join(String... paths) {
        return StringUtils.join(paths, File.separator);
    }

    /**
     * Asserts that a file exists.
     *
     * @param fileName the name of the required file.
     *
     * @throws RuntimeException if the file does not exist.
     */
    public static void requireFile(String fileName) {
        requireFile(new File(fileName));
    }

    /**
     * Asserts that a file exists.
     *
     * @param file the required file.
     *
     * @throws RuntimeException if the file does not exist.
     */
    public static void requireFile(File file) {
        if (!file.exists())
            throw JamException.runtime("File [%s] does not exist.", getCanonicalPath(file));
    }
}
