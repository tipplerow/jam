
package jam.rna;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Defines an interface to models that convert RNA expression to
 * protein concentration.
 */
public final class StepConcentrationModel extends ConcentrationModel {
    private final Expression threshold;

    private static StepConcentrationModel global = null;

    private static final Concentration ZERO_CONC = Concentration.ZERO;
    private static final Concentration UNIT_CONC = Concentration.valueOf(1.0);

    /**
     * Name of the system property that specifies the type of the
     * global protein concentration model.
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
