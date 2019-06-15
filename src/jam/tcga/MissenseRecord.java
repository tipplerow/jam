
package jam.tcga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jam.app.JamLogger;
import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblRecord;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoMaster;
import jam.hugo.HugoSymbol;
import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.peptide.ProteinChange;

/**
 * Encapsulates information contained in one record from a Mutation
 * Annotation Format (MAF) file that describes only <b>missense</b>
 * mutations.
 */
public final class MissenseRecord {
    private final TumorBarcode      tumorBarcode;
    private final HugoSymbol        hugoSymbol;
    private final EnsemblTranscript transcriptID;
    private final ProteinChange     proteinChange;

    /**
     * Creates a new missense mutation record.
     *
     * @param tumorBarcode the tumor in which the mutation occurred.
     *
     * @param hugoSymbol the HUGO symbol of the mutated protein.
     *
     * @param transcriptID the Ensembl identifier for the mutated RNA
     * transcript.
     *
     * @param proteinChange the description of the single-residue change.
     */
    public MissenseRecord(TumorBarcode      tumorBarcode,
                          HugoSymbol        hugoSymbol,
                          EnsemblTranscript transcriptID,
                          ProteinChange     proteinChange) {
        this.tumorBarcode  = tumorBarcode;
        this.hugoSymbol    = hugoSymbol;
        this.transcriptID  = transcriptID;
        this.proteinChange = proteinChange;
        validate();
    }

    private void validate() {
        Collection<HugoSymbol> symbols =
            HugoMaster.global().getHugo(transcriptID);

        if (!symbols.isEmpty() && !symbols.contains(hugoSymbol))
            JamLogger.warn("Inconsistent HUGO symbol: [%s, %s].", hugoSymbol, transcriptID);
    }

    /**
     * Aggregates all protein changes in a list of records
     * describing mutations in the same tumor and gene.
     *
     * @param records the records to aggregate.
     *
     * @return a list of all protein changes in the specified records.
     *
     * @throws RuntimeException unless all records describe mutations
     * in the same tumor and transcript.
     */
    public static List<ProteinChange> aggregate(List<MissenseRecord> records) {
        switch (records.size()) {
        case 0:
            return Collections.emptyList();

        case 1:
            return List.of(records.get(0).getProteinChange());

        default:
            validateList(records);
            return aggregateValid(records);
        }
    }

    private static void validateList(List<MissenseRecord> records) {
        TumorBarcode barcode0 = records.get(0).getTumorBarcode();
        EnsemblTranscript transcript0 = records.get(0).getTranscriptID();

        for (int index = 1; index < records.size(); ++index) {
            TumorBarcode barcodeI = records.get(index).getTumorBarcode();
            EnsemblTranscript transcriptI = records.get(index).getTranscriptID();

            if (!barcodeI.equals(barcode0))
                throw JamException.runtime("Inconsistent tumor barcodes.");

            if (!transcriptI.equals(transcript0))
                throw JamException.runtime("Inconsistent transcripts.");
        }
    }

    private static List<ProteinChange> aggregateValid(List<MissenseRecord> records) {
        List<ProteinChange> changes = new ArrayList<ProteinChange>(records.size());

        for (MissenseRecord record : records)
            changes.add(record.getProteinChange());

        return changes;
    }

    /**
     * Applies the mutations encoded in a list of records to the
     * germline protein contained in the global Ensembl database.
     *
     * @param records the records to apply.
     *
     * @return the mutated protein.
     *
     * @throws RuntimeException unless all records describe mutations
     * in the same tumor and transcript, the Ensembl database contains
     * a transcript matching the one contained in the records, and all
     * mutations are valid given the germline protein structure.
     */
    public static Peptide apply(List<MissenseRecord> records) {
        return records.get(0).getGermlineProtein().mutate(aggregate(records));
    }

    /**
     * Applies the mutation encoded in this record to the germline
     * protein structure contained in the global Ensembl database.
     *
     * @return the mutated protein.
     *
     * @throws RuntimeException unless the Ensembl database contains a
     * transcript matching this record and the mutation is valid given
     * the germline protein structure.
     */
    public Peptide apply() {
        return getGermlineProtein().mutate(proteinChange);
    }

    /**
     * Returns the germline protein that is altered by this mutation.
     *
     * @return the germline protein that is altered by this mutation.
     *
     * @throws RuntimeException unless the Ensembl database contains a
     * transcript matching this record.
     */
    public Peptide getGermlineProtein() {
        EnsemblRecord ensemblRecord = EnsemblDb.global().get(transcriptID);

        if (ensemblRecord == null)
            throw JamException.runtime("Unmapped transcript: [%s]", transcriptID);

        return ensemblRecord.getPeptide();
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
     * Returns the HUGO symbol of the mutated protein.
     *
     * @return the HUGO symbol of the mutated protein.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
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
     * Returns the description of the single-residue change.
     *
     * @return the description of the single-residue change.
     */
    public ProteinChange getProteinChange() {
        return proteinChange;
    }
}
