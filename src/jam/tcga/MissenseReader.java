
package jam.tcga;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.peptide.ProteinChange;
import jam.util.RegexUtil;

/**
 * Reads missense mutation records sequentially from a MAF file.
 */
public final class MissenseReader implements Closeable, Iterable<MissenseRecord>, Iterator<MissenseRecord> {
    private final TableReader reader;

    private final int tumorBarcodeColumn;
    private final int hugoSymbolColumn;
    private final int transcriptIDColumn;
    private final int proteinChangeColumn;

    private MissenseReader(TableReader reader) {
        this.reader = reader;

        this.tumorBarcodeColumn  = findColumn(TumorBarcode.COLUMN_NAME);
        this.hugoSymbolColumn    = findColumn(HugoSymbol.COLUMN_NAME);
        this.transcriptIDColumn  = findColumn(EnsemblTranscript.COLUMN_NAME);
        this.proteinChangeColumn = findColumn(ProteinChange.COLUMN_NAME);
    }

    private int findColumn(String columnName) {
        List<String> columnKeys = reader.columnKeys();

        for (int columnIndex = 0; columnIndex < columnKeys.size(); ++columnIndex)
            if (columnKeys.get(columnIndex).equals(columnName))
                return columnIndex;

        throw JamException.runtime("Column [%s] not found.", columnName);
    }

    /**
     * Opens a missense mutation file for reading.
     *
     * @param file the path to the missense mutation file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains a valid header line.
     */
    public static MissenseReader open(File file) {
        return new MissenseReader(TableReader.open(file));
    }

    /**
     * Opens a missense mutation file for reading.
     *
     * @param fileName the name of the missense mutation file.
     *
     * @return the opened reader.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains a valid header line.
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
        reader.close();
    }

    /**
     * Identifies the end of the missense mutation file.
     *
     * @return {@code true} iff the file contains another record.
     */
    @Override public boolean hasNext() {
        return reader.hasNext();
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
        return createRecord(reader.next());
    }

    private MissenseRecord createRecord(List<String> fields) {
        TumorBarcode      tumorBarcode  = parseTumorBarcode(fields);
        HugoSymbol        hugoSymbol    = parseHugoSymbol(fields);
        EnsemblTranscript transcriptID  = parseTranscriptID(fields);
        ProteinChange     proteinChange = parseProteinChange(fields);

        return new MissenseRecord(tumorBarcode,
                                  hugoSymbol,
                                  transcriptID,
                                  proteinChange);
    }

    private TumorBarcode parseTumorBarcode(List<String> fields) {
        return TumorBarcode.instance(fields.get(tumorBarcodeColumn));
    }

    private HugoSymbol parseHugoSymbol(List<String> fields) {
        return HugoSymbol.instance(fields.get(hugoSymbolColumn));
    }

    private EnsemblTranscript parseTranscriptID(List<String> fields) {
        return EnsemblTranscript.instance(EnsemblTranscript.stripVersion(fields.get(transcriptIDColumn)));
    }

    private ProteinChange parseProteinChange(List<String> fields) {
        return ProteinChange.parse(fields.get(proteinChangeColumn));
    }

    @Override public Iterator<MissenseRecord> iterator() {
        return this;
    }
}
