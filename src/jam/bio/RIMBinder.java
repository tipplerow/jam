
package jam.bio;

/**
 * Represents peptide binders with <em>position-independent</em> local
 * pairwise interactions specified by a residue-interaction matrix.
 */
public interface RIMBinder extends PIPairwiseBinder {
    /**
     * Returns the residue-interaction matrix that defines the
     * pairwise potential.
     *
     * @return the pairwise free energy.
     */
    public abstract RIM getRIM();

    @Override public default double getFreeEnergy(Residue targetResidue, Residue binderResidue) {
        return getRIM().get(targetResidue, binderResidue);
    }
}
