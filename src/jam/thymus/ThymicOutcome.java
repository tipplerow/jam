
package jam.thymus;

/**
 * Enumerates possible outcomes for the selection of T cells in the
 * thymus.
 */
public enum ThymicOutcome {
    /**
     * Failed positive selection by failing to bind at least one
     * cortex peptide weakly.
     */
    FAILED_POSITIVE("FP", false, false),

    /**
     * Failed negative selection by binding one or more medulla
     * peptides strongly.
     */
    FAILED_NEGATIVE("FN", true, false),

    /**
     * Passed both selection tests and exited the thymus.
     */
    EXPORTED("EX", true, true);

    private final String code;
    private final boolean passedPositive;
    private final boolean passedNegative;

    private ThymicOutcome(String code, boolean passedPositive, boolean passedNegative) {
        this.code = code;
        this.passedPositive = passedPositive;
        this.passedNegative = passedNegative;

        validate();
    }

    private void validate() {
        if (passedNegative && !passedPositive)
            throw new IllegalStateException("Cannot pass negative selection without passing positive selection.");
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
     * States whether the receptor passed the positive selection
     * process in the cortex.
     *
     * @return {@code true} iff the receptor passed the positive
     * selection step.
     */
    public boolean passedPositive() {
        return passedPositive;
    }

    /**
     * States whether the receptor passed the negative selection
     * process in the medulla.
     *
     * @return {@code true} iff the receptor passed the negative
     * selection step.
     */
    public boolean passedNegative() {
        return passedNegative;
    }
}
