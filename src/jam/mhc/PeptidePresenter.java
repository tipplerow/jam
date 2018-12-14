
package jam.mhc;

import java.util.Collection;

import jam.math.DoubleUtil;
import jam.peptide.Peptide;

/**
 * Represents a single MHC allele or a full genotype that presents
 * peptides to T cell receptors.
 */
public interface PeptidePresenter {
    /**
     * Determines whether a target peptide is presented by this allele
     * or genotype.
     *
     * @param target the peptide to test for presentation.
     *
     * @return {@code true} iff the specified peptide is presented by
     * this allele or genotype.
     */
    public abstract boolean isPresented(Peptide target);

    /**
     * Computes the fraction of peptides that are presented by this
     * allele or genotype.
     *
     * @param peptides the peptides to test.
     *
     * @return the fraction of peptides that are presented by this
     * allele.
     */
    public default double computePresentationRate(Collection<Peptide> peptides) {
        int presented = 0;

        for (Peptide peptide : peptides)
            if (isPresented(peptide))
                ++presented;

        return DoubleUtil.ratio(presented, peptides.size());
    }

    /**
     * Implements MHC restriction for this allele or genotype.
     *
     * @param targets a collection of target peptides to restrict
     * (unchanged).
     *
     * @param presented a collection to be filled with the presented
     * peptides.
     */
    public default void restrict(Collection<Peptide> targets, Collection<Peptide> presented) {
        for (Peptide target : targets)
            if (isPresented(target))
                presented.add(target);
    }
}
