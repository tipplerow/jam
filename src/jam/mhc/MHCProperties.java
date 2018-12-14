
package jam.mhc;

import jam.app.JamProperties;
import jam.math.DoubleUtil;
import jam.math.IntRange;

/**
 * Manages global system properties related to MHC alleles and
 * genotypes.
 */
public final class MHCProperties {
    private static Integer alleleCount = null;
    private static Integer anchorLength = null;

    private static IntRange anchorRegion = null;
    private static IntRange targetRegion = null;

    private static double activationEnergy = DoubleUtil.unset();
    private static double affinityThreshold = DoubleUtil.unset();

    /**
     * Name of the system property that defines the number of MHC
     * alleles in the genotype.
     */
    public static final String ALLELE_COUNT_PROPERTY = "jam.mhc.alleleCount";

    /**
     * Name of the system property that defines a common length for
     * all MHC anchor regions.
     */
    public static final String ANCHOR_LENGTH_PROPERTY = "jam.mhc.anchorLength";

    /**
     * Name of the system property that defines the indexes of the
     * MHC anchor residues that overlap with the target peptide.
     */
    public static final String ANCHOR_REGION_PROPERTY = "jam.mhc.anchorRegion";

    /**
     * Name of the system property that defines the indexes of the
     * target residues that overlap with the MHC anchor region.
     */
    public static final String TARGET_REGION_PROPERTY = "jam.mhc.targetRegion";

    /**
     * Name of the system property that defines the activation energy
     * for MHC-peptide binding.
     */
    public static final String ACTIVATION_ENERGY_PROPERTY = "jam.mhc.activationEnergy";

    /**
     * Name of the system property that defines the affinity threshold
     * for peptide presentation.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = "jam.mhc.affinityThreshold";

    /**
     * Returns the number of MHC alleles in the genotype.
     *
     * @return the number of MHC alleles in the genotype.
     */
    public static int getAlleleCount() {
        if (alleleCount == null)
            alleleCount = JamProperties.getRequiredInt(ALLELE_COUNT_PROPERTY, new IntRange(1, 6));

        return alleleCount;
    }

    /**
     * Returns the common length for all MHC anchor regions.
     *
     * @return the common length for all MHC anchor regions.
     */
    public static int getAnchorLength() {
        if (anchorLength == null)
            anchorLength = JamProperties.getRequiredInt(ANCHOR_LENGTH_PROPERTY, IntRange.POSITIVE);

        return anchorLength;
    }

    /**
     * Returns the indexes of the MHC anchor residues that overlap
     * with the target peptide.
     *
     * @return the indexes of the MHC anchor residues that overlap
     * with the target peptide.
     */
    public static IntRange getAnchorRegion() {
        if (anchorRegion == null)
            anchorRegion = IntRange.parse(JamProperties.getRequired(ANCHOR_REGION_PROPERTY));

        return anchorRegion;
    }

    /**
     * Returns the indexes of the target residues that overlap with
     * the MHC anchor region.
     *
     * @return the indexes of the target residues that overlap with
     * the MHC anchor region.
     */
    public static IntRange getTargetRegion() {
        if (targetRegion == null)
            targetRegion = IntRange.parse(JamProperties.getRequired(TARGET_REGION_PROPERTY));

        return targetRegion;
    }

    /**
     * Returns the activation energy for MHC-peptide binding.
     *
     * @return the activation energy for MHC-peptide binding.
     */
    public static double getActivationEnergy() {
        if (DoubleUtil.isUnset(activationEnergy))
            activationEnergy = JamProperties.getRequiredDouble(ACTIVATION_ENERGY_PROPERTY);

        return activationEnergy;
    }

    /**
     * Returns the affinity threshold for peptide presentation.
     *
     * @return the affinity threshold for peptide presentation.
     */
    public static double getAffinityThreshold() {
        if (DoubleUtil.isUnset(affinityThreshold))
            affinityThreshold = JamProperties.getRequiredDouble(AFFINITY_THRESHOLD_PROPERTY);

        return affinityThreshold;
    }
}
