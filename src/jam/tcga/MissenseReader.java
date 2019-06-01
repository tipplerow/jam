
package jam.tcga;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import jam.ensembl.EnsemblTranscript;
import jam.io.IOUtil;
import jam.lang.JamException;
import jam.peptide.HugoSymbol;
import jam.peptide.ProteinChange;
import jam.util.RegexUtil;

/**
 * Reads missense mutation records sequentially from a MAF file.
 */
public final class MissenseReader implements Closeable, Iterable<MissenseRecord>, Iterator<MissenseRecord> {
    private final File file;
    private final String header;

    private final int patientIDColumn;
    private final int tumorBarcodeColumn;
    private final int hugoSymbolColumn;
    private final int transcriptIDColumn;
    private final int proteinChangeColumn;

    private final BufferedReader reader;

    // The next line to be processed (null when there are no records
    // remaining)...
    private String nextLine;

    private MissenseReader(File file) {
        this.file = file;
        this.reader = IOUtil.openReader(file);
        this.header = readLine();

        this.patientIDColumn     = findOptionalColumn(PatientID.COLUMN_NAME);
        this.tumorBarcodeColumn  = findRequiredColumn(TumorBarcode.COLUMN_NAME);
        this.hugoSymbolColumn    = findRequiredColumn(HugoSymbol.COLUMN_NAME);
        this.transcriptIDColumn  = findRequiredColumn(EnsemblTranscript.COLUMN_NAME);
        this.proteinChangeColumn = findRequiredColumn(ProteinChange.COLUMN_NAME);

        // Advance the next header pointer to the first non-comment
        // line in the file...
        this.nextLine = readLine();
    }

    private String readLine() {
        try {
            return reader.readLine();
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
    }

    private int findOptionalColumn(String columnName) {
        String[] fields = COLUMN_DELIM.split(header);

        for (int column = 0; column < fields.length; ++column)
            if (fields[column].trim().equals(columnName))
                return column;

        return -1;
    }

    private int findRequiredColumn(String columnName) {
        int column = findOptionalColumn(columnName);

        if (column < 0)
            throw JamException.runtime("Column [%s] not found.", columnName);
        else
            return column;
    }

    /**
     * The column delimiter used in all missense mutation files.
     */
    public static Pattern COLUMN_DELIM = RegexUtil.TAB;

    /**
     * Opens a missense mutation file for reading.
     *
     * @param file the path to the missense mutation file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading.
     */
    public static MissenseReader open(File file) {
        return new MissenseReader(file);
    }

    /**
     * Opens a missense mutation file for reading.
     *
     * @param fileName the name of the missense mutation file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading.
     */
    public static MissenseReader open(String fileName) {
        return open(new File(fileName));
    }

    /**
     * Reads all missense mutation records from a given file.
     *
     * @param file the path to the missense mutation file.
     *
     * @return all missense mutation records contained in the given
     * file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static List<MissenseRecord> read(File file) {
        MissenseReader myReader = open(file);
        List<MissenseRecord> myRecords = new ArrayList<MissenseRecord>();

        for (MissenseRecord myRecord : myReader)
            myRecords.add(myRecord);

        return myRecords;
    }

    /**
     * Reads all missense mutation records from a given file.
     *
     * @param fileName the name of the missense mutation file.
     *
     * @return all missense mutation records contained in the given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static List<MissenseRecord> read(String fileName) {
        return read(new File(fileName));
    }

    /**
     * Closes this reader.
     */
    @Override public void close() {
        IOUtil.close(reader);
    }

    /**
     * Identifies the end of the missense mutation file.
     *
     * @return {@code true} iff the file contains another record.
     */
    @Override public boolean hasNext() {
        return nextLine != null;
    }

    /**
     * Reads the next complete missense mutation record from the file.
     *
     * @return the next complete missense mutation record from the file.
     *
     * @throws RuntimeException unless the file contains at least one
     * more properly formatted missense mutation record.
     */
    @Override public MissenseRecord next() {
        if (nextLine == null)
            throw new NoSuchElementException();
            
        MissenseRecord record = createRecord();
        nextLine = readLine();

        return record;
    }

    private MissenseRecord createRecord() {
        String[] fields = COLUMN_DELIM.split(nextLine);

        TumorBarcode      tumorBarcode  = parseTumorBarcode(fields);
        HugoSymbol        hugoSymbol    = parseHugoSymbol(fields);
        EnsemblTranscript transcriptID  = parseTranscriptID(fields);
        ProteinChange     proteinChange = parseProteinChange(fields);
        PatientID         patientID     = parsePatientID(fields, tumorBarcode);

        return new MissenseRecord(patientID,
                                  tumorBarcode,
                                  hugoSymbol,
                                  transcriptID,
                                  proteinChange);
    }

    private TumorBarcode parseTumorBarcode(String[] fields) {
        return TumorBarcode.instance(fields[tumorBarcodeColumn]);
    }

    private HugoSymbol parseHugoSymbol(String[] fields) {
        return HugoSymbol.instance(fields[hugoSymbolColumn]);
    }

    private EnsemblTranscript parseTranscriptID(String[] fields) {
        return EnsemblTranscript.instance(EnsemblTranscript.stripVersion(fields[transcriptIDColumn]));
    }

    private ProteinChange parseProteinChange(String[] fields) {
        return ProteinChange.parse(fields[proteinChangeColumn]);
    }

    private PatientID parsePatientID(String[] fields, TumorBarcode tumorBarcode) {
        if (patientIDColumn < 0)
            return tumorBarcode.patientID();
        else
            return PatientID.instance(fields[patientIDColumn]);
    }

    @Override public Iterator<MissenseRecord> iterator() {
        return this;
    }
}
