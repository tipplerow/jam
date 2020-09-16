
package jam.flat;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.io.LineReader;
import jam.lang.ObjectUtil;
import jam.util.IterableUtil;

/**
 * Provides a base class for tables of flat-file records.
 *
 * @param <V> the runtime type of the flat-file records.
 */
public abstract class RecordStore<V extends FlatRecord> implements Iterable<V> {
    /**
     * Creates a new record by parsing a line from the flat file.
     *
     * @param line a line from the flat file.
     *
     * @return the record encoded in the specified line.
     */
    public abstract V parse(String line);

    /**
     * Returns the number of records in this table.
     *
     * @return the number of records in this table.
     */
    public abstract int count();

    /**
     * Adds a record to this table.
     *
     * @param record the record to add.
     */
    public abstract void insert(V record);

    /**
     * Adds a collection of records to this table.
     *
     * @param records the records to add.
     */
    public void insert(Collection<V> records) {
        for (V record : records)
            insert(record);
    }

    /**
     * Identifies empty stores.
     *
     * @return {@code true} iff this store contains no records.
     */
    public boolean isEmpty() {
        return count() == 0;
    }

    /**
     * Returns a new list containing the records in this table.  The
     * list may be modified, but those changes will not be reflected
     * in this table.
     *
     * @return a new list containing the records in this table.
     */
    public List<V> list() {
        List<V> records = new ArrayList<V>(count());

        for (V record : this)
            records.add(record);

        return records;
    }

    /**
     * Reads all lines from a flat file, parses each line, and inserts
     * the records into this table.
     * table.
     *
     * @param reader a reader open for the the file to parse.
     *
     * @throws RuntimeException unless the file was successfully
     * processed.
     */
    public void load(LineReader reader) {
        for (String line : reader)
            insert(parse(line));
    }

    /**
     * Parses a flat file and adds all of the records to this table.
     *
     * @param file the file to parse.
     *
     * @throws RuntimeException unless the file was successfully
     * processed.
     */
    public void parse(File file) {
        try (LineReader reader = LineReader.open(file)) {
            load(reader);
        }
    }

    /**
     * Writes the records in this table to a flat file.
     *
     * @param file the file to write.
     *
     * @throws RuntimeException unless the file can be opened for
     * writing.
     */
    public void store(File file) {
        try (PrintWriter writer = IOUtil.openWriter(file)) {
            JamLogger.info("Writing file [%s]...", file);
            store(writer);
        }
    }

    private void store(PrintWriter writer) {
        for (V record : this)
            writer.println(record.format());
    }

    @Override public boolean equals(Object obj) {
        return ObjectUtil.equalsClass(this, obj) && equalsStore((RecordStore) obj);
    }

    @SuppressWarnings("unchecked")
    private boolean equalsStore(RecordStore that) {
        return IterableUtil.equals(this, that);
    }

    @Override public String toString() {
        Iterator<V> iterator = iterator();
        StringBuilder builder = new StringBuilder("[");

        if (iterator.hasNext())
            builder.append(iterator.next());

        while (iterator.hasNext()) {
            builder.append(", ");
            builder.append(iterator.next());
        }

        builder.append("]");
        return builder.toString();
    }
}
