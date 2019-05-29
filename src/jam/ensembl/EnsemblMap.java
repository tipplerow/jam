
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
import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.lang.JamException;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;

/**
 * Manages the human genome data from Ensembl.
 */
public final class EnsemblMap {
    //
    // Protein and transcript keys map uniquely to peptide
    // structures...
    //
    private final Map<EnsemblProtein, EnsemblRecord> proteinMap =
        new HashMap<EnsemblProtein, EnsemblRecord>();

    private final Map<EnsemblTranscript, EnsemblRecord> transcriptMap =
        new HashMap<EnsemblTranscript, EnsemblRecord>();

    // Gene identifiers may map to multiple peptide structures...
    private final Multimap<HugoSymbol, EnsemblRecord> hugoMap = HashMultimap.create();
    private final Multimap<EnsemblGene, EnsemblRecord> geneMap = HashMultimap.create();

    private static EnsemblMap instance = null;

    private EnsemblMap() {
        File file = resolveFile();
        JamLogger.info("Reading [%s]...", file);

        FastaReader reader = FastaReader.open(file);

        for (FastaRecord record : reader)
            processRecord(record);

        reader.close();
    }

    private static File resolveFile() {
        return new File(JamEnv.getRequired("ENSEMBL_HOME"),
                        JamEnv.getRequired("ENSEMBL_HUMAN_PEPTIDOME"));
    }


    private void processRecord(FastaRecord fastaRecord) {
        EnsemblRecord ensemblRecord = EnsemblRecord.parse(fastaRecord);

        if (proteinMap.put(ensemblRecord.getEnsemblProtein(), ensemblRecord) != null)
            throw JamException.runtime("Duplicate protein key: [%s]", ensemblRecord.getEnsemblProtein());

        if (transcriptMap.put(ensemblRecord.getEnsemblTranscript(), ensemblRecord) != null)
            throw JamException.runtime("Duplicate transcript key: [%s]", ensemblRecord.getEnsemblTranscript());

        hugoMap.put(ensemblRecord.getHugoSymbol(), ensemblRecord);
        geneMap.put(ensemblRecord.getEnsemblGene(), ensemblRecord);
    }

    /**
     * Returns the single global instance.
     *
     * @return the single global instance.
     */
    public static EnsemblMap instance() {
        if (instance == null)
            instance = new EnsemblMap();

        return instance;
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
     * Returns a read-only view of the genes in this map.
     *
     * @return a read-only set containing the genes in this map.
     */
    public Set<EnsemblGene> geneSet() {
        return Collections.unmodifiableSet(geneMap.keySet());
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
}
