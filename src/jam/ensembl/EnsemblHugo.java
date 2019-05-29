
package jam.ensembl;

import jam.peptide.HugoSymbol;

/**
 * Represents the unique Ensembl HUGO identifier.
 */
public final class EnsemblHugo extends EnsemblID {
    private static final String LABEL_CODE = "gene_symbol:";

    private EnsemblHugo(String key) {
        super(key);
    }

    /**
     * Returns the Ensemble HUGO identifier for a given key string.
     *
     * @param key the key string.
     *
     * @return the Ensemble HUGO identifier for the given key string.
     */
    public static EnsemblHugo instance(String key) {
        return new EnsemblHugo(key);
    }

    /**
     * Extracts the HUGO symbol from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the HUGO symbol contained in the given header line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted HUGO symbol.
     */
    public static EnsemblHugo parseHeader(String headerLine) {
        return new EnsemblHugo(parseHeader(headerLine, LABEL_CODE));
    }

    /**
     * Returns the HUGO symbol object corresponding to this HUGO key.
     *
     * @return the HUGO symbol object corresponding to this HUGO key.
     */
    public HugoSymbol hugoSymbol() {
        return HugoSymbol.instance(getKey());
    }
}
