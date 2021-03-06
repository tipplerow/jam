
package jam.ensembl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamEnv;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.hugo.HugoSymbol;
import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.util.MapUtil;

/**
 * Manages the human genome data from Ensembl.
 */
public final class EnsemblDb {
    // Protein and transcript keys map uniquely to peptide structures...
    private final Map<EnsemblProtein, EnsemblRecord> proteinRecordMap;
    private final Map<EnsemblTranscript, EnsemblRecord> transcriptRecordMap;

    // ...while gene identifiers may map to multiple peptide structures.
    private final Multimap<HugoSymbol, EnsemblRecord> hugoRecordMap;
    private final Multimap<EnsemblGene, EnsemblRecord> geneRecordMap;

    // The GRCh38 reference Ensembl database defines a mapping from gene to HUGO symbol...
    private final Map<EnsemblGene, HugoSymbol> geneHugoMap;

    private static EnsemblDb reference = null;

    private EnsemblDb() {
        this.proteinRecordMap = new HashMap<EnsemblProtein, EnsemblRecord>();
        this.transcriptRecordMap = new HashMap<EnsemblTranscript, EnsemblRecord>();

        this.hugoRecordMap = HashMultimap.create();
        this.geneRecordMap = HashMultimap.create();

        this.geneHugoMap = new HashMap<EnsemblGene, HugoSymbol>();
    }

    /**
     * Environment variable that defines the absolute path name for
     * the primary Ensembl proteome file.  If the system property
     * {@code jam.ensembl.primaryFile} is also defined, it will
     * override the environment variable.
     */
    public static final String PRIMARY_FILE_ENV = "JAM_ENSEMBL_PRIMARY";

    /**
     * System property that defines the absolute path name for the
     * primary Ensembl proteome file. If not defined, the environment
     * variable {@code PRIMARY_FILE_ENV} will be used by default.
     */
    public static final String PRIMARY_FILE_PROPERTY = "jam.ensembl.primaryFile";

    /**
     * Environment variable that defines the absolute path name for
     * the secondary Ensembl proteome file.  If the system property
     * {@code jam.ensembl.secondaryFile} is also defined, it will
     * override the environment variable.
     */
    public static final String SECONDARY_FILE_ENV = "JAM_ENSEMBL_SECONDARY";

    /**
     * System property that defines the absolute path name for the
     * secondary Ensembl proteome file. If not defined, the environment
     * variable {@code SECONDARY_FILE_ENV} will be used by default.
     */
    public static final String SECONDARY_FILE_PROPERTY = "jam.ensembl.secondaryFile";

