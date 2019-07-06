
package jam.rna;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Defines a log-transformed protein concentration model:
 * The protein concentration is equal to:
 *
 * <pre>
 *     C = min[log(1 + FPKM / alpha), Cmax],
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level,
 * {@code alpha} is a positive scale parameter (in FPKM units),
 * {@code Cmax} is the maximum (log-transformed) concentration.
 */
public final class LogConcentrationModel extends ConcentrationModel {
    private final double alpha;
    private final double maxConc;

    private static LogConcentrationModel global = null;

    /**
     * Name of the system property that specifies the alpha scaling
     * factor.
     */
    public static final String ALPHA_PROPERTY = "jam.rna.log1P.alpha";

    /**
     * Name of the system property that specifies the maximum
     * log-transformed concentration.
     */
    public static final String MAX_CONC_PROPERTY = "jam.rna.log1P.maxConc";

    /**
     * Expression threshold below which concentration is always zero.
     */
    public static final double THRESHOLD = 0.13;

    /**
     * Creates a new concentration model with fixed parameters.
     *
     * @param alpha the alpha scaling parameter.
     *
     * @param maxConc the maximum log-transformed concentration.
     */
    public LogConcentrationModel(double alpha, double maxConc) {
        this.alpha = alpha;
        this.maxConc = maxConc;
    }

    /**
     * Returns the global log-transformed concentration model defined
     * by system properties.
     *
     * @return the global log-transformed concentration model defined
     * by system properties.
     */
    public static LogConcentrationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static LogConcentrationModel createGlobal() {
        return new LogConcentrationModel(resolveAlpha(), resolveMaxConc());
    }

    private static double resolveAlpha() {
        return JamProperties.getRequiredDouble(ALPHA_PROPERTY);
    }

    private static double resolveMaxConc() {
        return JamProperties.getRequiredDouble(MAX_CONC_PROPERTY);
    }

    @Override public Concentration translate(Expression expression) {
        if (expression.doubleValue() < THRESHOLD)
            return Concentration.ZERO;
        else
            return Concentration.valueOf(Math.min(maxConc, Math.log(1.0 + expression.doubleValue() / alpha)));
    }
}
