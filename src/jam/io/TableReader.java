
package jam.io;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import jam.lang.JamException;
import jam.util.RegexUtil;

/**
 * Provides for {@code Iterable} reading of tabular data files, line
 * by line.
 *
 * <p>The files to be processed must contain delimited columns and a
 * header line.  The delimiter may be a comma, tab, or pipe character
 * ({@code |}).  If none of these characters are found in the header
 * line, the reader assumes that the columns are delimted by white
 * space.
 */
public final class TableReader implements Closeable, Iterable<List<String>>, Iterator<List<String>> {
    private final LineReader reader;

    private final String header;
    private final Pattern delimiter;
    private final List<String> columnKeys;

    private TableReader(LineReader reader) {
        this.reader = reader;

        this.header = readHeader();
        this.delimiter = resolveDelimiter();
        this.columnKeys = parseHeader();
    }

    private String readHeader() {
        if (reader.hasNext())
            return reader.next();
        else
            throw JamException.runtime("Empty tabular file.");
    }

    private Pattern resolveDelimiter() {
        //
        // The header line may contain zero or one of the possible
        // delimiter set (",", "\t", "|").  If it contains none of
        // those, then the delimiter is white space.
        //
        List<Pattern> delimiters = new ArrayList<Pattern>();

        if (header.contains(","))
            delimiters.add(RegexUtil.COMMA);

        if (header.contains("\t"))
            delimiters.add(RegexUtil.TAB);

        if (header.contains("|"))
            delimiters.add(RegexUtil.PIPE);

        switch (delimiters.size()) {
        case 0:
            return RegexUtil.MULTI_WHITE_SPACE;

        case 1:
            return delimiters.get(0);

        default:
            throw JamException.runtime("Multiple delimiters are present in the header line.");
        }
    }

    private List<String> parseHeader() {
        return List.of(RegexUtil.split(delimiter, header));
    }

    /**
     * Creates a new table reader for a specified file.
     *
     * @param file the file to read.
     *
     * @return the table reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static TableReader open(File file) {
        return new TableReader(LineReader.open(file));
    }

    /**
     * Creates a new table reader for a specified file.
     *
     * @param fileName the name of the file to read.
     *
     * @return the table reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for reading.
     */
    public static TableReader open(String fileName) {
        return new TableReader(LineReader.open(fileName));
    }

    /**
     * Returns a read-only view of the column keys.
     *
     * @return a read-only view of the column keys.
     */
    public List<String> columnKeys() {
        return columnKeys;
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns in the table.
     */
    public int ncol() {
        return columnKeys.size();
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        reader.close();
    }

    /**
     * Indicates whether the reader has more data lines.
     *
     * @return {@code true} iff there is another data line to read.
     */
    @Override public boolean hasNext() {
        return reader.hasNext();
    }

    /**
     * Returns the columns from the next data line in the file.
     *
     * @return the columns from the next data line in the file.
     *
     * @throws NoSuchElementException if the reader has reached the
     * end of the file.
     */
    @Override public List<String> next() {
        return List.of(RegexUtil.split(delimiter, reader.next(), columnKeys.size()));
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

    @Override public Iterator<List<String>> iterator() {
        return this;
    }
}
