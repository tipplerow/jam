
package jam.ensembl;

/**
 * Represents the unique Ensembl gene identifier.
 */
public final class EnsemblGene extends EnsemblID {
    private static final String LABEL_CODE = "gene:";

    private EnsemblGene(String key) {
        super(key);
    }

    /**
     * Returns the Ensemble gene identifier for a given key string.
     *
     * @param key the key string.
     *
     * @return the Ensemble gene identifier for the given key string.
     */
    public static EnsemblGene instance(String key) {
        return new EnsemblGene(key);
    }

    /**
     * Extracts the gene key from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the gene key contained in the given header line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted gene key.
     */
    public static EnsemblGene parseHeader(String headerLine) {
        return new EnsemblGene(parseHeader(headerLine, LABEL_CODE));
    }
}
