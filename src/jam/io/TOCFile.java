
package jam.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;

/**
 * Maintains a table of contents (represented by {@code String} keys)
 * in memory and in a physical file.
 */
public final class TOCFile extends UniqueFile {
    private final Set<String> items = new HashSet<String>();

    // One instance per canonical file...
    private static final Map<File, TOCFile> instances = new HashMap<File, TOCFile>();

    private TOCFile(File file) {
        super(file);
        loadItems();
    }

    private void loadItems() {
        if (file.exists())
            items.addAll(IOUtil.readLines(file));
    }

    /**
     * Returns a table-of-contents file with a fixed physical file
     * path and loads the existing contents (if any) into memory.
     *
     * @param file the path of the underlying physical file.
     *
     * @return the table-of-contents file with the specified physical
     * file.
     */
    public static synchronized TOCFile instance(File file) {
        File canonical = FileUtil.getCanonicalFile(file);
        TOCFile instance = instances.get(canonical);

        if (instance == null) {
            instance = new TOCFile(canonical);
            instances.put(canonical, instance);
        }

        return instance;
    }

    /**
     * Returns a table-of-contents file with a fixed physical file
     * path and loads the existing contents (if any) into memory.
     *
     * @param fileName the path of the underlying physical file.
     *
     * @return the table-of-contents file with the specified physical
     * file.
     */
    public static TOCFile instance(String fileName) {
        return instance(new File(fileName));
    }

    /**
     * Adds an item to the table of contents (both in memory and in
     * the underlying physical file).
     *
     * @param item the item to add.
     */
    public synchronized void add(String item) {
        if (!contains(item)) {
            IOUtil.writeLines(file, true, item);
            items.add(item);
        }
    }

    /**
     * Determines whether this table of contents contains a given
     * item.
     *
     * @param item the item to examine.
     *
     * @return {@code true} iff this table of contents contains the
     * specified item.
     */
    public boolean contains(String item) {
        return items.contains(item);
    }

    /**
     * Returns a read-only view of the items in this table of
     * contents.
     *
     * @return a read-only view of the items in this table of
     * contents.
     */
    public Set<String> viewItems() {
        return Collections.unmodifiableSet(items);
    }

    @Override public boolean delete() {
        if (super.delete()) {
            items.clear();
            return true;
        }
        else
            return false;
    }
}
