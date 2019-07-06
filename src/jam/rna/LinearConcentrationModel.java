
package jam.rna;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Defines a linear protein concentration model where the protein
 * concentration is equal to the expression level:
 *
 * <pre>
 *     C = min(FPKM, Cmax),
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level and
 * {@code Cmax} is the maximum concentration.
 */
public final class LinearConcentrationModel extends ConcentrationModel {
    private final double maxConc;

    private static LinearConcentrationModel global = null;

    /**
     * Name of the system property that specifies the maximum
     * concentration.
     */
    public static final String MAX_CONC_PROPERTY = "jam.rna.linear.maxConc";

    /**
     * Expression threshold below which concentration is always zero.
     */
    public static final double THRESHOLD = 0.01;

    /**
     * Creates a new concentration model with fixed parameters.
     *
     * @param maxConc the maximum concentration.
     */
    public LinearConcentrationModel(double maxConc) {
        this.maxConc = maxConc;
    }

    /**
     * Returns the global linear concentration model defined by system
     * properties.
     *
     * @return the global linear concentration model defined by system
     * properties.
     */
    public static LinearConcentrationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static LinearConcentrationModel createGlobal() {
        return new LinearConcentrationModel(resolveMaxConc());
    }

    private static double resolveMaxConc() {
        return JamProperties.getRequiredDouble(MAX_CONC_PROPERTY);
    }

    @Override public Concentration translate(Expression expression) {
        if (expression.doubleValue() < THRESHOLD)
            return Concentration.ZERO;
        else
            return Concentration.valueOf(Math.min(expression.doubleValue(), maxConc));
    }
}
