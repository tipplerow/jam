
package jam.io;

import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import jam.lang.JamException;
import jam.report.LineBuilder;
import jam.util.RegexUtil;
import jam.util.StringUtil;

/**
 * Writes tabular (delimited) data files and resolves the delimiter
 * automatically from the file name suffix.
 */
public final class TableWriter implements Closeable {
    private final String delim;
    private final PrintWriter writer;

    private TableWriter(PrintWriter writer, String delim) {
        this.delim = delim;
        this.writer = writer;
    }

    /**
     * Opens a {@code TableWriter} for a given file and creates any
     * required subdirectories.
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
     * required subdirectories.
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
    public static String resolveDelim(String fileName) {
        fileName = ZipUtil.removeSuffix(fileName);

        if (fileName.endsWith(".csv"))
            return ",";

        if (fileName.endsWith(".psv"))
            return "|";

        return "\t";
    }

    /**
     * Returns the delimiter string for this table writer.
     *
     * @return the delimiter string for this table writer.
     */
    public String getDelim() {
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
        LineBuilder builder = new LineBuilder(delim);

        for (String field : fields)
            builder.append(field);

        writer.println(builder.toString());
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        writer.close();
    }
}