    /**
     * Creates a database of Ensembl records from FASTA files.
     *
     * <p>The first file is the <em>primary</em> file: all protein and
     * transcript identifiers in this file must be unique.  Subsequent
     * files are <em>secondary</em> files: they may contain duplicate
     * protein or transcript identifiers, but those records will be
     * ignored.
     *
     * @param fastaFiles files containing the underlying FASTA
     * records.
     *
     * @return the database of Ensembl records.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static EnsemblDb load(String... fastaFiles) {
        if (fastaFiles.length < 1)
            throw new IllegalArgumentException("At least one FASTA file is required.");

        EnsemblDb database = new EnsemblDb();
        database.loadPrimary(fastaFiles[0]);

        for (int index = 1; index < fastaFiles.length; ++index)
            database.loadSecondary(fastaFiles[index]);

        return database;
    }

    private void loadPrimary(String fastaFile) {
        try (FastaReader reader = FastaReader.open(fastaFile)) {
            addPrimaryRecords(reader);
        }
    }

    private void loadSecondary(String fastaFile) {
        try (FastaReader reader = FastaReader.open(fastaFile)) {
            addSecondaryRecords(reader);
        }
    }

    private void addPrimaryRecords(Iterable<FastaRecord> records) {
        for (FastaRecord record : records)
            addPrimaryRecord(record);
    }

    private void addPrimaryRecord(FastaRecord fastaRecord) {
        EnsemblRecord ensemblRecord = EnsemblRecord.parse(fastaRecord);

        mapProtein(ensemblRecord);
        mapTranscript(ensemblRecord);
        mapGene(ensemblRecord);
        mapHugo(ensemblRecord);
    }

    private void mapProtein(EnsemblRecord record) {
        EnsemblProtein protein = record.getEnsemblProtein();

        if (proteinRecordMap.put(protein, record) != null)
            throw JamException.runtime("Duplicate protein key: [%s]", protein);
    }

    private void mapTranscript(EnsemblRecord record) {
        EnsemblTranscript transcript = record.getEnsemblTranscript();

        if (transcriptRecordMap.put(transcript, record) != null)
            throw JamException.runtime("Duplicate transcript key: [%s]", transcript);
    }

    private void mapGene(EnsemblRecord record) {
        //
        // There will be multiple records for a single gene...
        //
        geneRecordMap.put(record.getEnsemblGene(), record);
    }

    private void mapHugo(EnsemblRecord record) {
        HugoSymbol hugo = record.getHugoSymbol();
        EnsemblGene gene = record.getEnsemblGene();

        if (hugo != null) {
            hugoRecordMap.put(hugo, record);
            MapUtil.putUnique(geneHugoMap, gene, hugo);
        }
    }

    private void addSecondaryRecords(Iterable<FastaRecord> records) {
        for (FastaRecord record : records)
            addSecondaryRecord(record);
    }

    private void addSecondaryRecord(FastaRecord fastaRecord) {
        EnsemblRecord ensemblRecord = EnsemblRecord.parse(fastaRecord);

        if (isUniqueProtein(ensemblRecord) && isUniqueTranscript(ensemblRecord))
            addPrimaryRecord(fastaRecord);
    }

    private boolean isUniqueProtein(EnsemblRecord ensemblRecord) {
        return !proteinRecordMap.containsKey(ensemblRecord.getEnsemblProtein());
    }

    private boolean isUniqueTranscript(EnsemblRecord ensemblRecord) {
        return !transcriptRecordMap.containsKey(ensemblRecord.getEnsemblTranscript());
    }

    /**
     * Returns the reference human proteome.
     *
     * @return the reference human proteome.
     */
    public static EnsemblDb reference() {
        if (reference == null)
            reference = loadReference();

        return reference;
    }

    private static EnsemblDb loadReference() {
        String primaryFile = JamProperties.resolve(PRIMARY_FILE_PROPERTY, PRIMARY_FILE_ENV, null);
        String secondaryFile = JamProperties.resolve(SECONDARY_FILE_PROPERTY, SECONDARY_FILE_ENV, "");

        if (secondaryFile.isEmpty())
            return load(primaryFile);
        else
            return load(primaryFile, secondaryFile);
    }

    /**
     * Identifies genes in this map.
     *
     * @param gene a gene of interest.
     *
     * @return {@code true} iff this map contains the specified gene.
     */
    public boolean contains(EnsemblGene gene) {
        return geneRecordMap.containsKey(gene);
    }

    /**
     * Identifies proteins in this map.
     *
     * @param protein a protein of interest.
     *
     * @return {@code true} iff this map contains the specified protein.
     */
    public boolean contains(EnsemblProtein protein) {
        return proteinRecordMap.containsKey(protein);
    }

    /**
     * Identifies transcripts in this map.
     *
     * @param transcript a transcript of interest.
     *
     * @return {@code true} iff this map contains the specified transcript.
     */
    public boolean contains(EnsemblTranscript transcript) {
        return transcriptRecordMap.containsKey(transcript);
    }

    /**
     * Identifies HUGO symbols in this map.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return {@code true} iff this map contains the specified HUGO
     * symbol.
     */
    public boolean contains(HugoSymbol hugo) {
        return hugoRecordMap.containsKey(hugo);
    }

    /**
     * Counts the number of peptides mapped to a given gene.
     *
     * @param gene a gene of interest.
     *
     * @return the number of peptides mapped to the given gene.
     */
    public int count(EnsemblGene gene) {
        return geneRecordMap.get(gene).size();
    }

