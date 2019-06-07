
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
 * Reads cancer types from a file and stores them in memory indexed by
 * tumor barcode.
 *
 * <p><b>File format.</b> The data file must contain a header line
 * (which is ignored) and every other line must contain the barcode
 * and cancer type separated by a comma, tab, or pipe character.
 */
public final class CancerTypeDb {
    private final Map<TumorBarcode, CancerType> cancerTypes;

    private static CancerTypeDb global = null;

    private CancerTypeDb() {
        this.cancerTypes = new HashMap<TumorBarcode, CancerType>();
    }

    /**
     * Name of the system property that contains the full path name of
     * the file containing the global cancer type mapping.
     */
    public static final String TYPE_FILE_PROPERTY = "jam.tcga.cancerTypeFile";

    /**
     * Returns the global cancer type database.
     *
     * @return the global cancer type database.
     *
     * @throws RuntimeException unless the system property with the
     * name given by the {@code TYPE_FILE_PROPERTY} contains the name
     * of a file with a valid cancer type mapping.
     */
    public static CancerTypeDb global() {
        if (global == null)
            global = load(resolveFileName());

        return global;
    }

    private static String resolveFileName() {
        return JamProperties.getRequired(TYPE_FILE_PROPERTY);
    }

    /**
     * Loads a cancer type database from a data file.
     *
     * @param file the file to load.
     *
     * @return a cancer type database with the mappings specified in
     * the input file.
     *
     * @throws RuntimeException unless the input file contains valid
     * cancer type mappings.
     */
    public static CancerTypeDb load(File file) {
        CancerTypeDb db = new CancerTypeDb();

        TableReader reader = openReader(file);
        db.load(reader);

        return db;
    }

    /**
     * Loads a cancer type database from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a cancer type database with the mappings specified in
     * the input file.
     *
     * @throws RuntimeException unless the input file contains valid
     * cancer type mappings.
     */
    public static CancerTypeDb load(String fileName) {
        return load(new File(fileName));
    }

    private static TableReader openReader(File file) {
        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Invalid header in cancer type file: [%s].", file);

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

        JamLogger.info("CancerTypeDb: Loaded [%d] cancer types.", size());
    }

    private void parseColumns(List<String> columns) {
        assert columns.size() == 2;

        TumorBarcode  barcode  = TumorBarcode.instance(columns.get(0));
        CancerType cancerType = CancerType.valueOf(columns.get(1));

        if (cancerTypes.containsKey(barcode))
            throw JamException.runtime("Duplicate barcode: [%s]", barcode.getKey());

        cancerTypes.put(barcode, cancerType);
    }

    /**
     * Identifies barcodes with cancer types in this database.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return {@code true} iff this database contains a cancer type
     * for the specified barcode.
     */
    public boolean contains(TumorBarcode barcode) {
        return cancerTypes.containsKey(barcode);
    }

    /**
     * Returns the cancer type for a given barcode.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the cancer type for the specified barcode ({@code null}
     * if the barcode is not in this database).
     */
    public CancerType lookup(TumorBarcode barcode) {
        return cancerTypes.get(barcode);
    }

    /**
     * Returns the cancer type for a given barcode.
     *
     * @param barcode a tumor barcode of interest.
     *
     * @return the cancer type for the specified barcode ({@code null}
     * if the barcode is not in this database).
     *
     * @throws RuntimeException unless the barcode is present.
     */
    public CancerType require(TumorBarcode barcode) {
        CancerType cancerType = lookup(barcode);

        if (cancerType != null)
            return cancerType;
        else
            throw JamException.runtime("No cancer type for barcode [%s].", barcode.getKey());
    }

    /**
     * Returns the number of cancer types in this database.
     *
     * @return the number of cancer types in this database.
     */
    public int size() {
        return cancerTypes.size();
    }

    /**
     * Returns a read-only view of the barcodes in this database.
     *
     * @return an unmodifiable set containing all tumor barcodes
     * from this database.
     */
    public Set<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableSet(cancerTypes.keySet());
    }
}
