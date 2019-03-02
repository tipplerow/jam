
package jam.mhc;

import java.util.List;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.math.IntUtil;
import jam.peptide.Peptide;
import jam.util.RegexUtil;

/**
 * Manages global system properties related to MHC alleles and
 * genotypes.
 */
public final class MHCProperties {
    private static int alleleCount = 0;

    private static DoubleRange presentationRange = null;

    private static double activationEnergy = DoubleUtil.unset();
    private static double affinityThreshold = DoubleUtil.unset();

    private static List<Peptide> anchorPeptides = null;
    private static List<Peptide> canonicalTargets = null;

    // Target interaction points: Zero-based indexes of the residues
    // on MHC-bound peptides that interact with the MHC anchor...
    private static List<Integer> TIPs = null;

    /**
     * Name of the system property that defines the number of MHC
     * alleles in the genotype.
     */
    public static final String ALLELE_COUNT_PROPERTY = "jam.mhc.alleleCount";

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
     * Name of the system property that defines <em>target interaction
     * points</em>: zero-based indexes of the amino acids on MHC-bound
     * peptides that interact with the MHC anchor.
     */
    public static final String TIPS_PROPERTY = "jam.mhc.targetInteractionPoints";

    /**
     * Returns the common length for all MHC anchors.
     *
     * @return the common length for all MHC anchors.
     */
    public static int getAnchorLength() {
        return viewTIPs().size();
    }

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
            anchorPeptides = Peptide.enumerate(getAnchorLength());

        return anchorPeptides;
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
            canonicalTargets = Peptide.enumerate(getCanonicalTargetLength());

        return canonicalTargets;
    }

    private static int getCanonicalTargetLength() {
        return IntUtil.max(viewTIPs());
    }

    /**
     * Returns the number of MHC alleles in the genotype.
     *
     * @return the number of MHC alleles in the genotype.
     */
    public static int getAlleleCount() {
        if (alleleCount < 1)
            alleleCount = JamProperties.getRequiredInt(ALLELE_COUNT_PROPERTY, new IntRange(1, 6));

        return alleleCount;
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

    /**
     * Returns a read-only view of the peptide-interaction points.
     *
     * @return a read-only view of the peptide-interaction points.
     */
    public static List<Integer> viewTIPs() {
        if (TIPs == null)
            TIPs = IntUtil.parseIntList(JamProperties.getRequired(TIPS_PROPERTY), RegexUtil.COMMA);

        return TIPs;
    }
}
