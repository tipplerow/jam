
package jam.peptide;

import java.util.List;
import jam.util.ListUtil;

/**
 * Defines an affinity model with position-independent pairwise
 * interactions between amino acids.
 */
public interface PairwiseAffinityModel extends AffinityModel {
    /**
     * Returns the position-independent pairwise interaction (free
     * energy) between amino acids.
     *
     * @param binderResidue an amino acid on the binder.
     *
     * @param targetResidue an amino acid on the target.
     *
     * @return the binding free energy for the peptide pair.
     */
    public abstract double getFreeEnergy(Residue binderResidue, Residue targetResidue);

    /**
     * Computes the binding affinity for all congruent (same-length)
     * target peptides assuming that target residues are distributed
     * independently with equal probability.
     *
     * @param binderPeptide the binder peptide (MHC anchor or
     * lymphocyte receptor).
     *
     * @return the binding affinity for all congruent target peptides.
     */
    public default List<Double> enumerateAffinity(Peptide binderPeptide) {
        //
        // The upper bounds of the target region determines the
        // required length of the enumerated target peptides...
        //
        List<Peptide> targetPeptides = Peptide.enumerate(binderPeptide.length());
        return ListUtil.apply(targetPeptides, x -> computeAffinity(binderPeptide, x));
    }

    @Override public default double computeFreeEnergy(Peptide binderPeptide,
                                                      Peptide targetPeptide) {
        if (targetPeptide.length() != binderPeptide.length())
            throw new IllegalStateException("Incongruent target and binder peptides.");

        double freeEnergy = 0.0;

        for (int index = 0; index < binderPeptide.length(); ++index)
            freeEnergy +=
                getFreeEnergy(binderPeptide.at(index),
                              targetPeptide.at(index));

        return freeEnergy;
    }
}
