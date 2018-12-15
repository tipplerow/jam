
package jam.mhc;

import java.util.ArrayList;
import java.util.List;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.peptide.Peptide;

/**
 * Manages global system properties related to MHC alleles and
 * genotypes.
 */
public final class MHCProperties {
    private static Integer alleleCount = null;
    private static Integer anchorLength = null;

    private static IntRange anchorRegion = null;
    private static IntRange targetRegion = null;

    private static DoubleRange presentationRange = null;

    private static double activationEnergy = DoubleUtil.unset();
    private static double affinityThreshold = DoubleUtil.unset();

    private static List<Peptide> anchorPeptides = null;
    private static List<Peptide> canonicalTargets = null;

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
     * Name of the system property that defines the valid range of MHC
     * allele presentation rates (alleles presenting a total fraction
     * of peptides outside of this range are not used in genotypes).
     */
    public static final String PRESENTATION_RANGE_PROPERTY = "jam.mhc.presentationRange";

    /**
     * Generates a list of all unique anchor peptides for alleles with
     * the global anchor length.
     *
     * @return a list of all unique anchor peptides for alleles with
     * the global anchor length.
     *
     * @throws IllegalStateException unless the anchor region begins
     * with the first (index zero) anchor residue.
     */
    public static List<Peptide> enumerateAnchorPeptides() {
        if (anchorPeptides == null)
            generateAnchorPeptides();

        return anchorPeptides;
    }

    private static void generateAnchorPeptides() {
        IntRange anchorRegion = getAnchorRegion();

        if (anchorRegion.lower() != 0)
            throw new IllegalStateException("MHC anchor region must begin at index zero.");

        anchorPeptides = Peptide.enumerate(anchorRegion.size());
    }

    /**
     * Generates a list of all unique target peptides for alleles
     * whose anchors bind the global target region.
     *
     * @return a list of all unique target peptides for alleles whose
     * anchors bind the global target region.
     *
     * @throws IllegalStateException unless the target region begins
     * with the first (index zero) target residue.
     */
    public static List<Peptide> enumerateCanonicalTargets() {
        if (canonicalTargets == null)
            generateCanonicalTargets();

        return canonicalTargets;
    }

    private static void generateCanonicalTargets() {
        IntRange targetRegion = MHCProperties.getTargetRegion();

        if (targetRegion.lower() != 0)
            throw new IllegalStateException("MHC target region must begin at index zero.");

        canonicalTargets = Peptide.enumerate(targetRegion.size());
    }

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

    /**
     * Returns the valid range of MHC allele presentation rates
     * (alleles presenting a total fraction of peptides outside
     * of this range are not used in genotypes).
     *
     * @return the valid range of MHC allele presentation rates.
     */
    public static DoubleRange getPresentationRange() {
        if (presentationRange == null)
            presentationRange = DoubleRange.parse(JamProperties.getRequired(PRESENTATION_RANGE_PROPERTY));

        return presentationRange;
    }
}
