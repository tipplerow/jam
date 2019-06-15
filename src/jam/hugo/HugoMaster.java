
package jam.hugo;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.ensembl.EnsemblGene;
import jam.ensembl.EnsemblTranscript;
import jam.io.TableReader;
import jam.lang.JamException;

/**
 * Maintains mappings between HUGO symbols, genes, and transcripts.
 */
public final class HugoMaster {
    private final Multimap<HugoSymbol, EnsemblGene> hugoToGeneMap = HashMultimap.create();
    private final Multimap<EnsemblGene, HugoSymbol> geneToHugoMap = HashMultimap.create();

    private final Multimap<HugoSymbol, EnsemblTranscript> hugoToTranscriptMap = HashMultimap.create();
    private final Multimap<EnsemblTranscript, HugoSymbol> transcriptToHugoMap = HashMultimap.create();

    private static HugoMaster global = null;

    private HugoMaster(TableReader reader) {
        validateHeader(reader);

        for (List<String> line : reader)
            processLine(line);

        reader.close();
    }

    private static void validateHeader(TableReader reader) {
        List<String> expected =
            List.of(HugoSymbol.COLUMN_NAME,
                    EnsemblGene.COLUMN_NAME,
                    EnsemblTranscript.COLUMN_NAME);

        if (!reader.columnKeys().equals(expected))
            throw JamException.runtime("Invalid header line.");
    }

    private void processLine(List<String> line) {
        HugoSymbol        hugo       = HugoSymbol.instance(line.get(0));
        EnsemblGene       gene       = EnsemblGene.instance(line.get(1));
        EnsemblTranscript transcript = EnsemblTranscript.instance(line.get(2));

        hugoToGeneMap.put(hugo, gene);
        geneToHugoMap.put(gene, hugo);

        hugoToTranscriptMap.put(hugo, transcript);
        transcriptToHugoMap.put(transcript, hugo);
    }

    /**
     * Name of the environment variable that defines the absolute path
     * name for the Hugo master symbol table.  If the system property
     * {@code jam.hugo.masterFile} is also defined, it will override
     * the environment variable.
     */
    public static final String MASTER_FILE_ENV = "HUGO_MASTER_FILE";

    /**
     * Name of the system property that defines the absolute path
     * name for the Hugo master symbol table. If not defined, the
     * environment variable {@code HUGO_MASTER_FILE} will be used 
     * by default.
     */
    public static final String MASTER_FILE_PROPERTY = "jam.hugo.masterFile";

    /**
     * Returns the global master table defined by system properties or
     * environment variables.
     *
     * @return the global master table defined by system properties or
     * environment variables.
     */
    public static HugoMaster global() {
        if (global == null)
            global = load(resolveMasterFile());

        return global;
    }

    private static String resolveMasterFile() {
        if (JamProperties.isSet(MASTER_FILE_PROPERTY))
            return JamProperties.getRequired(MASTER_FILE_PROPERTY);
        else
            return JamEnv.getRequired(MASTER_FILE_ENV);
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFile the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(File masterFile) {
        return new HugoMaster(TableReader.open(masterFile));
    }

    /**
     * Loads the HUGO symbol mappings from a data file.
     *
     * @param masterFileName the name of the master file to load.
     *
     * @return the master table of HUGO symbol mappings.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static HugoMaster load(String masterFileName) {
        return new HugoMaster(TableReader.open(masterFileName));
    }

    /**
     * Identifies genes in this master table.
     *
     * @param gene a gene of interest.
     *
     * @return {@code true} iff this master table contains the
     * specified gene.
     */
    public boolean contains(EnsemblGene gene) {
        return geneToHugoMap.containsKey(gene);
    }

    /**
     * Identifies transcripts in this master table.
     *
     * @param transcript a transcript of interest.
     *
     * @return {@code true} iff this master table contains the
     * specified transcript.
     */
    public boolean contains(EnsemblTranscript transcript) {
        return transcriptToHugoMap.containsKey(transcript);
    }

    /**
     * Identifies HUGO symbols in this master table.
     *
     * @param hugo a HUGO symbol of interest.
     *
     * @return {@code true} iff this master table contains the
     * specified HUGO symbol.
     */
    public boolean contains(HugoSymbol hugo) {
        return hugoToGeneMap.containsKey(hugo);
    }

    /**
     * Returns a read-only view of the HUGO symbols associated with
     * a given gene.
     *
     * @param gene the gene of interest.
     *
     * @return a read-only collection containing the HUGO symbols
     * mapped to the specified gene (an empty collection if this
     * master table does not contain the gene).
     */
    public Collection<HugoSymbol> getHugo(EnsemblGene gene) {
        return Collections.unmodifiableCollection(geneToHugoMap.get(gene));
    }

    /**
     * Returns a read-only view of the HUGO symbols associated with
     * a given transcript.
     *
     * @param transcript the transcript of interest.
     *
     * @return a read-only collection containing the HUGO symbols
     * mapped to the specified transcript (an empty collection if
     * this master table does not contain the transcript).
     */
    public Collection<HugoSymbol> getHugo(EnsemblTranscript transcript) {
        return Collections.unmodifiableCollection(transcriptToHugoMap.get(transcript));
    }

    /**
     * Returns a read-only view of the genes mapped to a given HUGO
     * symbol.
     *
     * @param hugo the HUGO symbol of interest.
     *
     * @return a read-only collection containing the genes mapped
     * to the specified HUGO symbol (an empty collection if this
     * master table does not contain the symbol).
     */
    public Collection<EnsemblGene> getGenes(HugoSymbol hugo) {
        return Collections.unmodifiableCollection(hugoToGeneMap.get(hugo));
    }

    /**
     * Returns a read-only view of the transcripts mapped to a given
     * HUGO symbol.
     *
     * @param hugo the HUGO symbol of interest.
     *
     * @return a read-only collection containing the transcripts
     * mapped to the specified HUGO symbol (an empty collection if
     * this master table does not contain the symbol).
     */
    public Collection<EnsemblTranscript> getTranscripts(HugoSymbol hugo) {
        return Collections.unmodifiableCollection(hugoToTranscriptMap.get(hugo));
    }

    /**
     * Returns a read-only view of the genes in this master table.
     *
     * @return a read-only set containing the genes in this master
     * table.
     */
    public Set<EnsemblGene> geneSet() {
        return Collections.unmodifiableSet(geneToHugoMap.keySet());
    }

    /**
     * Returns a read-only view of the HUGO symbols in this master
     * table.
     *
     * @return a read-only set containing the HUGO symbols in this
     * master table.
     */
    public Set<HugoSymbol> hugoSet() {
        return Collections.unmodifiableSet(hugoToGeneMap.keySet());
    }

    /**
     * Returns a read-only view of the transcripts in this master
     * table.
     *
     * @return a read-only set containing the transcripts in this
     * master table.
     */
    public Set<EnsemblTranscript> transcriptSet() {
        return Collections.unmodifiableSet(transcriptToHugoMap.keySet());
    }
}
