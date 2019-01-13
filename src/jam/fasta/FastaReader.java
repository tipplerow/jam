
package jam.fasta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jam.io.LineReader;
import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.util.RegexUtil;

/**
 * Reads FASTA records from an input file.
 */
public final class FastaReader {
    private final File file;
    private final LineReader reader;
    private final List<FastaRecord> records;

    private String headerKey;
    private String headerComment;
    private List<String> peptideLines;

    private FastaReader(File file) {
        this.file = file;
        this.reader = LineReader.open(file);
        this.records = new ArrayList<FastaRecord>();
    }

    /**
     * The comment marker.
     */
    public static final Pattern COMMENT_PATTERN = RegexUtil.PYTHON_COMMENT;

    /**
     * Reads FASTA records from an input file.
     *
     * @param file the file to read.
     *
     * @return the FASTA records contained in the file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if it contains improperly formatted peptides.
     */
    public static List<FastaRecord> read(File file) {
        FastaReader reader = new FastaReader(file);
        return reader.read();
    }

    /**
     * Reads FASTA records from an input file.
     *
     * @param fileName the name of the file to read.
     *
     * @return the FASTA records contained in the file.
     *
     * @throws RuntimeException if the file cannot be opened for
     * reading or if it contains improperly formatted peptides.
     */
    public static List<FastaRecord> read(String fileName) {
        return read(new File(fileName));
    }

    private List<FastaRecord> read() {
        reset();

        try {
            for (String line : reader)
                processLine(line);
        }
        finally {
            reader.close();
        }

        addRecord();
        return records;
    }

    private void reset() {
        headerKey = null;
        headerComment = null;
        peptideLines = null;
    }

    private void addRecord() {
        if (headerKey == null)
            throw JamException.runtime("Missing header key.");

        if (headerComment == null)
            throw JamException.runtime("Missing header comment.");

        if (peptideLines == null || peptideLines.isEmpty())
            throw JamException.runtime("Missing peptide data.");

        records.add(new FastaRecord(headerKey, headerComment, Peptide.parse(String.join("", peptideLines))));
        reset();
    }

    private void processLine(String line) {
        line = RegexUtil.stripComment(COMMENT_PATTERN, line);

        if (line.isEmpty())
            return;
        else if (isHeaderLine(line))
            processHeader(line);
        else
            processPeptide(line);
    }

    private static boolean isHeaderLine(String line) {
        return line.startsWith(FastaRecord.HEADER_MARKER);
    }

    private void processHeader(String line) {
        //
        // If a previous header is already in place, we have reached a
        // record boundary and the previous record should be added to
        // the output list...
        //
        if (headerKey != null)
            addRecord();

        // Remove the header marker...
        line = line.substring(FastaRecord.HEADER_MARKER.length());

        // Split into key and comment, delimited by white space...
        String[] fields = FastaRecord.KEY_COMMENT_DELIM.split(line, 2);

        headerKey = fields[0];

        if (fields.length == 2)
            headerComment = fields[1];
        else
            headerComment = "";
    }

    private void processPeptide(String line) {
        //
        // The header must be populated or the file is not properly formatted...
        //
        if (headerKey == null)
            throw JamException.runtime("New peptide data with no header line.");

        if (peptideLines == null)
            peptideLines = new ArrayList<String>();

        peptideLines.add(line);
    }
}
