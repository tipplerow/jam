
package jam.hugo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.peptide.Peptide;

/**
 * Associates a HUGO symbol with a list of peptides (e.g., the peptide
 * fragments produced by proteasomal cleavage of the protein encoded
 * by the gene).
 */
public final class HugoPeptideList {
    private final HugoSymbol symbol;
    private final List<Peptide> peptides;

    private HugoPeptideList(HugoSymbol symbol, List<Peptide> peptides, boolean copy) {
        this.symbol = symbol;

        if (copy)
            this.peptides = new ArrayList<Peptide>(peptides);
        else
            this.peptides = Collections.unmodifiableList(peptides);
    }

    /**
     * Associates a HUGO symbol with a list of peptides.
     *
     * @param symbol the HUGO symbol.
     *
     * @param peptides peptides to associate with the HUGO symbol.
     *
     * @return a new {@code HugoPeptideList}, which maintains a
     * reference to the input list; subsequent changes to the input
     * list will be reflected in the new {@code HugoPeptideList}.
     */
    public static HugoPeptideList wrap(HugoSymbol symbol, List<Peptide> peptides) {
        return new HugoPeptideList(symbol, peptides, false);
    }

    /**
     * Returns the peptides at a given position in the list.
     *
     * @param index the index of the peptide in the list.
     *
     * @return the peptide at the specified position in the list.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Peptide getPeptide(int index) {
        return peptides.get(index);
    }

    /**
     * Returns the HUGO symbol that is associated with the peptides in
     * this list.
     *
     * @return the HUGO symbol that is associated with the peptides in
     * this list.
     */
    public HugoSymbol getSymbol() {
        return symbol;
    }

    /**
     * Returns the number of peptides in this list.
     *
     * @return the number of peptides in this list.
     */
    public int size() {
        return peptides.size();
    }

    /**
     * Returns a read-only view of the peptides in this list.
     *
     * @return a read-only view of the peptides in this list.
     */
    public List<Peptide> viewPeptides() {
        return peptides;
    }
}
