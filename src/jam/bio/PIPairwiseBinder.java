
package jam.bio;

/**
 * Represents peptide binders with <em>position-independent</em>
 * local pairwise interactions between amino acids.
 */
public interface PIPairwiseBinder extends PairwiseBinder {
    /**
     * Returns the position-independent free energy of interaction
     * between two residues.
     *
     * @param targetResidue the target residue.
     *
     * @param binderResidue the binder residue.
     *
     * @return the pairwise free energy.
     */
    public abstract double getFreeEnergy(Residue targetResidue, Residue binderResidue);

    /**
     * Returns the underlying peptide responsible for binding targets.
     *
     * @return the underlying peptide responsible for binding targets.
     */
    public abstract Peptide getBinderPeptide();

    @Override public double getFreeEnergy(Residue targetResidue, int binderIndex) {
        return getFreeEnergy(targetResidue, getBinderPeptide().at(binderIndex));
    }

    @Override public int length() {
        return getBinderPeptide().length();
    }
}
