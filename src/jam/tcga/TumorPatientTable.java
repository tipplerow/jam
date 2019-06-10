
package jam.tcga;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.TableReader;
import jam.lang.JamException;

/**
 * Maps tumor barcodes to the sampled patient in the cohort.
 *
 * <p><b>File format.</b> The data file must contain a header line
 * (which is ignored) and every other line must contain the barcode
 * and patient key separated by a comma, tab, or pipe character.
 */
public final class TumorPatientTable {
    private final Map<TumorBarcode, PatientID> map;

    private static TumorPatientTable global = null;

    private TumorPatientTable() {
        this.map = new HashMap<TumorBarcode, PatientID>();
    }

    /**
     * Name of the system property that contains the full path name of
     * the file containing the global data table..
     */
    public static final String TABLE_FILE_PROPERTY = "jam.tcga.tumorPatientTable";

    /**
     * Returns the global data table.
     *
     * @return the global data table.
     *
     * @throws RuntimeException unless the system property with the
     * name given by {@code TABLE_FILE_PROPERTY} contains the name of
     * a file with a valid table.
     */
    public static TumorPatientTable global() {
        if (global == null)
            global = load(resolveFileName());

        return global;
    }

    private static String resolveFileName() {
        return JamProperties.getRequired(TABLE_FILE_PROPERTY);
    }

    /**
     * Loads a table from a data file.
     *
     * @param file the file to load.
     *
     * @return a table with the mappings specified in the input file.
     *
     * @throws RuntimeException unless the input file contains a valid
     * data table.
     */
    public static TumorPatientTable load(File file) {
        TumorPatientTable table = new TumorPatientTable();

        TableReader reader = openReader(file);
        table.load(reader);

        return table;
    }

    /**
     * Loads a table from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a table with the mappings specified in the input file.
     *
     * @throws RuntimeException unless the input file contains a valid
     * data table.
     */
    public static TumorPatientTable load(String fileName) {
        return load(new File(fileName));
    }

    private static TableReader openReader(File file) {
        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Invalid header in file: [%s].", file);

        return reader;
    }

    private void load(TableReader reader) {
        try {
            for (List<String> columns : reader)
                parseColumns(columns);
        }
        finally {
            reader.close();
        }

        JamLogger.info("TumorPatientTable: Loaded [%d] records.", size());
    }

    private void parseColumns(List<String> columns) {
        assert columns.size() == 2;

        TumorBarcode barcode   = TumorBarcode.instance(columns.get(0));
        PatientID    patientID = PatientID.instance(columns.get(1));

        if (map.containsKey(barcode))
            throw JamException.runtime("Duplicate key: [%s]", barcode.getKey());

        map.put(barcode, patientID);
    }

    /**
     * Identifies tumors in this table.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this table contains the specified
     * tumor.
     */
    public boolean contains(TumorBarcode barcode) {
        return map.containsKey(barcode);
    }

    /**
     * Returns the sampled patient for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the sampled patient for the specified barcode 
     * ({@code null} if the barcode is not in this table).
     */
    public PatientID lookup(TumorBarcode barcode) {
        return map.get(barcode);
    }

    /**
     * Returns the sampled patient for a given tumor.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the sampled patient for the specified barcode 
     * ({@code null} if the barcode is not in this table).
     *
     * @throws RuntimeException unless the barcode is present.
     */
    public PatientID require(TumorBarcode barcode) {
        PatientID patientID = lookup(barcode);

        if (patientID != null)
            return patientID;
        else
            throw JamException.runtime("No patient mapped to barcode [%s].", barcode.getKey());
    }

    /**
     * Returns the number of tumors in this table.
     *
     * @return the number of tumors in this table.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a read-only view of the barcodes in this table.
     *
     * @return an unmodifiable set containing all barcodes in this
     * table.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