    /**
     * Counts the number of peptides mapped to a given HUGO symbol.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return the number of peptides mapped to the given HUGO symbol.
     */
    public int count(HugoSymbol hugo) {
        return hugoRecordMap.get(hugo).size();
    }

    /**
     * Returns a read-only view of the records mapped to a given
     * gene.
     *
     * @param gene the gene of interest.
     *
     * @return a read-only collection containing the records mapped
     * to the specified gene (an empty collection if the gene is not
     * mapped).
     */
    public Collection<EnsemblRecord> get(EnsemblGene gene) {
        return Collections.unmodifiableCollection(geneRecordMap.get(gene));
    }

    /**
     * Returns a read-only view of the records mapped to a collection
     * of genes.
     *
     * @param genes the genes of interest.
     *
     * @return a read-only collection containing the records mapped to
     * the specified genes (an empty collection if none of the genes
     * are mapped).
     */
    public Collection<EnsemblRecord> get(Collection<EnsemblGene> genes) {
        List<EnsemblRecord> records = new ArrayList<EnsemblRecord>();

        for (EnsemblGene gene : genes)
            records.addAll(get(gene));

        return records;
    }

    /**
     * Returns the record mapped to a given protein.
     *
     * @param protein the protein of interest.
     *
     * @return the record mapped to the specified protein (or
     * {@code null} if there is no mapping).
     */
    public EnsemblRecord get(EnsemblProtein protein) {
        return proteinRecordMap.get(protein);
    }

    /**
     * Returns the record mapped to a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return the record mapped to the specified transcript (or
     * {@code null} if there is no mapping).
     */
    public EnsemblRecord get(EnsemblTranscript transcript) {
        return transcriptRecordMap.get(transcript);
    }

    /**
     * Returns a read-only view of the records mapped to a given
     * HUGO symbol.
     *
     * @param hugo the HUGO symbol of interest.
     *
     * @return a read-only collection containing the records mapped
     * to the specified HUGO symbol (an empty collection if the HUGO
     * symbol is not mapped).
     */
    public Collection<EnsemblRecord> get(HugoSymbol hugo) {
        return Collections.unmodifiableCollection(hugoRecordMap.get(hugo));
    }

    /**
     * Returns the HUGO symbol mapped to a given gene.
     *
     * @param gene the gene of interest.
     *
     * @return the HUGO symbol mapped to the specified gene, or
     * {@code null} if there is no mapping.
     */
    public HugoSymbol getHugo(EnsemblGene gene) {
        return geneHugoMap.get(gene);
    }

    /**
     * Returns the HUGO symbol mapped to a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return the HUGO symbol mapped to the specified transcript, or
     * {@code null} if there is no mapping.
     */
    public HugoSymbol getHugo(EnsemblTranscript transcript) {
        EnsemblRecord record = get(transcript);

        if (record != null)
            return getHugo(record.getEnsemblGene());
        else
            return null;
    }

    /**
     * Returns a read-only view of the genes in this map.
     *
     * @return a read-only set containing the genes in this map.
     */
    public Set<EnsemblGene> geneSet() {
        return Collections.unmodifiableSet(geneRecordMap.keySet());
    }

    /**
     * Returns a read-only view of the HUGO symbols in this map.
     *
     * @return a read-only set containing the HUGO symbols in this
     * map.
     */
    public Set<HugoSymbol> hugoSet() {
        return Collections.unmodifiableSet(hugoRecordMap.keySet());
    }

    /**
     * Returns a read-only view of the proteins in this map.
     *
     * @return a read-only set containing the proteins in this map.
     */
    public Set<EnsemblProtein> proteinSet() {
        return Collections.unmodifiableSet(proteinRecordMap.keySet());
    }

    /**
     * Returns a read-only view of the transcripts in this map.
     *
     * @return a read-only set containing the transcripts in this map.
     */
    public Set<EnsemblTranscript> transcriptSet() {
        return Collections.unmodifiableSet(transcriptRecordMap.keySet());
    }

    /**
     * Returns the number of records in this database.
     *
     * @return the number of records in this database.
     */
    public int size() {
        return proteinRecordMap.size();
    }
}
