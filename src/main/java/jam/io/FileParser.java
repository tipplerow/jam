
package jam.io;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Standardizes the processing of commented data (or configuration)
 * files.
 *
 * <p>Commented data files may contain single-line and inline comments
 * (starting with an arbitrary identifier) along with blank lines and
 * other white space. The following example file denotes comments with
 * a hash sign:
 *
 * <pre>
   # A single-line comment; the entire line is ignored
   3, 4, 5 # A data line with an inline comment, will be processed as "3, 4, 5"

   # The blank line above will be ignored but the one below will be processed...
        5, 12, 13
   # The line above will have leading and trailing white space removed
   # and so will be processed as "5, 12, 13"
 * </pre>
 *
 * <p>A single data item may be spread across multiple consecutive
 * lines if the lines end with the backslash continuation character
 * ({@code '\'}). For example:
 * <pre>
   key = a value \
         continued \
         on three lines
 * </pre>
 * is returned in the single line {@code "key = a value continued on three lines"}.
 *
 * <p>Subclasses must implement {@link FileParser#processLine(String)}, 
 * which will be called by {@link FileParser#processFile()} for every
 * <em>data line</em> in the file.  A <em>data line</em> is stripped 
 * of comments and leading and trailing white space.  Lines that are 
 * empty following the removal of comments and leading and trailing 
 * white space will be skipped.
 *
 * <p>A file parser may process a file only once.  Attempting to
 * process the file a second time will generate an exception.
 */
public abstract class FileParser {
    private final DataReader reader;
    private boolean isOpen;

    /**
     * Creates a parser for a given file and comment pattern.
     *
     * @param fileName the name of the file to parse.
     *
     * @param comment the pattern that identifies the start of a
     * single-line or inline comment
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    protected FileParser(String fileName, Pattern comment) {
        this(new File(fileName), comment);
    }

    /**
     * Creates a parser for a given file and comment pattern.
     *
     * @param file the file to parse.
     *
     * @param comment the pattern that identifies the start of a
     * single-line or inline comment
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    protected FileParser(File file, Pattern comment) {
        this.reader = DataReader.open(file, comment);
        this.isOpen = true;
    }

    /**
     * Processes the data file specified in the constructor.
     *
     * <p>Every line in the file is read in order.  Comment text is
     * removed along with leading and trailing white space.  If the
     * line is a <em>data line</em> (is not empty following removal of
     * comment text and leading and trailing white space), it will be
     * passed to {@code processLine(String)}; otherwise, that line of
     * the file will be ignored.
     *
     * <p>Files may be processed only once.  Attempting to process the
     * file a second time will generate an exception.
     *
     * @throws IllegalStateException if the file has already been
     * processed.
     *
     * @throws RuntimeException if any data lines cannot be parsed or
     * otherwise processed.
     */
    public void processFile() {
        if (!isOpen)
            throw new IllegalStateException("The parser is closed.");

        try {
            //
            // Note that the data reader automatically removes comment
            // text and skips blank lines, so each string returned by
            // the reader contains data to be processed...
            //
            for (String line : reader)
                processLine(line);
        }
        finally {
            isOpen = false;
            reader.close();
        }
    }

    /**
     * Processes one data line.
     *
     * <p>Data lines have had comment text and leading and trailing
     * white space removed and are not empty after those operations.
     *
     * @param dataLine the data line to process.
     *
     * @throws RuntimeException if the data line cannot be parsed or
     * otherwise processed.
     */
    protected abstract void processLine(String dataLine);
}
