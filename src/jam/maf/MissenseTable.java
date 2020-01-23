
package jam.maf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;
import jam.lang.JamException;
import jam.tcga.TumorBarcode;

/**
 * Indexes missesnse mutations by tumor barcode and HUGO symbol.
 */
public final class MissenseTable {
    //
    // Derive class-specific containers to avoid messy generics syntax
    // (three angle brackets!?!)...
    //
    private static final class RecordList extends ArrayList<MissenseRecord> {}
    private static final class HugoMap    extends HashMap<HugoSymbol, RecordList> {}
    private static final class BarcodeMap extends HashMap<TumorBarcode, HugoMap> {}

    // All mutations indexed by barcode (outer) and symbol (inner)...
    private final BarcodeMap barcodeMap = new BarcodeMap();

    private MissenseTable(Collection<MissenseRecord> records) {
        fillMap(records);
    }

    private void fillMap(Collection<MissenseRecord> records) {
        for (MissenseRecord record : records)
            addRecord(record);
    }

    private void addRecord(MissenseRecord record) {
        HugoSymbol   symbol  = record.getHugoSymbol();
        TumorBarcode barcode = record.getTumorBarcode();

        addRecord(record, recordList(barcode, symbol));
    }

    private static void addRecord(MissenseRecord record, RecordList recordList) {
        validateRecord(record, recordList);
        recordList.add(record);
    }

    private static void validateRecord(MissenseRecord record, RecordList recordList) {
        //
        // Ensure that all mutations for the same tumor sample and
        // gene are annoted with reference to the RNA transcript...
        //
        if (!recordList.isEmpty() && !record.getTranscriptID().equals(recordList.get(0).getTranscriptID()))
            throw JamException.runtime("Inconsistent Ensembl transcripts for [%s:%s].",
                                       record.getTumorBarcode().getKey(),
                                       record.getHugoSymbol().getKey());
    }

    private RecordList recordList(TumorBarcode barcode, HugoSymbol symbol) {
        HugoMap    hugoMap    = hugoMap(barcode);
        RecordList recordList = hugoMap.get(symbol);

        if (recordList == null) {
            recordList = new RecordList();
            hugoMap.put(symbol, recordList);
        }

        return recordList;
    }

    private HugoMap hugoMap(TumorBarcode barcode) {
        HugoMap hugoMap = barcodeMap.get(barcode);

        if (hugoMap == null) {
            hugoMap = new HugoMap();
            barcodeMap.put(barcode, hugoMap);
        }

        return hugoMap;
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
        List<MissenseRecord> records = MissenseParser.parse(fileName);
        JamLogger.info("MissenseTable: Loaded [%d] records.", records.size());
        
        return load(records);
    }

    /**
     * Populates a table from a collection of missense mutation
     * records.
     *
     * @param records the records to be indexed in the table.
     *
     * @return a table containing all missense mutation records in
     * the given file.
     *
     * @throws RuntimeException unless all Ensembl transcripts are
     * identical for records with the same tumor sample and gene.
     */
    public static MissenseTable load(Collection<MissenseRecord> records) {
        return new MissenseTable(records);
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
     * Counts the total number of missense mutations in a given tumor.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return the total number of missense mutations in the specified
     * tumor.
     */
    public int count(TumorBarcode barcode) {
        HugoMap hugoMap = barcodeMap.get(barcode);

        if (hugoMap == null)
            return 0;

        int total = 0;

        for (RecordList records : hugoMap.values())
            total += records.size();

        return total;
    }

    /**
     * Counts the number of missense mutations for a given tumor
     * and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the gene of interest.
     *
     * @return the number of missense mutations for the specified
     * tumor and gene.
     */
    public int count(TumorBarcode barcode, HugoSymbol symbol) {
        return lookup(barcode, symbol).size();
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
        HugoMap hugoMap = barcodeMap.get(barcode);

        if (hugoMap != null)
            return Collections.unmodifiableMap(hugoMap);
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
            return Collections.unmodifiableList(recordList);
        else
            return Collections.emptyList();
    }

    /**
     * Returns a read-only view of all tumor barcodes in this table.
     *
     * @return a read-only view of all tumor barcodes in this table.
     */
    public Collection<TumorBarcode> viewBarcodes() {
        return Collections.unmodifiableCollection(barcodeMap.keySet());
    }
}
