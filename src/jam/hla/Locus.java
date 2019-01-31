
package jam.hla;

/**
 * Enumerates the HLA allele locus.
 */
public enum Locus {
    A(MHCClass.I),
    B(MHCClass.I),
    C(MHCClass.I);

    private final MHCClass mhcClass;

    private Locus(MHCClass mhcClass) {
        this.mhcClass = mhcClass;
    }

    /**
     * Returns the MHC restriction class for this locus.
     *
     * @return the MHC restriction class for this locus.
     */
    public MHCClass getMHCClass() {
        return mhcClass;
    }
}
