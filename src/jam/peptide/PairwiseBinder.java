
package jam.peptide;

import java.util.List;

/**
 * Represents biological entities (MHC molecules and lymphocyte
 * receptors) that bind target peptides through purely local
 * (and position-independent) pairwise interactions described
 * by a residue-interaction matrix.
 */
public interface PairwiseBinder extends PeptideBinder {
    /**
     * Returns the residue-interaction matrix that defines the amino
     * acid pair potential.
     *
     * @return the residue-interaction matrix that defines the amino
     * acid pair potential.
     */ 
    public abstract RIM getRIM();

    /**
     * Returns the underlying peptide structure of the binding entity
     * (the MHC anchor or the T cell receptor).
     *
     * @return the indexes of the binder residues that overlap with
     * the target peptide.
     */
    public abstract Peptide getBinderPeptide();

    /**
     * Returns the indexes of the <em>target</em> residues that are
     * paired with residues in this structure: residue {@code k} in
     * the peptide of this binding structure interacts with residue
     * {@code getTargetInteractionPoints().get(k)} of the target.
     *
     * @return the indexes of the target interaction points.
     */
    public abstract List<Integer> getTargetInteractionPoints();

    @Override public default double computeFreeEnergy(Peptide target) {
        return getRIM().computeNearest(getBinderPeptide(), target, getTargetInteractionPoints());
    }
}
