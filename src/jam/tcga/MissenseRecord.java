
package jam.tcga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.ensembl.EnsemblAssembly;
import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblGene;
import jam.ensembl.EnsemblRecord;
import jam.ensembl.EnsemblTranscript;
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
    private final CellFraction      cellFraction;

    private static EnsemblAssembly assembly = null;

    private MissenseRecord(TumorBarcode      tumorBarcode,
                           HugoSymbol        hugoSymbol,
                           EnsemblTranscript transcriptID,
                           ProteinChange     proteinChange,
                           CellFraction      cellFraction) {
        this.tumorBarcode  = tumorBarcode;
        this.hugoSymbol    = hugoSymbol;
        this.transcriptID  = transcriptID;
        this.proteinChange = proteinChange;
        this.cellFraction  = cellFraction;
    }

    /**
     * Name of the system property that defines the genome assembly
     * from which the transcripts are derived.
     */
    public static final String ASSEMBLY_PROPERTY = "jam.tcga.missenseAssembly";

    /**
     * Returns the governing Ensembl assembly from which transcripts
     * are derived.
     *
     * @return the governing Ensembl assembly from which transcripts
     * are derived.
     */
    public static EnsemblAssembly assembly() {
        if (assembly == null)
            assembly = resolveAssembly();

        return assembly;
    }

    private static EnsemblAssembly resolveAssembly() {
        return JamProperties.getRequiredEnum(ASSEMBLY_PROPERTY, EnsemblAssembly.class);
    }

    /**
     * Creates a new missense mutation record.
     *
     * <p>The HUGO symbol is resolved by mapping the transcript to its
     * gene in the governing genome assembly, then mapping the gene to
     * a HUGO symbol using the reference Ensembl database.
     *
     * @param tumorBarcode the tumor in which the mutation occurred.
     *
     * @param transcriptID the Ensembl identifier for the mutated RNA
     * transcript.
     *
     * @param proteinChange the description of the single-residue change.
     *
     * @param cellFraction the fraction of cancer cells carrying the
     * mutation.
     *
     * @return the corresponding missense record.
     */
    public static MissenseRecord create(TumorBarcode      tumorBarcode,
                                        EnsemblTranscript transcriptID,
                                        ProteinChange     proteinChange,
                                        CellFraction      cellFraction) {
        return new MissenseRecord(tumorBarcode,
                                  resolveHugoSymbol(transcriptID),
                                  transcriptID,
                                  proteinChange,
                                  cellFraction);
    }

    /**
     * Creates a new missense mutation record.
     *
     * @param tumorBarcode the tumor in which the mutation occurred.
     *
     * @param hugoSymbol the HUGO symbol for the mutated gene.
     *
     * @param transcriptID the Ensembl identifier for the mutated RNA
     * transcript.
     *
     * @param proteinChange the description of the single-residue change.
     *
     * @param cellFraction the fraction of cancer cells carrying the
     * mutation.
     *
     * @return the corresponding missense record.
     */
    public static MissenseRecord create(TumorBarcode      tumorBarcode,
                                        HugoSymbol        hugoSymbol,
                                        EnsemblTranscript transcriptID,
                                        ProteinChange     proteinChange,
                                        CellFraction      cellFraction) {
        return new MissenseRecord(tumorBarcode,
                                  hugoSymbol,
                                  transcriptID,
                                  proteinChange,
                                  cellFraction);
    }

    /**
     * Maps a transcript identifier to a HUGO symbol.
     *
     * <p>The HUGO symbol is resolved by mapping the transcript to its
     * gene in the governing genome assembly, then mapping the gene to
     * a HUGO symbol using the reference Ensembl database.
     *
     * @param transcriptID the transcript to map.
     *
     * @return the HUGO symbol for the corresponding gene.
     */
    public static HugoSymbol resolveHugoSymbol(EnsemblTranscript transcriptID) {
        //
        // Must use the missense assembly to look up the gene
        // corresponding to the transcript, then the reference
        // assembly to map the gene to a HUGO symbol...
        //
        EnsemblRecord ensemblRecord = assembly().db().get(transcriptID);

        if (ensemblRecord == null)
            throw JamException.runtime("Transcript [%s] is not in the genome assembly [%s].", transcriptID, assembly());

        EnsemblGene ensemblGene = ensemblRecord.getEnsemblGene();
        HugoSymbol  hugoSymbol  = EnsemblDb.reference().getHugo(ensemblGene);

        if (hugoSymbol != null)
            return hugoSymbol;
        else
            throw JamException.runtime("Transcript/gene [%s/%s] is not mapped to a HUGO symbol.", transcriptID, ensemblGene);
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
     * germline protein contained in the governing Ensembl database.
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
        Peptide germlineProtein = records.get(0).getGermlineProtein();
        return germlineProtein.mutate(aggregate(records));
    }

    /**
     * Applies the mutation encoded in this record to the germline
     * protein structure contained in the governing Ensembl database.
     *
     * @return the mutated protein.
     *
     * @throws RuntimeException unless the governing Ensembl database
     * contains a transcript matching this record and the mutation is
     * valid given the germline protein structure.
     */
    public Peptide apply() {
        return getGermlineProtein().mutate(proteinChange);
    }

    /**
     * Returns the germline protein that is altered by this mutation.
     *
     * @return the germline protein that is altered by this mutation.
     *
     * @throws RuntimeException unless the governing Ensembl database
     * contains a transcript matching this record.
     */
    public Peptide getGermlineProtein() {
        EnsemblRecord ensemblRecord = assembly().db().get(transcriptID);

        if (ensemblRecord == null)
            throw JamException.runtime("Unmapped transcript: [%s] (assembly [%s])", transcriptID, assembly);

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

    /**
     * Returns the fraction of cancer cells carrying this mutation.
     *
     * @return the fraction of cancer cells carrying this mutation.
     */
    public CellFraction getCellFraction() {
        return cellFraction;
    }
}
