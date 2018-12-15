
package jam.mhc;

import jam.peptide.Peptide;

/**
 * Provides a skeletal implementation for MHC molecules having a
 * peptide chain as the underlying binder structure and sharing
 * characteristics defined through global system properties.
 */
public abstract class AbstractMHC implements MHC {
    /**
     * The underlying anchor structure.
     */
    protected final Peptide anchor;

    /**
     * Creates a new peptide-based MHC molecule.
     *
     * @param anchor the underlying anchor structure.
     *
     * @throws IllegalArgumentException unless the length of the input
     * anchor structure matches the global anchor length.
     */
    protected AbstractMHC(Peptide anchor) {
        this.anchor = anchor;
        validateAnchor(anchor);
    }

    private static void validateAnchor(Peptide anchor) {
        if (anchor.length() != MHCProperties.getAnchorLength())
            throw new IllegalArgumentException("Anchor structure does not match the anchor length.");
    }

    /**
     * Returns the anchor structure.
     *
     * @return the anchor structure.
     */
    public final Peptide getAnchor() {
        return anchor;
    }

    @Override public double getActivationEnergy() {
        return MHCProperties.getActivationEnergy();
    }

    @Override public double getAffinityThreshold() {
        return MHCProperties.getAffinityThreshold();
    }
}
