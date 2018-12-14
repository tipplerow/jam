
package jam.peptide;

/**
 * Defines a common interface for biological entities (MHC molecules
 * and lymphocyte receptors) that bind peptides or peptide fragments.
 */
public interface PeptideBinder {
    /**
     * Computes the free energy of binding between this binder and a
     * target peptide.
     *
     * @param target the target peptide.
     *
     * @return the free energy of binding between this binder and the
     * target peptide.
     */
    public abstract double computeFreeEnergy(Peptide target);

    /**
     * Converts a free energy of binding into a binding affinity:
     * {@code Affinity = Activation Energy - Free Energy}.
     *
     * @param freeEnergy the free energy of binding.
     *
     * @return the affinity corresponding to the input free energy.
     */
    public default double computeAffinity(double freeEnergy) {
        return getActivationEnergy() - freeEnergy;
    }

    /**
     * Computes the affinity between this binder and a target peptide:
     * {@code Affinity = Activation Energy - Free Energy}.
     *
     * @param target the target peptide.
     *
     * @return the affinity between this binder and the target peptide.
     */
    public default double computeAffinity(Peptide target) {
        return computeAffinity(computeFreeEnergy(target));
    }

    /**
     * Returns the activation energy for binder-peptide interactions.
     *
     * @return the activation energy for binder-peptide interactions.
     */
    public default double getActivationEnergy() {
        return 0.0;
    }
}
