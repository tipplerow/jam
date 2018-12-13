
package jam.peptide;

/**
 * Quantifies the free energy and affinity of peptide-peptide binding.
 */
public interface AffinityModel {
    /**
     * Computes the free energy of peptide-peptide binding.
     *
     * @param binder the binder peptide (MHC anchor or lymphocyte
     * receptor).
     *
     * @param target the target peptide for binding (antigen or
     * epitope).
     *
     * @return the free energy of peptide-peptide binding.
     */
    public abstract double computeFreeEnergy(Peptide binder, Peptide target);

    /**
     * Returns the activation energy used to convert free energy into
     * affinity.
     *
     * @return the activation energy for this binder.
     */
    public abstract double getActivationEnergy();

    /**
     * Computes the affinity of peptide-peptide binding.
     *
     * @param binder the binder peptide (MHC anchor or lymphocyte
     * receptor).
     *
     * @param target the target peptide for binding (antigen or
     * epitope).
     *
     * @return the affinity of peptide-peptide binding.
     */
    public default double computeAffinity(Peptide binder, Peptide target) {
        return computeAffinity(computeFreeEnergy(binder, target));
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
