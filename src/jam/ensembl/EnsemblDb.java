
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
import jam.util.MapUtil;

/**
 * Manages the human genome data from Ensembl.
 */
public final class EnsemblDb {
    //
    // Protein and transcript keys map uniquely to peptide
    // structures...
    //
    private final Map<EnsemblProtein, EnsemblRecord> proteinRecordMap =
        new HashMap<EnsemblProtein, EnsemblRecord>();

    private final Map<EnsemblTranscript, EnsemblRecord> transcriptRecordMap =
        new HashMap<EnsemblTranscript, EnsemblRecord>();

    // ...while gene identifiers may map to multiple peptide structures.
    private final Multimap<HugoSymbol, EnsemblRecord> hugoRecordMap = HashMultimap.create();
    private final Multimap<EnsemblGene, EnsemblRecord> geneRecordMap = HashMultimap.create();

    // The reference Ensembl database defines the global mapping from
    // gene to HUGO symbol...
    private final Map<EnsemblGene, HugoSymbol> geneHugoMap =
        new HashMap<EnsemblGene, HugoSymbol>();

    private EnsemblDb(Iterable<FastaRecord> records) {
        for (FastaRecord record : records)
            addRecord(record);
    }

    private void addRecord(FastaRecord fastaRecord) {
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
     * @param fastaFile the name of the file containing the underlying
     * FASTA records.
     *
     * @return the database of Ensembl records.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static EnsemblDb create(String fastaFile) {
        return create(new File(fastaFile));
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
     * Returns the reference human proteome.
     *
     * @return the reference human proteome.
     */
    public static EnsemblDb reference() {
        return EnsemblAssembly.reference().db();
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
