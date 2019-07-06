
package jam.rna;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Defines an interface to models that convert RNA expression to
 * protein concentration.
 */
public abstract class ConcentrationModel {
    private static ConcentrationModel global = null;

    /**
     * Name of the system property that specifies the type of the
     * global protein concentration model.
     */
    public static final String CONCENTRATION_MODEL_TYPE_PROPERTY =
        "jam.rna.concentrationModelType";

    /**
     * Returns the global concentration model defined by system
     * properties.
     *
     * @return the global concentration model defined by system
     * properties.
     */
    public static ConcentrationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ConcentrationModel createGlobal() {
        ConcentrationModelType type = resolveModelType();

        switch (type) {
        case LINEAR:
            return LinearConcentrationModel.global();

        case LOG:
            return LogConcentrationModel.global();

        case STEP:
            return StepConcentrationModel.global();

        default:
            throw JamException.runtime("Unsupported concentration model type: [%s].", type);
        }
    }

    private static ConcentrationModelType resolveModelType() {
        return JamProperties.getRequiredEnum(CONCENTRATION_MODEL_TYPE_PROPERTY, ConcentrationModelType.class);
    }

    /**
     * Returns the protein concentration that corresponds to a given
     * RNA expression.
     *
     * @param expression the RNA expression level.
     *
     * @return the protein concentration that corresponds to the given
     * RNA expression level.
     */
    public abstract Concentration translate(Expression expression);
}
