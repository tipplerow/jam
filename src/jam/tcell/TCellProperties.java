
package jam.tcell;

import jam.app.JamProperties;
import jam.math.DoubleUtil;
import jam.math.IntRange;

/**
 * Manages global system properties related to T cells and their
 * receptors.
 */
public final class TCellProperties {
    private static int repertoireSize = 0;
    private static double activationEnergy = DoubleUtil.unset();
    private static double positiveThreshold = DoubleUtil.unset();
    private static double negativeThreshold = DoubleUtil.unset();

    /**
     * Name of the system property that defines the activation energy
     * for TCR-epitope binding.
     */
    public static final String ACTIVATION_ENERGY_PROPERTY = "jam.tcell.activationEnergy";

    /**
     * Name of the system property that defines the affinity threshold
     * for positive selection.
     */
    public static final String POSITIVE_THRESHOLD_PROPERTY = "jam.tcell.positiveThreshold";

    /**
     * Name of the system property that defines the affinity threshold
     * for negative selection.
     */
    public static final String NEGATIVE_THRESHOLD_PROPERTY = "jam.tcell.negativeThreshold";

    /**
     * Name of the system property that defines the number of T cells
     * circulating in the periphery.
     */
    public static final String REPERTOIRE_SIZE_PROPERTY = "jam.tcell.repertoireSize";

    /**
     * Returns the activation energy for TCR-epitope binding.
     *
     * @return the activation energy for TCR-epitope binding.
     */
    public static double getActivationEnergy() {
        if (DoubleUtil.isUnset(activationEnergy))
            activationEnergy = JamProperties.getRequiredDouble(ACTIVATION_ENERGY_PROPERTY);

        return activationEnergy;
    }

    /**
     * Returns the affinity threshold for positive selection.
     *
     * @return the affinity threshold for positive selection.
     */
    public static double getPositiveThreshold() {
        if (DoubleUtil.isUnset(positiveThreshold))
            positiveThreshold = JamProperties.getRequiredDouble(POSITIVE_THRESHOLD_PROPERTY);

        return positiveThreshold;
    }

    /**
     * Returns the affinity threshold for negative selection.
     *
     * @return the affinity threshold for negative selection.
     *
     * @throws IllegalStateException unless the negative threshold
     * exceeds the positive threshold.
     */
    public static double getNegativeThreshold() {
        if (DoubleUtil.isUnset(negativeThreshold)) {
            negativeThreshold = JamProperties.getRequiredDouble(NEGATIVE_THRESHOLD_PROPERTY);

            if (negativeThreshold <= positiveThreshold)
                throw new IllegalStateException("Negative threshold must exceed the positive threshold.");
        }

        return negativeThreshold;
    }

    /**
     * Returns the number of T cells circulating in the periphery.
     *
     * @return the number of T cells circulating in the periphery.
     */
    public static int getRepertoireSize() {
        if (repertoireSize < 1)
            repertoireSize = JamProperties.getRequiredInt(REPERTOIRE_SIZE_PROPERTY, IntRange.POSITIVE);

        return repertoireSize;
    }
}
