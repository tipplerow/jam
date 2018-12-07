
package jam.bio;

/**
 * Represents biological entities (MHC molecules and lymphocyte
 * receptors) that may bind peptides.
 */
public interface PeptideBinder {
    /**
     * Computes the free energy of binding to a target peptide.
     *
     * @param target the target peptide for binding.
     *
     * @return the free energy of binding to the specified peptide.
     */
    public abstract double computeFreeEnergy(Peptide target);

    /**
     * Returns the activation energy used to convert free energy into
     * affinity.
     *
     * @return the activation energy for this binder.
     */
    public abstract double getActivationEnergy();

    /**
     * Computes the affinity of this binder for a target peptide.
     *
     * @param target the target peptide for binding.
     *
     * @return the affinity of this binder for the specified peptide.
     */
    public default double computeAffinity(Peptide target) {
        return computeAffinity(computeFreeEnergy(target));
    }

    /**
     * Translates a free energy of binding into a binding affinity:
     * {@code Affinity = ActivationEnergy - FreeEnergy}.
     *
     * @param freeEnergy the free energy of binding.
     *
     * @return the binding affinity corresponding to the specified
     * free energy.
     */
    public default double computeAffinity(double freeEnergy) {
        return getActivationEnergy() - freeEnergy;
    }
}
