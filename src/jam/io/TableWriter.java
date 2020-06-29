
package jam.io;

import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import jam.lang.JamException;
import jam.report.LineBuilder;

/**
 * Writes tabular (delimited) data files and resolves the delimiter
 * automatically from the file name suffix.
 */
public final class TableWriter implements Closeable {
    private final Delimiter delim;
    private final PrintWriter writer;

    private TableWriter(PrintWriter writer, Delimiter delim) {
        this.delim = delim;
        this.writer = writer;
    }

    /**
     * Opens a {@code TableWriter} for a given file and creates any
     * required subdirectories; the delimiter is inferred from the
     * file name suffix.
     *
     * @param file the file to write.
     *
     * @return the open table writer.
     *
     * @throws RuntimeException unless the file can be opened for
     * writing.
     */
    public static TableWriter open(File file) {
        return new TableWriter(IOUtil.openWriter(file), resolveDelim(file.getName()));
    }

    /**
     * Opens a {@code TableWriter} for a given file and creates any
     * required subdirectories; the delimiter is inferred from the
     * file name suffix.
     *
     * @param fileName the name of the file to write.
     *
     * @return the open table writer.
     *
     * @throws RuntimeException unless the file can be opened for
     * writing.
     */
    public static TableWriter open(String fileName) {
        return open(new File(fileName));
    }

    /**
     * Resolves the delimiter character from the suffix of the file
     * name.
     *
     * <p>The suffix-delimiter mapping is the following:
     * <pre>
     *         .csv =&gt; ','   (comma)
     *         .psv =&gt; '|'   (pipe)
     *         .tsv =&gt; '\t'  (tab)
     *         .txt =&gt; '\t'  (tab)
     * </pre>
     *
     * If the file name represents a compressed file (ends with
     * {@code .gz} or {@code .zip}), that suffix is removed before
     * applying the mapping above.
     *
     * @param fileName the name of the file to be written.
     *
     * @return the delimiter character to use for the specified
     * file name.
     */
    public static Delimiter resolveDelim(String fileName) {
        fileName = ZipUtil.removeSuffix(fileName);

        if (fileName.endsWith(".csv"))
            return Delimiter.COMMA;

        if (fileName.endsWith(".psv"))
            return Delimiter.PIPE;

        return Delimiter.TAB;
    }

    /**
     * Returns the delimiter string for this table writer.
     *
     * @return the delimiter string for this table writer.
     */
    public Delimiter getDelim() {
        return delim;
    }

    /**
     * Writes fields to a new line using the table delimiter.
     *
     * @param fields the fields to write.
     */
    public void println(String... fields) {
        println(List.of(fields));
    }

    /**
     * Writes fields to a new line using the table delimiter.
     *
     * @param fields the fields to write.
     */
    public void println(List<String> fields) {
        writer.println(delim.join(fields));
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        writer.close();
    }
}
