
package jam.hugo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import jam.ensembl.EnsemblGene;
import jam.util.RegexUtil;

/**
 * Represents one record in the HUGO master table.
 */
public final class HugoRecord {
    private final HugoSymbol hugoSymbol;
    private final EnsemblGene ensemblGene;
    private final Set<HugoSymbol> aliasSymbols;

    /**
     * Name of the data file column containing the HUGO symbol.
     */
    public static final String HUGO_SYMBOL_NAME = "Hugo_Symbol";

    /**
     * Name of the data file column containing the Ensembl gene.
     */
    public static final String ENSEMBL_GENE_NAME = "Ensembl_Gene";

    /**
     * Name of the data file column containing the HUGO symbol.
     */
    public static final String ALIAS_SYMBOLS_NAME = "Aliases";

    /**
     * Delimiter used to separate aliases in data files.
     */
    public static final Pattern ALIAS_DELIMITER = RegexUtil.COMMA;

    /**
     * Creates a new HUGO record from its individual fields.
     *
     * @param hugoSymbol the official HUGO symbol for the gene.
     *
     * @param ensemblGene the corresponding Ensembl gene identifier.
     *
     * @param aliasSymbols additional HUGO symbols used for the same
     * gene.
     */
    public HugoRecord(HugoSymbol hugoSymbol,
                      EnsemblGene ensemblGene,
                      Set<HugoSymbol> aliasSymbols) {
        this.hugoSymbol = hugoSymbol;
        this.ensemblGene = ensemblGene;
        this.aliasSymbols = aliasSymbols;
    }

    /**
     * Returns the official HUGO symbol for the gene.
     *
     * @return the official HUGO symbol for the gene.
     */
    public HugoSymbol getHugoSymbol() {
        return hugoSymbol;
    }

    /**
     * Returns the corresponding Ensembl identifier for the gene.
     *
     * @return the corresponding Ensembl identifier for the gene.
     */
    public EnsemblGene getEnsemblGene() {
        return ensemblGene;
    }

    /**
     * Identifies records with aliased HUGO symbols.
     *
     * @return {@code true} iff this record contains one or more
     * aliases for the primary HUGO symbol.
     */
    public boolean hasAliases() {
        return !aliasSymbols.isEmpty();
    }

    /**
     * Returns a read-only view of the additional HUGO symbols used
     * for the same gene.
     *
     * @return a read-only view of the additional HUGO symbols used
     * for the same gene.
     */
    public Set<HugoSymbol> viewAliases() {
        return Collections.unmodifiableSet(aliasSymbols);
    }
}
