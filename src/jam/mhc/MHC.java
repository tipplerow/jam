
package jam.mhc;

import java.util.ArrayList;
import java.util.List;

import jam.math.IntRange;
import jam.peptide.Peptide;
import jam.peptide.PeptideBinder;
import jam.peptide.Residue;

/**
 * Represents an MHC allele and/or the corresponding MHC molecule.
 */
public interface MHC extends PeptideBinder, PeptidePresenter {
    /**
     * Returns the affinity threshold for peptide presentation: MHC
     * molecules must bind a peptide with affinity at or above this
     * threshold to present it to T cells.
     *
     * @return the affinity threshold for peptide presentation.
     */
    public abstract double getAffinityThreshold();

    /**
     * Generates a list of all unique anchor peptides for alleles
     * witht he global anchor length.
     *
     * @return a list of all unique anchor peptides for alleles
     * witht he global anchor length.
     *
     * @throws IllegalStateException unless the anchor region begins
     * with the first (index zero) anchor residue.
     */
    public static List<Peptide> enumerateAnchorPeptides() {
        if (MHCProperties.getAnchorRegion().lower() != 0)
            throw new IllegalStateException("MHC anchor region must begin at index zero.");

        return Peptide.enumerate(MHCProperties.getAnchorLength());
    }

    /**
     * Generates a list of all unique target peptides for alleles
     * whose anchors bind the global target region.
     *
     * @return a list of all unique target peptides for alleles
     * whose anchors bind the global target region.
     */
    public static List<Peptide> enumerateCanonicalTargets() {
        IntRange targetRegion = MHCProperties.getTargetRegion();
        List<Peptide> targetAnchors = Peptide.enumerate(targetRegion.size());

        if (targetRegion.lower() == 0)
            return targetAnchors;

        // Create a list of peptides padded with dummy residues (not
        // seen by the MHC anchor) to the left of the target region...
        Peptide paddingPeptide = getPaddingPeptide(targetRegion);
        List<Peptide> targetPeptides = new ArrayList<Peptide>(targetAnchors.size());

        for (Peptide targetAnchor : targetAnchors)
            targetPeptides.add(paddingPeptide.append(targetAnchor));

        return targetPeptides;
    }

    private static Peptide getPaddingPeptide(IntRange targetRegion) {
        int paddingLength = targetRegion.lower();
        Peptide paddingPeptide = Peptide.of(Residue.Ala);

        while (paddingPeptide.length() < paddingLength)
            paddingPeptide = paddingPeptide.append(Residue.Ala);

        return paddingPeptide;
    }               

    /**
     * Determines whether a target peptide is presented by this MHC
     * allele.
     *
     * @param target the peptide to examine.
     *
     * @return {@code true} iff the target peptide is presented by
     * this MHC allele.
     */
    @Override public default boolean isPresented(Peptide target) {
        return computeAffinity(target) >= getAffinityThreshold();
    }
}
