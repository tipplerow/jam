
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
     * Computes the fraction of target peptides that are presented by
     * this allele or genotype
     *
     * @param targets the target peptides to test.
     *
     * @return the fraction of target peptides in the given collection
     * that are presented by this allele.
     */
    public default double computePresentationRate(Collection<Peptide> targets) {
        int presented = 0;

        for (Peptide target : targets)
            if (isPresented(target))
                ++presented;

        return DoubleUtil.ratio(presented, targets.size());
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
