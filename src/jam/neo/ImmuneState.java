
package jam.neo;

/**
 * Enumerates possible immunogenic states for neopeptides and
 * pathogen-derived peptides.
 */
public enum ImmuneState {
    /**
     * Not presented by the HLA genotype.
     */
    NOT_PRESENTED("XX", false, false),

    /**
     * Presented by the HLA genotype but not recognized (bound
     * strongly) by any T cell receptors.
     */
    PRESENTED_NOT_RECOGNIZED("PX", true, false),

    /**
     * Presented by the HLA genotype and recognized (bound strongly)
     * by one or more T cell receptors.
     */
    PRESENTED_AND_RECOGNIZED("PR", true, true);

    private final String code;
    private final boolean presented;
    private final boolean recognized;

    private ImmuneState(String code, boolean presented, boolean recognized) {
        this.code = code;
        this.presented = presented;
        this.recognized = recognized;

        validate();
    }

    private void validate() {
        if (recognized && !presented)
            throw new IllegalStateException("Cannot be recognized but not presented.");
    }

    /**
     * Returns the shorthand code for this enum value.
     *
     * @return the shorthand code for this enum value.
     */
    public String getCode() {
        return code;
    }

    /**
     * States whether the peptide is presented by the governing HLA
     * genotype.
     *
     * @return {@code true} iff the peptide is presented by the HLA
     * genotype.
     */
    public boolean isPresented() {
        return presented;
    }

    /**
     * States whether the peptide is recognized by one or more T cell
     * receptors.
     *
     * @return {@code true} iff the peptide is recognized by one or
     * more T cell receptors.
     */
    public boolean isRecognized() {
        return recognized;
    }
}
