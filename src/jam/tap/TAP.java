
package jam.tap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.io.FileUtil;
import jam.peptide.Peptide;
import jam.peptide.Residue;

/**
 * Predicts peptides that will be transported into the endoplasmic
 * reticulum by the transporter associated with antigen processing
 * (TAP) using a method reported by Peters et al., J. Immunol. 171,
 * 1741--1749 (2003).
 */
public final class TAP {
    private final double threshold;
    private final TAPMatrix matrix;

    private static TAP consensus = null;

    private TAP(TAPMatrix matrix, double threshold) {
        this.matrix = matrix;
        this.threshold = threshold;
    }

    /**
     * The optimal shrinkage factor for the contribution of N-terminal
     * residues to the binding score as reported by Peters.
     */
    public static final double SCORE_ALPHA = 0.2;

    /**
     * Name of the system property that defines the threshold score
     * for transport: peptides must score BELOW this threshold to be
     * transported.
     */
    public static final String THRESHOLD_SCORE_PROPERTY = "jam.tap.thresholdScore";

    /**
     * Default value for the threshold score for transport: 
     * Peters reports that 98% of known epitopes fall below this
     * threshold.
     */
    public static final double THRESHOLD_SCORE_DEFAULT = 1.0;

    /**
     * Returns the consensus TAP scorer.
     *
     * @return the consensus TAP scorer.
     */
    public static TAP consensus() {
        if (consensus == null)
            consensus = createConsensus();

        return consensus;
    }

    private static TAP createConsensus() {
        return new TAP(TAPMatrix.consensus(), resolveThreshold());
    }

    private static double resolveThreshold() {
        return JamProperties.getOptionalDouble(THRESHOLD_SCORE_PROPERTY,
                                               THRESHOLD_SCORE_DEFAULT);
    }

    /**
     * Identifies peptides that will be transported by TAP.
     *
     * @param peptide the peptide of interest.
     *
     * @return {@code true} iff the input peptide will be transported
     * by TAP.
     *
     * @throws IllegalArgumentException unless the peptide has length
     * nine or greater.
     */
    public boolean isTransported(Peptide peptide) {
        return score(peptide) <= threshold;
    }

    /**
     * Computes the TAP binding score for a peptide.
     *
     * @param peptide the peptide being processed.
     *
     * @return the TAP binding score for the given peptide.
     *
     * @throws IllegalArgumentException unless the peptide has length
     * nine or greater.
     */
    public double score(Peptide peptide) {
        //
        // This code implements Equation 3 from Peters et al.,
        // J. Immunol. 171, 1741--1749 (2003) with L = 9 and
        // alpha = 0.2.
        //
        int L = peptide.length();

        if (L < 9)
            throw new IllegalArgumentException("Peptide must have at least nine residues.");

        // Start with the contribution from the C terminus...
        int    cterm = L - 1;
        double score = matrix.get(peptide.at(cterm), TAPPosition.CTerm);

        // Add the alpha-scaled contributions from the first (L - 8)
        // N-terminal residues...
        for (int nterm1 = 0; nterm1 < (L - 8); ++nterm1)
            score += SCORE_ALPHA * matrix.get(peptide.at(nterm1), TAPPosition.NTerm1);

        // Add the raw contributions from the next two residues after
        // the N-terminal...
        int nterm2 = L - 8;
        int nterm3 = L - 7;

        score += matrix.get(peptide.at(nterm2), TAPPosition.NTerm2);
        score += matrix.get(peptide.at(nterm3), TAPPosition.NTerm3);

        return score;
    }

    /**
     * Identifies peptides that will be transported by TAP.
     *
     * @param peptides the peptide of interests.
     *
     * @return a list of peptides that are transported by TAP.
     *
     * @throws IllegalArgumentException if any peptide has length less
     * than nine.
     */
    public List<Peptide> transport(Collection<Peptide> peptides) {
        List<Peptide> transported = new ArrayList<Peptide>(peptides.size());

        for (Peptide peptide : peptides)
            if (isTransported(peptide))
                transported.add(peptide);

        return transported;
    }
}
