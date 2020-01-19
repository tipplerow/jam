
package jam.maf;

import jam.fasta.FastaRecord;
import jam.hugo.HugoSymbol;
import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.tcga.CellFraction;
import jam.tcga.TumorBarcode;
import jam.util.RegexUtil;

/**
 * Encodes the protein sequence arising from one or more mutations in
 * a gene.
 *
 * <p><b>Record format.</b> {@code MAFFastaRecord} objects are created
 * from FASTA records with header lines formatted as follows:
 * <pre>
       &gt;Tumor_Barcode:BARCODE Hugo_Symbol:SYMBOL CCF:0.123
 * </pre>
 */
public final class MAFFastaRecord {
    private final TumorBarcode tumorBarcode;
    private final HugoSymbol   hugoSymbol;
    private final CellFraction cellFraction;
    private final Peptide      peptide;

    private MAFFastaRecord(TumorBarcode tumorBarcode,
                           HugoSymbol   hugoSymbol,
                           CellFraction cellFraction,
                           Peptide      peptide) {
        this.tumorBarcode = tumorBarcode;
        this.hugoSymbol   = hugoSymbol;
        this.cellFraction = cellFraction;
        this.peptide      = peptide;
    }

    /**
     * Creates a new {@code MAFFastaRecord} by parsing the key and
     * comment fields of a FASTA record.
     *
     * <p>The key of the input record must contain the tumor barcode
     * formatted as {@code Tumor_Barcode:BARCODE}; the comment string
     * must contain the HUGO symbol and cancer cell fraction in the
     * format {@code Hugo_Symbol:SYMBOL CCF:0.123}.
     *
     * @param record the FASTA record to parse.
     *
     * @return a new {@code MAFFastaRecord} with the tumor barcode,
     * HUGO symbol, cell fraction, and peptide sequence encoded in
     * the input record.
     *
     * @throws RuntimeException unless the input record contains a
     * properly formatted barcode, HUGO symbol, and cell fraction.
     */
    public static MAFFastaRecord parse(FastaRecord record) {
        TumorBarcode tumorBarcode = parseTumorBarcode(record);
        HugoSymbol   hugoSymbol   = parseHugoSymbol(record);
        CellFraction cellFraction = parseCellFraction(record);

        return new MAFFastaRecord(tumorBarcode, hugoSymbol, cellFraction, record.getPeptide());
    }

    private static TumorBarcode parseTumorBarcode(FastaRecord record) {
        return TumorBarcode.instance(parseField(record.getKey(), TumorBarcode.COLUMN_NAME));
    }

    private static String parseField(String field, String tag) {
        String[] fields = RegexUtil.split(RegexUtil.COLON, field, 2);

        if (!fields[0].equals(tag))
            throw JamException.runtime("Invalid [%s] field: [%s].", tag, field);

        return fields[1];
    }

    private static HugoSymbol parseHugoSymbol(FastaRecord record) {
        return HugoSymbol.instance(parseField(getCommentField(record, 0), HugoSymbol.COLUMN_NAME));
    }

    private static String getCommentField(FastaRecord record, int index) {
        String[] fields = splitComment(record);
        return fields[index];
    }

    private static String[] splitComment(FastaRecord record) {
        return RegexUtil.split(RegexUtil.MULTI_WHITE_SPACE, record.getComment());
    }

    private static CellFraction parseCellFraction(FastaRecord record) {
        return CellFraction.valueOf(parseField(getCommentField(record, 1), CellFraction.COLUMN_NAME));
    }

    /**
     * Returns the tumor barcode for this record.
     *
     * @return the tumor barcode for this record.
     */
    public TumorBarcode getTumorBarcode() {
        return tumorBarcode;
    }

    /**
     * Returns the symbol of the mutated gene for this record.
     *
     * @return the symbol of the mutated gene for this record.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the cancer cell fraction for this record.
     *
     * @return the cancer cell fraction for this record.
     */
    public CellFraction getCellFraction() {
        return cellFraction;
    }

    /**
     * Returns the peptide sequence for this record.
     *
     * @return the peptide sequence for this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }
}
