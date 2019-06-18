
package jam.rna;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Defines a step-function protein concentration model: the protein 
 *concentration, {@code C}, is a step function:
 *
 * <pre>
 *     C = 0, FPKM &lt; Fmin,
 *     C = 1, FPKM &ge; Fmin.
 * </pre>
 *
 * where {@code FPKM} is the RNA transcript expression level and
 * {@code Fmin} is a threshold expression level.
 */
public final class StepConcentrationModel extends ConcentrationModel {
    private final Expression threshold;

    private static StepConcentrationModel global = null;

    private static final Concentration ZERO_CONC = Concentration.ZERO;
    private static final Concentration UNIT_CONC = Concentration.valueOf(1.0);

    /**
     * Name of the system property that specifies the expression
     * threshold.
     */
    public static final String EXPRESSION_THRESHOLD_PROPERTY =
        "jam.rna.expressionThreshold";

    /**
     * Creates a new step concentration model with a fixed expression
     * threshold.
     *
     * @param threshold the expression threshold.
     */
    public StepConcentrationModel(Expression threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns the global step concentration model defined by system
     * properties.
     *
     * @return the global step concentration model defined by system
     * properties.
     */
    public static StepConcentrationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static StepConcentrationModel createGlobal() {
        return new StepConcentrationModel(resolveThreshold());
    }

    private static Expression resolveThreshold() {
        return Expression.valueOf(JamProperties.getRequiredDouble(EXPRESSION_THRESHOLD_PROPERTY));
    }

    @Override public Concentration translate(Expression expression) {
        if (expression.doubleValue() < threshold.doubleValue())
            return ZERO_CONC;
        else
            return UNIT_CONC;
    }
}
