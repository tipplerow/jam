
package jam.bio;

import java.util.List;

import jam.math.IntRange;
import jam.math.StatSummary;
import jam.util.ListUtil;

/**
 * Base class for biological structures that bind peptides and are
 * themselves contructed of peptides (MHC molecules and lymphocyte
 * receptors).
 */
public abstract class PeptideBinder {
    private final Peptide structure;

    // Summary of binding free energies over all possible peptides,
    // computed on demand...
    private StatSummary summary = null;

    /**
     * Creates a new peptide binder with a fixed structure.
     *
     * @param structure the fixed structure of the binder.
     */
    protected PeptideBinder(Peptide structure) {
        this.structure = structure;
    }

    /**
     * Defines the pairwise residue interactions for this binder.
     *
     * @return the pairwise residue interaction matrix for this
     * binder.
     */
    public abstract RIM getRIM();

    /**
     * Identifies the region of a target peptide bound by this
     * binder.
     *
     * @return the region of a target peptide bound by this binder.
     */
    public abstract IntRange getTargetRange();

    /**
     * Returns the fixed peptide structure of this binder.
     *
     * @return the fixed peptide structure of this binder.
     */
    public Peptide getStructure() {
        return structure;
    }

    /**
     * Computes the free energy of binding for a given target peptide.
     *
     * @param target the target peptide to bind.
     *
     * @return the free energy of binding for the specified target
     * peptide.
     *
     * @throws IllegalArgumentException unless the target peptide
     * contains the range specified by {@code getTargetRange()}.
     *
     * @throws IllegalStateException unless the size of the range
     * specified by {@code getTargetRange()} matches the length of
     * the underlying peptide structure of this binder.
     */
    public double computeFreeEnergy(Peptide target) {
        RIM rim = getRIM();
        IntRange targetRange = getTargetRange();

        if (targetRange.size() != structure.length())
            throw new IllegalStateException("Target range does not match the structure length.");

        // Iterate over the entire range of the underlying structure,
        // but only the specified subregion of the target peptide...
        int structIndex = 0;
        int targetIndex = targetRange.lower();
        double freeEnergy = 0.0;

        while (structIndex < structure.length()) {
            Residue structResidue = structure.at(structIndex);
            Residue targetResidue = target.at(targetIndex);

            freeEnergy += rim.get(structResidue, targetResidue);

            ++structIndex;
            ++targetIndex;
        }

        return freeEnergy;
    }

    /**
     * Computes the free energy of binding for this structure over all
     * possible target peptides.
     *
     * @return the free energy of binding for this structure over all
     * possible target peptides.
     *
     * @throws IllegalStateException if the length of this structure
     * exceeds the native peptide enumeration limit.
     */
    public List<Double> enumerateFreeEnergy() {
        return ListUtil.apply(Peptide.enumerate(structure.length()), x -> computeFreeEnergy(x));
    }

    /**
     * Computes the mean free energy of binding for this structure
     * over all possible target peptides (assuming that all native
     * amino acids occur with equal probability).
     *
     * @return the mean free energy of binding for this structure
     * over all possible target peptides.
     */
    public double meanFreeEnergy() {
        //
        // No need to enumerate if only the mean is requested...
        //
        RIM rim = getRIM();
        double mean = 0.0;

        for (Residue residue : structure.viewResidues())
            mean += rim.mean(residue);

        return mean;
    }

    /**
     * Returns a statistical summary of the free energy of binding for
     * this structure over all possible target peptides (assuming that
     * all native amino acids occur with equal probability).
     *
     * @return a statistical summary of the free energy of binding for
     * this structure over all possible target peptides.
     */
    public StatSummary summarize() {
        if (summary == null)
            summary = StatSummary.compute(enumerateFreeEnergy());

        return summary;
    }
}
