
package jam.tcell;

import java.util.List;

import jam.app.JamProperties;
import jam.math.DoubleUtil;
import jam.math.IntUtil;
import jam.util.RegexUtil;

/**
 * Manages global system properties related to T cells and their
 * receptors.
 */
public final class TCellProperties {
    private static int repertoireSize = 0;

    // Target interaction points: Zero-based indexes of the amino
    // acids on MHC-presented peptides with which T cell receptors
    // interact.
    private static List<Integer> TIPs = null;

    private static double activationEnergy = DoubleUtil.unset();
    private static double positiveThreshold = DoubleUtil.unset();
    private static double negativeThreshold = DoubleUtil.unset();

    /**
     * Name of the system property that defines the number of T cells
     * in the circulating repertoire.
     */
    public static final String REPERTOIRE_SIZE_PROPERTY = "jam.tcell.repertoireSize";

    /**
     * Name of the system property that defines the <em>target
     * interaction points</em>: zero-based indexes of the amino acids
     * on MHC-presented peptides with which T cell receptors interact.
     */
    public static final String TIPS_PROPERTY = "jam.tcell.targetInteractionPoints";

    /**
     * Default values for the peptide interaction points, assuming
     * that (one-based) positions 2 and 9 (zero-based positions 1 and
     * 8) are the anchor residues and that the receptor interacts with
     * the other seven.
     */
    public static final List<Integer> TIPS_DEFAULT = List.of(0, 2, 3, 4, 5, 6, 7);

    /**
     * Name of the system property that defines the activation energy
     * for TCR-epitope binding.
     */
    public static final String ACTIVATION_ENERGY_PROPERTY = "jam.tcell.activationEnergy";

    /**
     * Default value for the activation energy for TCR-epitope binding.
     */
    public static final double ACTIVATION_ENERGY_DEFAULT = 0.0;

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
     * Returns the activation energy for TCR-epitope binding.
     *
     * @return the activation energy for TCR-epitope binding.
     */
    public static double getActivationEnergy() {
        if (DoubleUtil.isUnset(activationEnergy))
            activationEnergy = JamProperties.getOptionalDouble(ACTIVATION_ENERGY_PROPERTY, ACTIVATION_ENERGY_DEFAULT);

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
     * Returns the common length for all T cell receptors.
     *
     * @return the common length for all T cell receptors.
     */
    public static int getReceptorLength() {
        return viewTIPs().size();
    }

    /**
     * Returns the number of T cells in the circulating repertoire.
     *
     * @return the number of T cells in the circulating repertoire.
     */
    public static int getRepertoireSize() {
        if (repertoireSize < 1)
            repertoireSize = JamProperties.getRequiredInt(REPERTOIRE_SIZE_PROPERTY);

        return repertoireSize;
    }

    /**
     * Returns a read-only view of the peptide-interaction points.
     *
     * @return a read-only view of the peptide-interaction points.
     */
    public static List<Integer> viewTIPs() {
        if (TIPs == null)
            resolveTIPs();

        return TIPs;
    }

    private static void resolveTIPs() {
        if (JamProperties.isSet(TIPS_PROPERTY))
            TIPs = IntUtil.parseIntList(JamProperties.getRequired(TIPS_PROPERTY), RegexUtil.COMMA);
        else
            TIPs = TIPS_DEFAULT;
    }
}
