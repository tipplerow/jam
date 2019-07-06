
package jam.rna;

/**
 * Enumerates types of protein concentration models.
 */
public enum ConcentrationModelType {
    /**
     * The protein concentration is equal to the expression level:
     *
     * <pre>
     *     C = min(FPKM, Cmax),
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level and
     * {@code Cmax} is the maximum concentration.
     */
    LINEAR,

    /**
     * The protein concentration is equal to:
     *
     * <pre>
     *     C = min[log(1 + FPKM / alpha), Cmax],
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level,
     * {@code alpha} is a scale parameter (in units of FPKM), and
     * {@code Cmax} is the maximum (log-transformed) concentration.
     */
    LOG,

    /**
     * The protein concentration, {@code C}, is a step function:
     *
     * <pre>
     *     C = 0, FPKM &lt; Fmin,
     *     C = 1, FPKM &ge; Fmin.
     * </pre>
     *
     * where {@code FPKM} is the RNA transcript expression level and
     * {@code Fmin} is a threshold expression level.
     */
    STEP;
}
