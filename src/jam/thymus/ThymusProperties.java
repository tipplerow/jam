
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
    private static Integer sharedPeptideCount = null;
    private static Integer cortexPrivateCount = null;
    private static Integer medullaPrivateCount = null;

    private static DoubleRange positivePassRange = null;
    private static DoubleRange negativePassRange = null;
    private static DoubleRange netSelectionRange = null;

    /**
     * Name of the system property that specifies the number of
     * self-peptides present in both cortex and medulla.
     */
    public static final String SHARED_PEPTIDE_COUNT_PROPERTY = "jam.thymus.sharedPeptideCount";

    /**
     * Name of the system property that specifies the probability
     * distribution for the number of self-peptides presented in 
     * both the cortex and medulla.
     */
    public static final String SHARED_PEPTIDE_DISTRIB_PROPERTY = "jam.thymus.sharedPeptideDistrib";

    /**
     * Name of the system property that specifies the number of
     * self-peptides presented only in the thymic cortex.
     */
    public static final String CORTEX_PRIVATE_COUNT_PROPERTY = "jam.thymus.cortexPrivateCount";

    /**
     * Name of the system property that specifies the probability
     * distribution for the number of self-peptides presented only
     * in the thymic cortex.
     */
    public static final String CORTEX_PRIVATE_DISTRIB_PROPERTY = "jam.thymus.cortexPrivateDistrib";

    /**
     * Name of the system property that specifies the number of
     * self-peptides presented only in the thymic medulla.
     */
    public static final String MEDULLA_PRIVATE_COUNT_PROPERTY = "jam.thymus.medullaPrivateCount";

    /**
     * Name of the system property that specifies the probability
     * distribution for the number of self-peptides presented only
     * in the thymic medulla.
     */
    public static final String MEDULLA_PRIVATE_DISTRIB_PROPERTY = "jam.thymus.medullaPrivateDistrib";

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
     * Returns the number of self-peptides presented in both cortex
     * and medulla.
     *
     * @return the number of self-peptides presented in both cortex
     * and medulla.
     */
    public static int getSharedPeptideCount() {
        if (sharedPeptideCount == null)
            sharedPeptideCount = resolveSharedPeptideCount();

        return sharedPeptideCount;
    }

    private static int resolveSharedPeptideCount() {
        int peptideCount;

        if (JamProperties.isSet(SHARED_PEPTIDE_DISTRIB_PROPERTY))
            peptideCount = (int) RealDistribution.resolve(SHARED_PEPTIDE_DISTRIB_PROPERTY).sample();
        else
            peptideCount = JamProperties.getRequiredInt(SHARED_PEPTIDE_COUNT_PROPERTY);

        if (peptideCount < 1)
            throw new IllegalStateException("Shared peptide count must be positive.");

        return peptideCount;
    }

    /**
     * Returns the number of self-peptides presented only in the
     * thymic cortex.
     *
     * @return the number of self-peptides presented only in the
     * thymic cortex.
     */
    public static int getCortexPrivateCount() {
        if (cortexPrivateCount == null)
            cortexPrivateCount = resolveCortexPrivateCount();

        return cortexPrivateCount;
    }

    private static int resolveCortexPrivateCount() {
        int peptideCount;

        if (JamProperties.isSet(CORTEX_PRIVATE_DISTRIB_PROPERTY))
            peptideCount = (int) RealDistribution.resolve(CORTEX_PRIVATE_DISTRIB_PROPERTY).sample();
        else
            peptideCount = JamProperties.getRequiredInt(CORTEX_PRIVATE_COUNT_PROPERTY);

        if (peptideCount < 0)
            throw new IllegalStateException("Cortex private peptide count must be non-negative.");

        return peptideCount;
    }

    /**
     * Returns the number of self-peptides presented only in the
     * thymic medulla.
     *
     * @return the number of self-peptides presented only in the
     * thymic medulla.
     */
    public static int getMedullaPrivateCount() {
        if (medullaPrivateCount == null)
            medullaPrivateCount = resolveMedullaPrivateCount();

        return medullaPrivateCount;
    }

    private static int resolveMedullaPrivateCount() {
        int peptideCount;

        if (JamProperties.isSet(MEDULLA_PRIVATE_DISTRIB_PROPERTY))
            peptideCount = (int) RealDistribution.resolve(MEDULLA_PRIVATE_DISTRIB_PROPERTY).sample();
        else
            peptideCount = JamProperties.getRequiredInt(MEDULLA_PRIVATE_COUNT_PROPERTY);

        if (peptideCount < 0)
            throw new IllegalStateException("Medulla private peptide count must be non-negative.");

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
