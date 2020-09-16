
package jam.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jam.lang.JamException;

/**
 * Provides a base class for objects that are uniquely mapped to a
 * single canonical physical file on the file system.
 */
public abstract class UniqueFile {
    /**
     * The underlying (canonical) physical file.
     */
    protected final File file;

    // One instance per canonical file...
    private static final Map<File, UniqueFile> registry = new HashMap<File, UniqueFile>();

    protected UniqueFile(File file) {
        this.file = file;

        validate();
        register();
    }

    private void validate() {
        if (!FileUtil.isCanonicalFile(file))
            throw JamException.runtime("Canonical files are required.");

        if (registry.containsKey(file))
            throw JamException.runtime("Duplicate file instance: [%s].", file);
    }

    private void register() {
        registry.put(file, this);
    }

    /**
     * Deletes the underlying physical file.
     *
     * @return {@code true} iff the file was successfully deleted.
     */
    public boolean delete() {
        return file.delete();
    }

    /**
     * Determines whether the underlying physical file exists.
     *
     * @return {@code true} iff the underlying physical file exists.
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * Returns the underlying canonical physical file.
     *
     * @return the underlying canonical physical file.
     */
    public File getCanonicalFile() {
        return file;
    }
}
