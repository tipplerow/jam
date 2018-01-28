
package jam.io;

import java.io.File;

import jam.lang.JamException;

/**
 * Provides utility methods for operating on directories.
 *
 * <p>All methods wrap checked exceptions ({@code IOException}s) in
 * runtime exceptions.
 */
public final class DirUtil {
    private DirUtil() {}

    /**
     * Creates a directory (and any required parent directories) if it
     * does not already exist.
     *
     * @param dir the path name of the required directory.
     *
     * @throws RuntimeException if (1) the path name refers to an
     * existing file, not a directory, or (2) the path name does not
     * exist and a directory cannot be created.
     */
    public static void onDemand(File dir) {
        if (!dir.exists()) {
            boolean success = dir.mkdirs();

            if (!success)
                throw JamException.runtime("Could not create directory [%s].", dir);
        }
        else if (!dir.isDirectory()) {
            throw JamException.runtime("Path [%s] is not a directory [%s].", dir);
        }
    }
}
