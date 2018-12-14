
package jam.tcell;

import jam.peptide.Peptide;

/**
 * Provides a skeletal implementation for T cell receptors having
 * a peptide chain as the underlying binder structure and sharing
 * characteristics defined through global system properties.
 */
public abstract class AbstractTCR implements TCR {
    /**
     * The underlying receptor structure.
     */
    protected final Peptide binder;

    /**
     * Creates a new peptide-based T cell receptor.
     *
     * @param binder the underlying binder structure.
     *
     * @throws IllegalArgumentException unless the length of the input
     * binder structure matches the global receptor length.
     */
    protected AbstractTCR(Peptide binder) {
        this.binder = binder;
        validateBinder(binder);
    }

    private static void validateBinder(Peptide binder) {
        if (binder.length() != TCellProperties.getReceptorLength())
            throw new IllegalArgumentException("Receptor structure does not match the receptor length.");
    }

    @Override public double getActivationEnergy() {
        return TCellProperties.getActivationEnergy();
    }

    @Override public double getPositiveThreshold() {
        return TCellProperties.getPositiveThreshold();
    }

    @Override public double getNegativeThreshold() {
        return TCellProperties.getNegativeThreshold();
    }
}
