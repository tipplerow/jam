
package jam.mhc;

import jam.math.IntRange;
import jam.peptide.Peptide;
import jam.peptide.PeptideBinder;
import jam.peptide.Residue;

/**
 * Represents an MHC allele and/or the corresponding MHC molecule.
 */
public interface MHC extends PeptideBinder, PeptidePresenter {
    /**
     * Returns the affinity threshold for peptide presentation: MHC
     * molecules must bind a peptide with affinity at or above this
     * threshold to present it to T cells.
     *
     * @return the affinity threshold for peptide presentation.
     */
    public abstract double getAffinityThreshold();

    /**
     * Determines whether a target peptide is presented by this MHC
     * allele.
     *
     * @param target the peptide to examine.
     *
     * @return {@code true} iff the target peptide is presented by
     * this MHC allele.
     */
    @Override public default boolean isPresented(Peptide target) {
        return computeAffinity(target) >= getAffinityThreshold();
    }
}
