
package jam.fasta;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import jam.io.IOUtil;
import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.util.RegexUtil;

/**
 * Reads FASTA records sequentially from a FASTA-formatted file.
 */
public final class FastaReader implements Closeable, Iterable<FastaRecord>, Iterator<FastaRecord> {
    private final File file;
    private final BufferedReader reader;

    // The next header line to be processed (null when there are no
    // records remaining)...
    private String nextHeaderLine;

    private FastaReader(File file) {
        this.file = file;
        this.reader = IOUtil.openReader(file);

        // Advance the next header pointer to the first non-comment
        // line in the file...
        this.nextHeaderLine = nextDataLine();
    }

    private String nextDataLine() {
        return IOUtil.nextDataLine(reader, COMMENT_PATTERN);
    }

    /**
     * The comment character for FASTA files.
     */
    public static final Pattern COMMENT_PATTERN = RegexUtil.SEMICOLON;

    /**
     * Opens a FASTA file for reading.
     *
     * @param file the path to the FASTA file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static FastaReader open(File file) {
        return new FastaReader(file);
    }

    /**
     * Opens a FASTA file for reading.
     *
     * @param fileName the name of the FASTA file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading.
     */
    public static FastaReader open(String fileName) {
        return open(new File(fileName));
    }

    /**
     * Reads all FASTA records from a given file.
     *
     * @param file the path to the FASTA file.
     *
     * @return all FASTA records contained in the given file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if the file contains 
     */
    public static List<FastaRecord> read(File file) {
        FastaReader myReader = open(file);
        List<FastaRecord> myRecords = new ArrayList<FastaRecord>();

        for (FastaRecord myRecord : myReader)
            myRecords.add(myRecord);

        return myRecords;
    }

    /**
     * Reads all FASTA records from a given file.
     *
     * @param fileName the name of the FASTA file.
     *
     * @return all FASTA records contained in the given file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if the file contains 
     */
    public static List<FastaRecord> read(String fileName) {
        return read(new File(fileName));
    }

    /**
     * Identifies header lines in FASTA files.
     *
     * @param line a line from a FASTA file.
     *
     * @return {@code true} iff the specified line is a header line.
     */
    public static boolean isHeaderLine(String line) {
        return line.startsWith(FastaRecord.HEADER_MARKER);
    }

    /**
     * Closes this reader.
     */
    @Override public void close() {
        IOUtil.close(reader);
    }

    /**
     * Identifies the end of the FASTA file.
     *
     * @return {@code true} iff the FASTA file contains another FASTA
     * record.
     */
    @Override public boolean hasNext() {
        return nextHeaderLine != null;
    }

    /**
     * Reads the next complete FASTA record from the file.
     *
     * @return the next complete FASTA record from the file.
     *
     * @throws RuntimeException unless the file contains at least one
     * more properly formatted FASTA record.
     */
    @Override public FastaRecord next() {
        if (nextHeaderLine != null)
            return createRecord(nextHeaderLine, readPeptideLines());
        else
            throw new NoSuchElementException();
    }

    private List<String> readPeptideLines() {
        List<String> peptideLines = new ArrayList<String>();

        while (true) {
            String line = nextDataLine();

            if (line == null) {
                nextHeaderLine = null;
                break;
            }
            else if (isHeaderLine(line)) {
                nextHeaderLine = line;
                break;
            }
            else {
                peptideLines.add(line);
            }
        }

        return peptideLines;
    }

    private static FastaRecord createRecord(String headerLine, List<String> peptideLines) {
        if (!isHeaderLine(headerLine))
            throw JamException.runtime("Expected a record header line but found [%s].", headerLine);

        if (peptideLines.isEmpty())
            throw JamException.runtime("No peptides for record [%s].", headerLine);

        // Remove the header marker...
        headerLine = headerLine.substring(FastaRecord.HEADER_MARKER.length());

        // Split into key and comment, delimited by white space...
        String[] fields = FastaRecord.KEY_COMMENT_DELIM.split(headerLine, 2);

        String  key     = fields[0];
        String  descrip = (fields.length == 2) ? fields[1] : "";
        Peptide peptide = Peptide.parse(String.join("", peptideLines));

        return new FastaRecord(key, descrip, peptide);
    }

    @Override public Iterator<FastaRecord> iterator() {
        return this;
    }
}
