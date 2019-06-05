
package jam.rna;

/**
 * Enumerates types of protein concentration models.
 */
public enum ConcentrationModelType {
    /**
     * The protein concentration {@code C = log(1 + alpha * FPKM)},
     * where {@code FPKM} is the RNA transcript expression level and
     * {@code alpha} is a positive constant parameter.
     */
    LOG1P,

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
