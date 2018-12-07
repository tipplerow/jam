
package jam.bio;

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
     * Determines whether this receptor binds a given peptide strongly
     * enough to undergo clonal deletion (fail negative selection) in
     * the thymus.
     *
     * @param peptide the peptide to examine.
     *
     * @return {@code true} iff this receptor binds the given peptide
     * strongly.
     */
    public default boolean bindsStrongly(Peptide peptide) {
        return computeAffinity(peptide) >= getNegativeThreshold();
    }

    /**
     * Determines whether this receptor binds a given peptide weakly
     * (with sufficient affinity for positive selection in the thymus).
     *
     * @param peptide the peptide to examine.
     *
     * @return {@code true} iff this receptor binds the given peptide
     * weakly.
     */
    public default boolean bindsWeakly(Peptide peptide) {
        return computeAffinity(peptide) >= getPositiveThreshold();
    }

    /**
     * Determines whether this receptor recognizes a foreign peptide 
     * in the periphery (binds strongly).
     *
     * @param peptide the peptide to examine.
     *
     * @return {@code true} iff this receptor recognizes the given
     * peptide.
     */
    public default boolean isRecognized(Peptide peptide) {
        return bindsStrongly(peptide);
    }
}
