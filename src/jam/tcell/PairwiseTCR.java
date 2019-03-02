
package jam.tcell;

import java.util.List;

import jam.peptide.PairwiseBinder;
import jam.peptide.Peptide;
import jam.peptide.RIM;

/**
 * Represents receptors with purely local (and position-independent)
 * pairwise interactions described by a residue-interaction matrix.
 */
public abstract class PairwiseTCR extends AbstractTCR implements PairwiseBinder {
    /**
     * Creates a new pairwise T cell receptor.
     *
     * @param binder the underlying binder structure.
     *
     * @throws IllegalArgumentException unless the length of the input
     * binder structure matches the size of the global target region.
     */
    protected PairwiseTCR(Peptide binder) {
        super(binder);
    }

    @Override public double computeMeanAffinity() {
        return getRIM().computeMeanNearest(binder);
    }

    @Override public Peptide getBinderPeptide() {
        return binder;
    }

    @Override public List<Integer> getTargetInteractionPoints() {
        return TCellProperties.viewTIPs();
    }
}
