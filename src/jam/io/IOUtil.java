
package jam.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import java.util.regex.Pattern;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.util.RegexUtil;

/**
 * Provides utility methods for file I/O operations.
 *
 * <p>All methods wrap checked exceptions ({@code IOException}s) in
 * runtime exceptions.
 */
public final class IOUtil {
    private IOUtil() {}

    /**
     * Closes a closeable object and ignores all exceptions.
     *
     * @param closeable the object to close.
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        }
        catch (Exception ex) {
            JamLogger.warn(ex.getMessage());
        }
    }

    /**
     * Flushes a flushable object and ignores all exceptions.
     *
     * @param flushable the object to flush.
     */
    public static void flush(Flushable flushable) {
        try {
            if (flushable != null)
                flushable.flush();
        }
        catch (Exception ex) {
            JamLogger.warn(ex.getMessage());
        }
    }

    /**
     * Reads the next data line (not empty after comment text and
     * leading and trailing white space has been removed) from a file.
     *
     * @param reader an open file reader.
     *
     * @param comment the delimeter marking the beginning of comment
     * text: all text from the delimiter to the end of the line is
     * considered as comment text and removed.
     *
     * @return the next data line from the input file; {@code null} if
     * the reader reaches the end of the file.
     */
    public static String nextDataLine(BufferedReader reader, Pattern comment) {
        String line = null;

        try {
            while (true) {
                line = reader.readLine();

                if (line == null)
                    break;

                line = RegexUtil.stripComment(comment, line);

                if (!line.isEmpty())
                    break;
            }
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }

        return line;
    }

    /**
     * Opens a reader for a file.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate reader
     * will be returned.
     *
     * @param fileName the name of the file to read.
     *
     * @return a reader for the specified file.
     *
     * @throws RuntimeException unless the file is open for reading.
     */
    public static BufferedReader openReader(String fileName) {
        return openReader(new File(fileName));
    }

    /**
     * Opens a reader for a file.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate reader
     * will be returned.
     *
     * @param directory the directory in which the file resides.
     *
     * @param baseName the base name of the file to read.
     *
     * @return a reader for the specified file.
     *
     * @throws RuntimeException unless the file is open for reading.
     */
    public static BufferedReader openReader(File directory, String baseName) {
        return openReader(new File(directory, baseName));
    }

    /**
     * Opens a reader for a file.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate reader
     * will be returned.
     *
     * @param file the file to read.
     *
     * @return a reader for the specified file.
     *
     * @throws RuntimeException unless the file is open for reading.
     */
    public static BufferedReader openReader(File file) {
        if (ZipUtil.isGZipFile(file))
            return ZipUtil.openGZipReader(file);

        try {
            return new BufferedReader(new FileReader(file));
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
    }

    /**
     * Opens a buffered reader for an input stream.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * @param stream an open input stream.
     *
     * @return a reader for the specified stream.
     *
     * @throws RuntimeException unless the file is open for reading.
     */
    public static BufferedReader openReader(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream));
    }

    /**
     * Opens a writer for a file, creates any subdirectories in the
     * file path that do not already exist, and truncates the file if
     * it already exists.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate writer
     * will be returned.
     *
     * @param fileName the name of the file to write (and truncate if
     * it already exists).
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    public static PrintWriter openWriter(String fileName) {
        return openWriter(fileName, false);
    }

    /**
     * Opens a writer for a file and creates any subdirectories in
     * the file path that do not already exist.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate writer
     * will be returned.
     *
     * @param fileName the name of the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    public static PrintWriter openWriter(String fileName, boolean append) {
        return openWriter(new File(fileName), append);
    }

    /**
     * Opens a writer for a file, creates any subdirectories in the
     * file path that do not already exist, and truncates the contents
     * of the file if it already exists.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate writer
     * will be returned.
     *
     * @param file the file to write (and truncate if it already
     * exists).
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    public static PrintWriter openWriter(File file) {
        return openWriter(file, false);
    }

    /**
     * Opens a writer for a file, creates any subdirectories in the
     * file path that do not already exist, and truncates the contents
     * of the file if it already exists.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate writer
     * will be returned.
     *
     * @param directory the directory in which to write the file.
     * exists).
     *
     * @param baseName the base name of the file to write (and
     * truncate if it already exists).
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    public static PrintWriter openWriter(File directory, String baseName) {
        return openWriter(new File(directory, baseName), false);
    }

    /**
     * Opens a writer for a file and creates any subdirectories in
     * the file path that do not already exist.
     *
     * <p>This method is provided to encapsulate and centralize the
     * standard chaining of readers.
     *
     * <p>If the file name ends in the GZIP suffix ({@code .gz}), the
     * file will assumed to be a GZIP file and an appropriate writer
     * will be returned.
     *
     * @param file the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    public static PrintWriter openWriter(File file, boolean append) {
        if (ZipUtil.isGZipFile(file))
            return ZipUtil.openGZipWriter(file);

        try {
            FileUtil.ensureParentDirs(file);
            return new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
    }

    /**
     * Reads all lines from a text file.
     *
     * @param fileName the name of the file to read.
     *
     * @return a list containing the lines from the file, each line as
     * a separate element with the order maintained.
     *
     * @throws RuntimeException unless the file was read successfully.
     */
    public static List<String> readLines(String fileName) {
        return readLines(new File(fileName));
    }

    /**
     * Reads all lines from a text file.
     *
     * @param file the file to read.
     *
     * @return a list containing the lines from the file, each line as
     * a separate element with the order maintained.
     *
     * @throws RuntimeException unless the file was read successfully.
     */
    public static List<String> readLines(File file) {
        List<String> lines = null;
        BufferedReader reader = openReader(file);

        try {
            lines = readLines(reader);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
        finally {
            close(reader);
        }

        return lines;
    }

    private static List<String> readLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<String>();

        while (true) {
            String line = reader.readLine();
            
            if (line != null)
                lines.add(line);
            else
                break;
        }

        return lines;
    }

    /**
     * Writes objects to a text file.
     *
     * @param fileName the name of the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @param objects the objects to write.
     *
     * @throws RuntimeException unless the file was written successfully.
     */
    public static void writeFile(String fileName, boolean append, Collection<Object> objects) {
        writeFile(new File(fileName), append, objects);
    }

    /**
     * Writes objects to a text file.
     *
     * @param file the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @param objects the objects to write.
     *
     * @throws RuntimeException unless the file was read successfully.
     */
    public static void writeFile(File file, boolean append, Collection<Object> objects) {
        PrintWriter writer = openWriter(file, append);

        for (Object object : objects)
            writer.println(object);

        close(writer);
    }

    /**
     * Writes objects to a text file.
     *
     * @param fileName the name of the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @param objects the objects to write.
     *
     * @throws RuntimeException unless the file was written successfully.
     */
    public static void writeFile(String fileName, boolean append, Object... objects) {
        writeFile(fileName, append, Arrays.asList(objects));
    }

    /**
     * Writes objects to a text file.
     *
     * @param file the file to write.
     *
     * @param append whether to write at the end of the file instead
     * of the beginning.
     *
     * @param objects the objects to write.
     *
     * @throws RuntimeException unless the file was read successfully.
     */
    public static void writeFile(File file, boolean append, Object... objects) {
        writeFile(file, append, Arrays.asList(objects));
    }
}

