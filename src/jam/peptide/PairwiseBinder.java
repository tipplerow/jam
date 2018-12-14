
package jam.peptide;

import jam.math.IntRange;

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
     * Returns the indexes of the binder residues that overlap with
     * the target peptide.
     *
     * @return the indexes of the binder residues that overlap with
     * the target peptide.
     */
    public abstract IntRange getBinderRegion();

    /**
     * Returns the indexes of the target residues that overlap with
     * the binder peptide.
     *
     * @return the indexes of the target residues that overlap with
     * the binder peptide.
     */
    public abstract IntRange getTargetRegion();

    /**
     * Returns the fragment of the binder peptide that interacts with
     * target peptides.
     *
     * @return the fragment of the binder peptide that interacts with
     * target peptides.
     */
    public default Peptide getBinderFragment() {
        return getBinderPeptide().fragment(getBinderRegion());
    }

    /**
     * Returns the fragment of the target peptide that interacts with
     * binder peptides.
     *
     * @param target the target peptide.
     *
     * @return the fragment of the target peptide that interacts with
     * binder peptides.
     */
    public default Peptide getTargetFragment(Peptide target) {
        return target.fragment(getTargetRegion());
    }

    @Override public default double computeFreeEnergy(Peptide target) {
        return getRIM().computeNearest(getBinderFragment(), getTargetFragment(target));
    }
}
