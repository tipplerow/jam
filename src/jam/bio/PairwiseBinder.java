
package jam.bio;

import java.util.List;

import jam.math.IntRange;
import jam.util.ListUtil;

/**
 * Represents peptide binders with purely local <em>but possibly
 * position-dependent</em> pairwise interactions between amino acids.
 */
public interface PairwiseBinder extends PeptideBinder {
    /**
     * Returns the position-dependent interaction between a target
     * residue and the residue at a given location on this binder.
     *
     * @param targetResidue the residue from the corresponding
     * position on the target peptide.
     *
     * @param binderIndex the index of the residue on this binder.
     *
     * @return the pairwise free energy.
     */
    public abstract double getFreeEnergy(Residue targetResidue, int binderIndex);

    /**
     * Identifies the region of a target peptide that overlaps with
     * this binder.
     *
     * @return the region of a target peptide that overlaps with this
     * binder.
     */
    public abstract IntRange getTargetRegion();

    /**
     * Returns the location on a target peptide (the index of the
     * target residue) corresponding to a location on this binder.
     *
     * @param binderIndex a location on this binder.
     *
     * @return the location on a target peptide corresponding to the
     * specified location on this binder.
     */
    public default int getTargetIndex(int binderIndex) {
        //
        // Note that the binder index always starts from zero,
        // so the target index is offset by the lower bound of
        // the target range...
        //
        return binderIndex + getTargetRegion().lower();
    }

    /**
     * Returns the residue on a target peptide that interacts with the
     * residue a given location on this binder.
     *
     * @param target the target peptide interacting with this binder.
     *
     * @param binderIndex a location on this binder.
     *
     * @return the residue on the target peptide that interacts with
     * the residue at the location of this binder.
     */
    public default Residue getTargetResidue(Peptide target, int binderIndex) {
        return target.at(getTargetIndex(binderIndex));
    }

    /**
     * Computes the affinity of binding for all possible target
     * peptides.
     *
     * @return the affinity of binding for all possible target
     * peptides.
     *
     * @throws IllegalStateException if the length of the underlying
     * structure exceeds the native peptide enumeration limit.
     */
    public default List<Double> enumerateAffinity() {
        return ListUtil.apply(Peptide.enumerate(length()), x -> computeAffinity(x));
    }

    /**
     * Returns the number of residues in this binder.
     *
     * @return the number of residues in this binder.
     */
    public default int length() {
        return getTargetRegion().size();
    }

    @Override public double computeFreeEnergy(Peptide target) {
        double freeEnergy = 0.0;

        for (int binderIndex = 0; binderIndex < length(); ++binderIndex)
            freeEnergy += getFreeEnergy(target.at(getTargetIndex(binderIndex)), binderIndex);

        return freeEnergy;
    }
}
