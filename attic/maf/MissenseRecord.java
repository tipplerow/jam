
package jam.maf;

import java.util.List;

import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;
import jam.peptide.ProteinChange;
import jam.tcga.TumorBarcode;
import jam.tcga.CellFraction;
import jam.util.ListUtil;

/**
 * Encapsulates information contained in one record from a Mutation
 * Annotation Format (MAF) file that describes only <b>missense</b>
 * mutations.
 */
public final class MissenseRecord {
    private final TumorBarcode      tumorBarcode;
    private final EnsemblTranscript transcriptID;
    private final HugoSymbol        hugoSymbol;
    private final ProteinChange     proteinChange;
    private final CellFraction      cellFraction;

    /**
     * Creates a new missense mutation record.
     *
     * @param tumorBarcode the tumor in which the mutation occurred.
     *
     * @param transcriptID the Ensembl identifier for the mutated RNA
     * transcript.
     *
     * @param hugoSymbol the HUGO symbol for the mutated gene.
     *
     * @param proteinChange the description of the single-residue change.
     *
     * @param cellFraction the cancer cell fraction of the mutation.
     */
    public MissenseRecord(TumorBarcode      tumorBarcode,
                          EnsemblTranscript transcriptID,
                          HugoSymbol        hugoSymbol,
                          ProteinChange     proteinChange,
                          CellFraction      cellFraction) {
        this.tumorBarcode  = tumorBarcode;
        this.transcriptID  = transcriptID;
        this.hugoSymbol    = hugoSymbol;
        this.proteinChange = proteinChange;
        this.cellFraction  = cellFraction;
    }

    /**
     * Filters a list of missense records and retains only those with
     * cell fractions above a threshold.
     *
     * @param records the records to filter.
     *
     * @param ccfThreshold the cancer cell fraction threshold.
     *
     * @return a list containing only records with cell fractions
     * above the specified threshold.
     */
    public static List<MissenseRecord> filterCellFraction(List<MissenseRecord> records, CellFraction ccfThreshold) {
        return ListUtil.filter(records, record -> record.getCellFraction().above(ccfThreshold));
    }

    /**
     * Returns the HUGO symbol of the mutated gene.
     *
     * @return the HUGO symbol of the mutated gene.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the description of the single-residue change.
     *
     * @return the description of the single-residue change.
     */
    public ProteinChange getProteinChange() {
        return proteinChange;
    }

    /**
     * Returns the Ensembl identifier for the mutated RNA transcript.
     *
     * @return the Ensembl identifier for the mutated RNA transcript.
     */
    public EnsemblTranscript getTranscriptID() {
        return transcriptID;
    }

    /**
     * Returns the tumor in which the mutation occurred.
     *
     * @return the tumor in which the mutation occurred.
     */
    public TumorBarcode getTumorBarcode() {
        return tumorBarcode;
    }

    /**
     * Returns the cancer cell fraction for the mutation.
     *
     * @return the cancer cell fraction for the mutation.
     */
    public CellFraction getCellFraction() {
        return cellFraction;
    }
}
