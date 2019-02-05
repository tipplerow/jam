
package jam.io;

import java.io.Closeable;
import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import jam.util.StringUtil;

/**
 * Provides for {@link java.lang.Iterable} file reading, line by line,
 * with all single-line comment text and blank lines excluded and
 * continuation lines joined together.
 *
 * <p>A continuation line ends with a backslash character {@code '\'}.
 * All continuation lines will be appended to the previous line (after
 * adding a leading space character).
 */
public final class DataReader implements Closeable, Iterable<String> {
    private final LineReader reader;
    private final Pattern comment;
    private String nextLine;

    private static final String CONTINUATION = "\\";

    private DataReader(LineReader reader, Pattern comment) {
        this.reader = reader;
        this.comment = comment;
        advance();
    }

    private void advance() {
        nextLine = StringUtil.EMPTY;

        while (reader.hasNext()) {
            String line = reader.next();

            line = StringUtil.removeCommentText(line, comment);
            line = line.trim();

            if (line.isEmpty())
                continue;

            if (isContinuationLine(line)) {
                nextLine = nextLine + formatContinuation(line);
                continue;
            }

            nextLine = nextLine + line;
            break;
        }
    }

    private static boolean isContinuationLine(String line) {
        return line.endsWith(CONTINUATION);
    }

    private static String formatContinuation(String line) {
        //
        // Just need to remove the continuation character...
        //
        return StringUtil.chop(line);
    }

    /**
     * Creates a new reader for a specified file.
     *
     * @param file the file to read.
     *
     * @param comment the pattern that identifies the start of a
     * single-line or inline comment.
     *
     * @return the data reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static DataReader open(File file, Pattern comment) {
        return new DataReader(LineReader.open(file), comment);
    }

    /**
     * Creates a new reader for a specified file.
     *
     * @param fileName the name of the file to read.
     *
     * @param comment the pattern that identifies the start of a
     * single-line or inline comment.
     *
     * @return the data reader for the specified file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static DataReader open(String fileName, Pattern comment) {
        return open(new File(fileName), comment);
    }

    /**
     * Closes the underlying reader.
     */
    @Override public void close() {
        reader.close();
    }

    /**
     * Indicates whether the reader has more lines.
     *
     * @return {@code true} iff there is another line to read.
     */
    public boolean hasNext() {
        return !nextLine.isEmpty();
    }


    /**
     * Returns the next <em>data</em> line (stripped of comment text
     * and skipping blank lines) in the file.
     *
     * @return the next <em>data</em> line in the file.
     *
     * @throws NoSuchElementException if the reader has reached the
     * end of the file.
     */
    public String next() {
        if (!hasNext())
            throw new NoSuchElementException();

        String result = nextLine;
        advance();

        return result;
    }

    @Override public Iterator<String> iterator() {
        return new MyIterator(this);
    }

    private static class MyIterator implements Iterator<String> {
        private final DataReader reader;

        private MyIterator(DataReader reader) {
            this.reader = reader;
        }

        @Override public boolean hasNext() {
            return reader.hasNext();
        }

        @Override public String next() {
            return reader.next();
        }
    }
}
