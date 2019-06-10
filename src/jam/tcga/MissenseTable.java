
package jam.tcga;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamProperties;
import jam.peptide.HugoSymbol;

/**
 * Reads missesnse mutations from a file and stores them in memory
 * indexed by tumor barcode and HUGO symbol.
 */
public final class MissenseTable {
    private final Map<TumorBarcode, Map<HugoSymbol, List<MissenseRecord>>> barcodeMap =
        new HashMap<TumorBarcode, Map<HugoSymbol, List<MissenseRecord>>>();

    private static MissenseTable global = null;

    private MissenseTable(List<MissenseRecord> recordList) {
        fillMap(recordList);
        freezeMap();
    }

    private void fillMap(List<MissenseRecord> recordList) {
        for (MissenseRecord record : recordList)
            processRecord(record);
    }

    private void processRecord(MissenseRecord record) {
        TumorBarcode barcode = record.getTumorBarcode();
        HugoSymbol   symbol  = record.getHugoSymbol();

        Map<HugoSymbol, List<MissenseRecord>> hugoMap = barcodeMap.get(barcode);

        if (hugoMap == null) {
            hugoMap = new HashMap<HugoSymbol, List<MissenseRecord>>();
            barcodeMap.put(barcode, hugoMap);
        }

        List<MissenseRecord> recordList = hugoMap.get(symbol);

        if (recordList == null) {
            recordList = new ArrayList<MissenseRecord>();
            hugoMap.put(symbol, recordList);
        }

        recordList.add(record);
    }

    private void freezeMap() {
        //
        // Replace every list and HUGO map with an unmodifiable
        // version of itself...
        //
        for (Map.Entry<TumorBarcode, Map<HugoSymbol, List<MissenseRecord>>> barcodeEntry : barcodeMap.entrySet()) {
            Map<HugoSymbol, List<MissenseRecord>> hugoMap = barcodeEntry.getValue();

            for (Map.Entry<HugoSymbol, List<MissenseRecord>> hugoEntry : hugoMap.entrySet())
                hugoEntry.setValue(Collections.unmodifiableList(hugoEntry.getValue()));

            barcodeEntry.setValue(Collections.unmodifiableMap(barcodeEntry.getValue()));
        }
    }

    /**
     * Name of the system property that contains the full path name of
     * the file containing the global data table.
     */
    public static final String TABLE_FILE_PROPERTY = "jam.tcga.missenseMAF";

    /**
     * Returns the global data table.
     *
     * @return the global data table.
     *
     * @throws RuntimeException unless the system property with the
     * name given {@code TABLE_FILE_PROPERTY} contains the name of
     * a file with a valid table.
     */
    public static MissenseTable global() {
        if (global == null)
            global = load(resolveFileName());

        return global;
    }

    private static String resolveFileName() {
        return JamProperties.getRequired(TABLE_FILE_PROPERTY);
    }

    /**
     * Populates a table by reading all missense mutation records from
     * a given file.
     *
     * @param file the path to the missense mutation file.
     *
     * @return a table containing all missense mutation records in the
     * given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseTable load(File file) {
        return new MissenseTable(MissenseReader.read(file));
    }

    /**
     * Populates a table by reading all missense mutation records from
     * a given file.
     *
     * @param fileName the path to the missense mutation file.
     *
     * @return a table containing all missense mutation records in the
     * given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseTable load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies tumors contained in this mutation table.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return {@code true} iff this table contains mutations for the
     * specified tumor.
     */
    public boolean contains(TumorBarcode barcode) {
        return barcodeMap.containsKey(barcode);
    }

    /**
     * Identifies tumor-gene pairs contained in this mutation table.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return {@code true} iff this table contains mutations for the
     * specified tumor-gene pair.
     */
    public boolean contains(TumorBarcode barcode, HugoSymbol symbol) {
        return contains(barcode) && barcodeMap.get(barcode).containsKey(symbol) ;
    }

    /**
     * Returns all missense mutations for a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return an immutable map containing all missense mutations for
     * the specified tumor (or an empty map if there are no matching
     * mutations).
     */
    public Map<HugoSymbol, List<MissenseRecord>> lookup(TumorBarcode barcode) {
        Map<HugoSymbol, List<MissenseRecord>> hugoMap = barcodeMap.get(barcode);

        if (hugoMap != null)
            return hugoMap;
        else
            return Collections.emptyMap();
    }

    /**
     * Returns all missense mutations for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO symbol of interest.
     *
     * @return an immutable list containing all missense mutations for
     * the specified tumor and gene (or an empty list if there are no
     * matching mutations).
     */
    public List<MissenseRecord> lookup(TumorBarcode barcode, HugoSymbol symbol) {
        List<MissenseRecord> recordList = lookup(barcode).get(symbol);

        if (recordList != null)
            return recordList;
        else
            return Collections.emptyList();
    }
}
