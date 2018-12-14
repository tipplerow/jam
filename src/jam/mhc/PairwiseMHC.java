
package jam.mhc;

import jam.math.IntRange;
import jam.peptide.PairwiseBinder;
import jam.peptide.Peptide;
import jam.peptide.RIM;

/**
 * Represents MHC alleles with anchor peptides having purely local
 * (and position-independent) pairwise interactions described by a
 * residue-interaction matrix.
 */
public abstract class PairwiseMHC extends AbstractMHC implements PairwiseBinder {
    /**
     * Creates a new pairwise MHC allele.
     *
     * @param anchor the underlying anchor structure.
     *
     * @throws IllegalArgumentException unless the length of the input
     * anchor structure matches the size of the global target region.
     */
    protected PairwiseMHC(Peptide anchor) {
        super(anchor);
    }

    @Override public Peptide getBinderPeptide() {
        return anchor;
    }

    @Override public IntRange getBinderRegion() {
        return MHCProperties.getAnchorRegion();
    }

    @Override public IntRange getTargetRegion() {
        return MHCProperties.getTargetRegion();
    }
}
