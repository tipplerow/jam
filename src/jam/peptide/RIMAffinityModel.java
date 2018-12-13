
package jam.peptide;

/**
 * Defines an affinity model with a position-independent pairwise
 * potential specified by a residue-interaction matrix.
 */
public interface RIMAffinityModel extends PairwiseAffinityModel {
    /**
     * Returns the governing residue-interaction matrix.
     *
     * @return the governing residue-interaction matrix.
     */
    public abstract RIM getRIM();

    @Override public default double getFreeEnergy(Residue binderResidue, Residue targetResidue) {
        return getRIM().get(binderResidue, targetResidue);
    }
}
