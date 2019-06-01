
package jam.tcga;

import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblRecord;
import jam.ensembl.EnsemblTranscript;
import jam.lang.JamException;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;
import jam.peptide.ProteinChange;

/**
 * Encapsulates information contained in one record from a Mutation
 * Annotation Format (MAF) file that describes only <b>missense</b>
 * mutations.
 */
public final class MissenseRecord {
    private final PatientID         patientID;
    private final TumorBarcode      tumorBarcode;
    private final HugoSymbol        hugoSymbol;
    private final EnsemblTranscript transcriptID;
    private final ProteinChange     proteinChange;

    /**
     * Creates a new missense mutation record.
     *
     * @param patientID the patient from which the tumor was sampled.
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
    public MissenseRecord(PatientID         patientID,
                          TumorBarcode      tumorBarcode,
                          HugoSymbol        hugoSymbol,
                          EnsemblTranscript transcriptID,
                          ProteinChange     proteinChange) {
        this.patientID     = patientID;
        this.tumorBarcode  = tumorBarcode;
        this.hugoSymbol    = hugoSymbol;
        this.transcriptID  = transcriptID;
        this.proteinChange = proteinChange;
    }

    /**
     * Applies the mutation encoded in this record to the peptide
     * contained in the global Ensembl database.
     *
     * @return the mutated peptide.
     *
     * @throws RuntimeException unless the global database contains
     * a transcript matching the one contained in this record and the
     * mutation is valid given the reference protein structure.
     */
    public Peptide apply() {
        return apply(EnsemblDb.global());
    }

    /**
     * Applies the mutation encoded in this record to the peptide
     * contained in a reference database.
     *
     * @param ensemblDb an Ensembl reference database.
     *
     * @return the mutated peptide.
     *
     * @throws RuntimeException unless the reference database contains
     * a transcript matching the one contained in this record and the
     * mutation is valid given the reference protein structure.
     */
    public Peptide apply(EnsemblDb ensemblDb) {
        EnsemblRecord ensemblRecord = ensemblDb.get(transcriptID);

        if (ensemblRecord == null)
            throw JamException.runtime("Unmapped transcript: [%s]", transcriptID);

        if (!ensemblRecord.getHugoSymbol().equals(hugoSymbol))
            throw JamException.runtime("Mismatched HUGO symbols: [%s vs %s]",
                                       hugoSymbol, ensemblRecord.getHugoSymbol());

        return ensemblRecord.getPeptide().mutate(proteinChange);
    }

    /**
     * Returns the patient from which the tumor was sampled.
     *
     * @return the patient from which the tumor was sampled.
     */
    public PatientID getPatientID() {
        return patientID;
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
