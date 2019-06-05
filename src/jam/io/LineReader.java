
package jam.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.io.LineIterator;

import jam.app.JamLogger;

/**
 * Provides for {@code Iterable} file reading, line by line.
 */
public final class LineReader implements Closeable, Iterable<String>, Iterator<String> {
    private final LineIterator iterator;

    private LineReader(LineIterator iterator) {
        this.iterator = iterator;
    }

    private LineReader(BufferedReader reader) {
        this(new LineIterator(reader));
    }

    /**
     * Creates a new reader for a specified file.
     *
     * @param file the file to read.
     *
     * @return the line reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static LineReader open(File file) {
        JamLogger.info("Opening LineReader(%s)...", file);
        return new LineReader(IOUtil.openReader(file));
    }

    /**
     * Creates a new reader for a specified file.
     *
     * @param fileName the name of the file to read.
     *
     * @return the line reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for reading.
     */
    public static LineReader open(String fileName) {
        return open(new File(fileName));
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        iterator.close();
    }

    /**
     * Indicates whether the reader has more lines.
     *
     * @return {@code true} iff there is another line to read.
     */
    @Override public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Returns the next line in the file.
     *
     * @return the next line in the file.
     *
     * @throws NoSuchElementException if the reader has reached the
     * end of the file.
     */
    @Override public String next() {
        return iterator.next();
    }

    /**
     * Throws an {@code UnsupportedOperationException} as removal is
     * not supported.
     *
     * @throws UnsupportedOperationException always.
     */
    @Override public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override public Iterator<String> iterator() {
        return this;
    }
}
