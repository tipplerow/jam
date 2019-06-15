
package jam.ensembl;

import jam.hugo.HugoSymbol;

/**
 * Represents the unique Ensembl HUGO identifier.
 */
public final class EnsemblHugo extends EnsemblID {
    private static final String LABEL_CODE = "gene_symbol:";

    private EnsemblHugo(String key) {
        super(key);
    }

    /**
     * Extracts the HUGO symbol from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the HUGO symbol contained in the given header line, or
     * {@code null} if the header line does not contain a HUGO symbol.
     */
    public static HugoSymbol parseHeader(String headerLine) {
        if (headerContains(headerLine, LABEL_CODE))
            return HugoSymbol.instance(parseHeader(headerLine, LABEL_CODE));
        else
            return null;
    }
}
