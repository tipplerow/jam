
package jam.ensembl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamEnv;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.hugo.HugoSymbol;
import jam.io.IOUtil;
import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.lang.JamException;
import jam.peptide.Peptide;

/**
 * Manages the human genome data from Ensembl.
 */
public final class EnsemblDb {
    //
    // Protein and transcript keys map uniquely to peptide
    // structures...
    //
    private final Map<EnsemblProtein, EnsemblRecord> proteinMap =
        new HashMap<EnsemblProtein, EnsemblRecord>();

    private final Map<EnsemblTranscript, EnsemblRecord> transcriptMap =
        new HashMap<EnsemblTranscript, EnsemblRecord>();

    // ...while gene identifiers may map to multiple peptide structures.
    private final Multimap<HugoSymbol, EnsemblRecord> hugoMap = HashMultimap.create();
    private final Multimap<EnsemblGene, EnsemblRecord> geneMap = HashMultimap.create();

    private static EnsemblDb global = null;

    private EnsemblDb(Iterable<FastaRecord> records) {
        for (FastaRecord record : records)
            addRecord(record);
    }

    private void addRecord(FastaRecord fastaRecord) {
        EnsemblRecord ensemblRecord = EnsemblRecord.parse(fastaRecord);

        if (proteinMap.put(ensemblRecord.getEnsemblProtein(), ensemblRecord) != null)
            throw JamException.runtime("Duplicate protein key: [%s]", ensemblRecord.getEnsemblProtein());

        if (transcriptMap.put(ensemblRecord.getEnsemblTranscript(), ensemblRecord) != null)
            throw JamException.runtime("Duplicate transcript key: [%s]", ensemblRecord.getEnsemblTranscript());

        geneMap.put(ensemblRecord.getEnsemblGene(), ensemblRecord);

        if (ensemblRecord.getHugoSymbol() != null)
            hugoMap.put(ensemblRecord.getHugoSymbol(), ensemblRecord);
    }

    /**
     * Name of the environment variable that defines the absolute path
     * of the Ensembl reference human peptidome file.  If the system
     * property {@code jam.ensembl.peptidomeFile} is also defined, it
     * will override the environment variable.
     */
    public static final String PEPTIDOME_FILE_ENV = "ENSEMBL_PEPTIDOME_FILE";

    /**
     * Name of the system property that defines the absolute path of
     * the Ensembl reference human peptidome file.  If not defined,
     * the environment variable {@code ENSEMBL_PEPTIDOME_FILE} will
     * be used by default.
     */
    public static final String PEPTIDOME_FILE_PROPERTY = "jam.ensembl.peptidomeFile";

    /**
     * Creates a database of Ensembl records from a sequence of FASTA
     * records.
     *
     * @param fastaRecords the underlying FASTA records.
     *
     * @return the database of Ensembl records.
     *
     * @throws RuntimeException if the records contain any duplicate
     * protein or transcript keys.
     */
    public static EnsemblDb create(Iterable<FastaRecord> fastaRecords) {
        return new EnsemblDb(fastaRecords);
    }

    /**
     * Creates a database of Ensembl records from the FASTA records
     * contained in a file.
     *
     * @param fastaFile the file containing the underlying FASTA
     * records.
     *
     * @return the database of Ensembl records.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static EnsemblDb create(File fastaFile) {
        EnsemblDb database = null;
        FastaReader reader = null;

        try {
            JamLogger.info("Reading [%s]...", fastaFile);
            reader = FastaReader.open(fastaFile);
            database = new EnsemblDb(reader);
        }
        finally {
            IOUtil.close(reader);
        }

        return database;
    }

    /**
     * Returns the single global database containing the reference
     * human proteome.
     *
     * @return the single global instance containing the reference
     * human proteome.
     */
    public static EnsemblDb global() {
        if (global == null)
            global = create(resolveGlobalFile());

        return global;
    }

    private static File resolveGlobalFile() {
        return new File(resolveGlobalFileName());
    }

    private static String resolveGlobalFileName() {
        if (JamProperties.isSet(PEPTIDOME_FILE_PROPERTY))
            return JamProperties.getRequired(PEPTIDOME_FILE_PROPERTY);
        else
            return JamEnv.getRequired(PEPTIDOME_FILE_ENV);
    }

    /**
     * Identifies genes in this map.
     *
     * @param gene a gene of interest.
     *
     * @return {@code true} iff this map contains the specified gene.
     */
    public boolean contains(EnsemblGene gene) {
        return geneMap.containsKey(gene);
    }

    /**
     * Identifies proteins in this map.
     *
     * @param protein a protein of interest.
     *
     * @return {@code true} iff this map contains the specified protein.
     */
    public boolean contains(EnsemblProtein protein) {
        return proteinMap.containsKey(protein);
    }

    /**
     * Identifies transcripts in this map.
     *
     * @param transcript a transcript of interest.
     *
     * @return {@code true} iff this map contains the specified transcript.
     */
    public boolean contains(EnsemblTranscript transcript) {
        return transcriptMap.containsKey(transcript);
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
        return hugoMap.containsKey(hugo);
    }

    /**
     * Counts the number of peptides mapped to a given gene.
     *
     * @param gene a gene of interest.
     *
     * @return the number of peptides mapped to the given gene.
     */
    public int count(EnsemblGene gene) {
        return geneMap.get(gene).size();
    }

    /**
     * Counts the number of peptides mapped to a given HUGO symbol.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return the number of peptides mapped to the given HUGO symbol.
     */
    public int count(HugoSymbol hugo) {
        return hugoMap.get(hugo).size();
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
        return Collections.unmodifiableCollection(geneMap.get(gene));
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
        return proteinMap.get(protein);
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
        return transcriptMap.get(transcript);
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
        return Collections.unmodifiableCollection(hugoMap.get(hugo));
    }

    /**
     * Returns a read-only view of the genes in this map.
     *
     * @return a read-only set containing the genes in this map.
     */
    public Set<EnsemblGene> geneSet() {
        return Collections.unmodifiableSet(geneMap.keySet());
    }

    /**
     * Returns a read-only view of the HUGO symbols in this map.
     *
     * @return a read-only set containing the HUGO symbols in this
     * map.
     */
    public Set<HugoSymbol> hugoSet() {
        return Collections.unmodifiableSet(hugoMap.keySet());
    }

    /**
     * Returns a read-only view of the proteins in this map.
     *
     * @return a read-only set containing the proteins in this map.
     */
    public Set<EnsemblProtein> proteinSet() {
        return Collections.unmodifiableSet(proteinMap.keySet());
    }

    /**
     * Returns a read-only view of the transcripts in this map.
     *
     * @return a read-only set containing the transcripts in this map.
     */
    public Set<EnsemblTranscript> transcriptSet() {
        return Collections.unmodifiableSet(transcriptMap.keySet());
    }

    /**
     * Returns the number of records in this database.
     *
     * @return the number of records in this database.
     */
    public int size() {
        return proteinMap.size();
    }
}
