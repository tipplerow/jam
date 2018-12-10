
package jam.tcell;

import jam.bio.Peptide;
import jam.bio.PeptideBinder;

/**
 * Represents a T cell receptor.
 */
public interface TCR extends PeptideBinder {
    /**
     * Returns the affinity threshold for positive selection: T cells
     * must bind at least one self-peptide in the thymic cortex to be
     * positively selected (avoid apoptosis).
     *
     * @return the affinity threshold for positive selection.
     */
    public abstract double getPositiveThreshold();

    /**
     * Returns the affinity threshold for negative selection: T cells
     * must not bind any self-peptides in the thymic medulla to pass
     * negative selection (avoid clonal deletion).
     *
     * @return the affinity threshold for negative selection.
     */
    public abstract double getNegativeThreshold();

    /**
     * Returns the mean free energy of binding taken over all target
     * peptides (assuming amino acids are distributed indpendently
     * with equal probability).
     *
     * @return the mean free energy of binding taken over all target
     * peptides.
     */
    public abstract double meanFreeEnergy();

    /**
     * Returns the mean affinity taken over all target peptides
     * (assuming amino acids are distributed indpendently with
     * equal probability).
     *
     * @return the mean affinity taken over all target peptides.
     */
    public default double meanAffinity() {
        return computeAffinity(meanFreeEnergy());
    }

    /**
     * Determines whether a target peptide binds with this receptor
     * strongly enough to induce clonal deletion (to fail negative
     * selection).
     *
     * @param target the peptide to examine.
     *
     * @return {@code true} iff the target peptide induces clonal
     * deletion.
     */
    public default boolean isDeletor(Peptide target) {
        return computeAffinity(target) >= getNegativeThreshold();
    }

    /**
     * Determines whether a target peptide binds with this receptor
     * strongly enough to signal positive selection.
     *
     * @param target the peptide to examine.
     *
     * @return {@code true} iff the target peptide signals positive
     * selection.
     */
    public default boolean isSelector(Peptide target) {
        return computeAffinity(target) >= getPositiveThreshold();
    }

    /**
     * Determines whether a target peptide binds with this receptor
     * strongly enough to be recognized in the periphery.
     *
     * @param target the peptide to examine.
     *
     * @return {@code true} iff this receptor recognizes the given
     * peptide.
     */
    public default boolean isCognate(Peptide target) {
        return computeAffinity(target) >= getNegativeThreshold();
    }
}
