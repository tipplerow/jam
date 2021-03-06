
package jam.tcell;

import jam.peptide.Peptide;
import jam.peptide.PeptideBinder;

/**
 * Represents a T cell receptor.
 */
public interface TCR extends PeptideBinder {
    /**
     * Computes the average affinity of this receptor over all
     * congruent target peptides (assuming that amino acids are
     * distributed indpendently with equal probability).
     *
     * @return the mean affinity taken over all congruent target
     * peptides.
     */
    public abstract double computeMeanAffinity();

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
    public default boolean isRecognized(Peptide target) {
        return computeAffinity(target) >= getNegativeThreshold();
    }

    /**
     * Returns a unique and compact string representation of this
     * receptor suitable to identify the receptor in reports.
     *
     * @return a unique and compact string representation of this
     * receptor.
     */
    public abstract String formatString();
}
