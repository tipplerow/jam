
package jam.io;

import java.io.Closeable;
import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jam.app.JamLogger;

/**
 * Reads flat files containing formatted objects and provides for
 * iteration over the file line by line.
 *
 * <p>The flat files must contain exactly one object on each line
 * with no header line or blank lines.
 */
public final class ObjectReader<T> implements Closeable, Iterable<T>, Iterator<T> {
    private final LineReader reader;
    private final ObjectParser<T> parser;

    private ObjectReader(LineReader reader, ObjectParser<T> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    /**
     * Creates a new object reader for a specified flat file.
     *
     * @param <T> the runtime object type.
     *
     * @param file the flat file to read.
     *
     * @param parser the object parser.
     *
     * @return the object reader for the specified flat file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static <T> ObjectReader<T> open(File file, ObjectParser<T> parser) {
        JamLogger.info("Opening ObjectReader(%s)...", file);
        return new ObjectReader<T>(LineReader.open(file), parser);
    }

    /**
     * Creates a new object reader for a specified flat file.
     *
     * @param <T> the runtime object type.
     *
     * @param fileName the name of the flat file to read.
     *
     * @param parser the object parser.
     *
     * @return the object reader for the specified flat file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static <T> ObjectReader<T> open(String fileName, ObjectParser<T> parser) {
        return open(new File(fileName), parser);
    }

    /**
     * Reads genotypes from a flat file containing one genotype per
     * line (and no header line).
     *
     * @param <T> the runtime object type.
     *
     * @param fileName the name of the flat file to read.
     *
     * @param parser the object parser.
     *
     * @return a list containing the objects in the flat file.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static <T> List<T> load(String fileName, ObjectParser<T> parser) {
        List<T> objects = new ArrayList<T>();
        ObjectReader<T> reader = ObjectReader.open(fileName, parser);

        try {
            for (T object : reader)
                objects.add(object);
        }
        finally {
            IOUtil.close(reader);
        }

        return objects;
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        reader.close();
    }

    /**
     * Indicates whether the reader has more objects.
     *
     * @return {@code true} iff there is another object to read.
     */
    @Override public boolean hasNext() {
        return reader.hasNext();
    }

    /**
     * Returns the next object in the file.
     *
     * @return the next object in the file.
     *
     * @throws NoSuchElementException if the reader has reached the
     * end of the file.
     */
    @Override public T next() {
        return parser.parse(reader.next());
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override public Iterator<T> iterator() {
        return this;
    }
}
