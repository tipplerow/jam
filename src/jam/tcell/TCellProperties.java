
package jam.tcell;

import jam.app.JamProperties;
import jam.math.DoubleUtil;
import jam.math.IntRange;

/**
 * Manages global system properties related to T cells and their
 * receptors.
 */
public final class TCellProperties {
    private static Integer receptorLength = null;
    private static Integer repertoireSize = null;

    private static IntRange binderRegion = null;
    private static IntRange targetRegion = null;

    private static double activationEnergy = DoubleUtil.unset();
    private static double positiveThreshold = DoubleUtil.unset();
    private static double negativeThreshold = DoubleUtil.unset();

    /**
     * Name of the system property that defines a common length for
     * all T cell receptors.
     */
    public static final String RECEPTOR_LENGTH_PROPERTY = "jam.tcell.receptorLength";

    /**
     * Name of the system property that defines the indexes of the
     * receptor residues that overlap with the target peptide.
     */
    public static final String BINDER_REGION_PROPERTY = "jam.tcell.binderRegion";

    /**
     * Name of the system property that defines the indexes of the
     * target residues that overlap with the receptor peptide.
     */
    public static final String TARGET_REGION_PROPERTY = "jam.tcell.targetRegion";

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
     * Returns the common length for all T cell receptors.
     *
     * @return the common length for all T cell receptors.
     */
    public static int getReceptorLength() {
        if (receptorLength == null)
            receptorLength = JamProperties.getRequiredInt(RECEPTOR_LENGTH_PROPERTY, IntRange.POSITIVE);

        return receptorLength;
    }

    /**
     * Returns the indexes of the binder residues that overlap with
     * the target peptide.
     *
     * @return the indexes of the binder residues that overlap with
     * the target peptide.
     */
    public static IntRange getBinderRegion() {
        if (binderRegion == null)
            binderRegion = IntRange.parse(JamProperties.getRequired(BINDER_REGION_PROPERTY));

        return binderRegion;
    }

    /**
     * Returns the indexes of the target residues that overlap with
     * the receptor peptide.
     *
     * @return the indexes of the target residues that overlap with
     * the receptor peptide.
     */
    public static IntRange getTargetRegion() {
        if (targetRegion == null)
            targetRegion = IntRange.parse(JamProperties.getRequired(TARGET_REGION_PROPERTY));

        return targetRegion;
    }

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
        if (repertoireSize == null)
            repertoireSize = JamProperties.getRequiredInt(REPERTOIRE_SIZE_PROPERTY, IntRange.POSITIVE);

        return repertoireSize;
    }
}
