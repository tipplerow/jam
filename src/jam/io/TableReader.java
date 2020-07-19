
package jam.io;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Provides for {@code Iterable} reading of tabular data files, line
 * by line.
 *
 * <p>The files to be processed must contain delimited columns and a
 * header line. The delimiter may be a comma, tab, or pipe character
 * ({@code |}).  If none of these characters are found in the header
 * line, the reader assumes that the columns are delimted by white
 * space.
 *
 * <p><b>Ragged lines.</b> Readers created by the {@code ragged()}
 * methods will allow files with lines containing different numbers
 * of columns.  Readers created by the {@code open()} methods will
 * throw exceptions if they encounter ragged lines.
 */
public final class TableReader implements Closeable, Iterable<List<String>>, Iterator<List<String>> {
    private final LineReader reader;
    private final boolean ragged;

    private final String header;
    private final Delimiter delimiter;
    private final List<String> columnKeys;
    private final Map<String, Integer> columnIndex;

    private TableReader(LineReader reader, boolean ragged) {
        this.reader = reader;
        this.ragged = ragged;

        this.header = readHeader();
        this.delimiter = resolveDelimiter();
        this.columnKeys = parseHeader();
        this.columnIndex = indexHeader();
    }

    private String readHeader() {
        if (reader.hasNext())
            return reader.next();
        else
            throw JamException.runtime("Empty tabular file.");
    }

    private Delimiter resolveDelimiter() {
        //
        // The header line may contain zero or one of the possible
        // delimiter set (",", "\t", "|").  If it contains none of
        // those, then the delimiter is white space.
        //
        List<Delimiter> delimiters = new ArrayList<Delimiter>();

        if (header.contains(","))
            delimiters.add(Delimiter.COMMA);

        if (header.contains("\t"))
            delimiters.add(Delimiter.TAB);

        if (header.contains("|"))
            delimiters.add(Delimiter.PIPE);

        switch (delimiters.size()) {
        case 0:
            return Delimiter.WHITE_SPACE;

        case 1:
            return delimiters.get(0);

        default:
            throw JamException.runtime("Multiple delimiters are present in the header line.");
        }
    }

    private List<String> parseHeader() {
        return List.of(delimiter.split(header));
    }

    private Map<String, Integer> indexHeader() {
        Map<String, Integer> columnIndex = new HashMap<String, Integer>();

        for (int index = 0; index < columnKeys.size(); ++index)
            columnIndex.put(columnKeys.get(index), index);

        return Collections.unmodifiableMap(columnIndex);
    }

    private static TableReader open(File file, boolean ragged) {
        return new TableReader(LineReader.open(file), ragged);
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
        return open(file, false);
    }

    /**
     * Creates a new table reader for a specified file that may
     * contain <em>ragged</em> lines.
     *
     * @param file the file to read.
     *
     * @return the table reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static TableReader ragged(File file) {
        return open(file, true);
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
        return open(new File(fileName), false);
    }

    /**
     * Creates a new table reader for a specified file that may
     * contain <em>ragged</em> lines.
     *
     * @param fileName the name of the file to read.
     *
     * @return the table reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for reading.
     */
    public static TableReader ragged(String fileName) {
        return open(new File(fileName), true);
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
     * Returns the index of the column with a given key.
     *
     * @param columnKey the desired column key.
     *
     * @return the index of the column with the specified key, or
     * {@code -1} if the key was not found in the header line.
     */
    public int findColumn(String columnKey) {
        Integer index = columnIndex.get(columnKey);

        if (index != null)
            return index;
        else
            return -1;
    }

    /**
     * Extracts a column field from a parsed data line.
     *
     * @param columns a parsed data line (a column list returned by
     * the iterator {@code next()} method).
     *
     * @param columnKey the key of the column to extract.
     *
     * @return the field in the specified column (or {@code null} if
     * this reader is processing a ragged file and the specified line
     * does not contain the column).
     *
     * @throws RuntimeException unless the column key is found in the
     * header line.
     */
    public String getColumn(List<String> columns, String columnKey) {
        int index = requireColumn(columnKey);

        if (columns.size() > index)
            return columns.get(index);
        else
            return null;
    }

    /**
     * Returns the index of the column with a given key.
     *
     * @param columnKey the desired column key.
     *
     * @return the index of the column with the specified key.
     *
     * @throws RuntimeException unless the key is found in the
     * header line.
     */
    public int requireColumn(String columnKey) {
        int index = findColumn(columnKey);

        if (index < 0)
            throw JamException.runtime("Missing column [%s].", columnKey);
        else
            return index;
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
     *
     * @throws RuntimeException if the number of columns in the next
     * line does not match the number of column keys and ragged lines
     * are not allowed.
     */
    @Override public List<String> next() {
        String line = reader.next();
        List<String> columns = List.of(delimiter.split(line));

        if (!ragged)
            validateColumns(line, columns);

        return columns;
    }

    private void validateColumns(String line, List<String> columns) {
        int actual = columns.size();
        int expected = columnKeys.size();

        if (actual != expected) {
            JamLogger.error("Invalid data line: [%s].", line);
            throw JamException.runtime("Expected [%d] columns but found [%d].", expected, actual);
        }
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
