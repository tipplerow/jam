
package jam.tcga;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.peptide.HugoSymbol;

/**
 * Reads missesnse mutations from a file and stores them in memory
 * indexed by tumor barcode and HUGO symbol.
 */
public final class MissenseDb {
    private final Map<TumorBarcode, Map<HugoSymbol, List<MissenseRecord>>> barcodeMap =
        new HashMap<TumorBarcode, Map<HugoSymbol, List<MissenseRecord>>>();

    private MissenseDb(List<MissenseRecord> recordList) {
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
     * Populates a database by reading all missense mutation records
     * from a given file.
     *
     * @param file the path to the missense mutation file.
     *
     * @return a database containing all missense mutation records in
     * the given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseDb load(File file) {
        return new MissenseDb(MissenseReader.read(file));
    }

    /**
     * Populates a database by reading all missense mutation records
     * from a given file.
     *
     * @param fileName the path to the missense mutation file.
     *
     * @return a database containing all missense mutation records in
     * the given file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted records.
     */
    public static MissenseDb load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Identifies tumors contained in this mutation database.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return {@code true} iff this database contains mutations for
     * the specified tumor.
     */
    public boolean contains(TumorBarcode barcode) {
        return barcodeMap.containsKey(barcode);
    }

    /**
     * Identifies tumor-gene pairs contained in this mutation database.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return {@code true} iff this database contains mutations for
     * the specified tumor-gene pair.
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
