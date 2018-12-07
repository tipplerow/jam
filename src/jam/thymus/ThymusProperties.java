
package jam.thymus;

import jam.app.JamProperties;
import jam.dist.RealDistribution;
import jam.math.DoubleRange;
import jam.math.IntRange;

/**
 * Manages global properties related to T cell development in the
 * thymus.
 */
public final class ThymusProperties {
    private static int cortexPeptideCount = 0;
    private static int medullaPeptideCount = 0;

    private static DoubleRange positivePassRange = null;
    private static DoubleRange negativePassRange = null;
    private static DoubleRange netSelectionRange = null;

    /**
     * Name of the system property that specifies the number of
     * self-peptides in the thymic cortex.
     */
    public static final String CORTEX_PEPTIDE_COUNT_PROPERTY = "jam.thymus.cortexPeptideCount";

    /**
     * Name of the system property that specifies the probability
     * distribution for the number of self-peptides in the thymic
     * cortex.
     */
    public static final String CORTEX_PEPTIDE_DISTRIB_PROPERTY = "jam.thymus.cortexPeptideDistrib";

    /**
     * Name of the system property that specifies the number of
     * self-peptides in the thymic medulla.
     */
    public static final String MEDULLA_PEPTIDE_COUNT_PROPERTY = "jam.thymus.medullaPeptideCount";

    /**
     * Name of the system property that specifies the probability
     * distribution for the ratio of the number of self-peptides in
     * the thymic medulla to the number in the cortex.
     */
    public static final String MEDULLA_RATIO_DISTRIB_PROPERTY = "jam.thymus.medullaRatioDistrib";

    /**
     * Name of the system property that specifies the acceptable range
     * of successful positive selection rates.
     */
    public static final String POSITIVE_PASS_RANGE_PROPERTY = "jam.thymus.positivePassRange";

    /**
     * Name of the system property that specifies the acceptable range
     * of successful negative selection rates.
     */
    public static final String NEGATIVE_PASS_RANGE_PROPERTY = "jam.thymus.negativePassRange";

    /**
     * Name of the system property that specifies the acceptable range
     * of net selection rates.
     */
    public static final String NET_SELECTION_RANGE_PROPERTY = "jam.thymus.netSelectionRange";

    /**
     * Returns the number of peptides in the thymic cortex.
     *
     * @return the number of peptides in the thymic cortex.
     */
    public static int getCortexPeptideCount() {
        if (cortexPeptideCount < 1)
            cortexPeptideCount = resolveCortexPeptideCount();

        return cortexPeptideCount;
    }

    private static int resolveCortexPeptideCount() {
        int peptideCount;

        if (JamProperties.isSet(CORTEX_PEPTIDE_DISTRIB_PROPERTY))
            peptideCount = (int) RealDistribution.resolve(CORTEX_PEPTIDE_DISTRIB_PROPERTY).sample();
        else
            peptideCount = JamProperties.getRequiredInt(CORTEX_PEPTIDE_COUNT_PROPERTY);

        if (peptideCount < 1)
            throw new IllegalStateException("Cortex peptide count must be positive.");

        return peptideCount;
    }

    /**
     * Returns the number of peptides in the thymic medulla.
     *
     * @return the number of peptides in the thymic medulla.
     */
    public static int getMedullaPeptideCount() {
        if (medullaPeptideCount < 1)
            medullaPeptideCount = resolveMedullaPeptideCount();

        return medullaPeptideCount;
    }

    private static int resolveMedullaPeptideCount() {
        int peptideCount;

        if (JamProperties.isSet(MEDULLA_RATIO_DISTRIB_PROPERTY))
            peptideCount = (int) (getCortexPeptideCount() * 
                                  RealDistribution.resolve(MEDULLA_RATIO_DISTRIB_PROPERTY).sample());
        else
            peptideCount = JamProperties.getRequiredInt(MEDULLA_PEPTIDE_COUNT_PROPERTY);

        if (peptideCount < getCortexPeptideCount())
            throw new IllegalStateException("Medulla peptide count must exceed the cortex peptide count.");

        return peptideCount;
    }

    /**
     * Returns the acceptable range of positive selection rates.
     *
     * @return the acceptable range of positive selection rates.
     */
    public static final DoubleRange getPositivePassRange() {
        if (positivePassRange == null)
            positivePassRange = DoubleRange.parse(JamProperties.getRequired(POSITIVE_PASS_RANGE_PROPERTY));

        return positivePassRange;
    }

    /**
     * Returns the acceptable range of negative selection rates.
     *
     * @return the acceptable range of negative selection rates.
     */
    public static final DoubleRange getNegativePassRange() {
        if (negativePassRange == null)
            negativePassRange = DoubleRange.parse(JamProperties.getRequired(NEGATIVE_PASS_RANGE_PROPERTY));

        return negativePassRange;
    }

    /**
     * Returns the acceptable range of net selection rates.
     *
     * @return the acceptable range of net selection rates.
     */
    public static final DoubleRange getNetSelectionRange() {
        if (netSelectionRange == null)
            netSelectionRange = DoubleRange.parse(JamProperties.getRequired(NET_SELECTION_RANGE_PROPERTY));

        return netSelectionRange;
    }
}
