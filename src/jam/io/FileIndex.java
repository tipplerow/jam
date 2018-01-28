
package jam.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Manages a large collection of files indexed by a continuous integer
 * value (for example, a time series of configuration states generated
 * by a Monte Carlo simulation).  Distributes files among directories
 * with a manageable number of files per directory.
 */
public final class FileIndex {
    private final File   parentDir;
    private final String basePrefix;
    private final String baseSuffix;

    private static final int    SUBDIR_CAPACITY = 1000;
    private static final String SUBDIR_FORMAT   = "%03d";
    private static final String BASENAME_FORMAT = "%s%06d%s";

    /**
     * Maximum number of files managed by a {@code FileIndex}.
     */
    public static final int TOTAL_CAPACITY = 1000000;

    /**
     * Creates a new file index.
     *
     * @param parentDir the top-level directory in the directory tree.
     *
     * @param basePrefix the prefix for the base file names.
     *
     * @param baseSuffix the suffix for the base file names.
     */
    public FileIndex(File   parentDir,
		     String basePrefix,
		     String baseSuffix) {
	this.parentDir  = parentDir;
	this.basePrefix = basePrefix;
	this.baseSuffix = baseSuffix;
    }

    /**
     * Creates a new file index.
     *
     * @param parentName the name of the top-level directory in
     * the directory tree.
     *
     * @param basePrefix the prefix for the base file names.
     *
     * @param baseSuffix the suffix for the base file names.
     */
    public FileIndex(String parentName,
		     String basePrefix,
		     String baseSuffix) {
	this(new File(parentName), basePrefix, baseSuffix);
    }

    /**
     * Translates a file index into a file path.
     *
     * @param fileIndex the target file index.
     *
     * @return a path for the file with the specified index.
     *
     * @throws IllegalArgumentException unless the file index is in
     * the valid range {@code [0, TOTAL_CAPACITY - 1]}.
     */
    public File resolveFile(int fileIndex) {
	validateIndex(fileIndex);
	return new File(resolveDir(fileIndex), resolveBase(fileIndex));
    }

    private void validateIndex(int fileIndex) {
	if (fileIndex < 0 || fileIndex >= TOTAL_CAPACITY)
	    throw new IllegalArgumentException("File index out of range.");
    }

    private File resolveDir(int fileIndex) {
	return new File(parentDir, String.format(SUBDIR_FORMAT, computeSubdirIndex(fileIndex)));
    }

    private static int computeSubdirIndex(int fileIndex) {
	return fileIndex / SUBDIR_CAPACITY;
    }

    private String resolveBase(int fileIndex) {
	return String.format(BASENAME_FORMAT, basePrefix, fileIndex, baseSuffix);
    }

    /**
     * Returns the top-level directory in the directory tree.
     *
     * @return the top-level directory in the directory tree.
     */
    public File getParentDir() {
	return parentDir;
    }

    /**
     * Returns the prefix for the base file names.
     *
     * @return the prefix for the base file names.
     */
    public String getBasePrefix() {
	return basePrefix;
    }

    /**
     * Returns the suffix for the base file names.
     *
     * @return the suffix for the base file names.
     */
    public String getBaseSuffix() {
	return baseSuffix;
    }
}
