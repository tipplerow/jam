
package jam.nap;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jam.math.DoubleComparator;
import jam.math.DoubleUtil;
import jam.math.Probability;

/**
 * Encapsulates the components of the neo-antigen presentation score.
 *
 * <p>This class defines three metrics for neo-antigen presentation
 * using the following components:
 * <ul>
 * <li>{@code B_n}: the number of neo-peptides  bound to MHC with stability above a common threshold.</li>
 * <li>{@code B_s}: the number of self-peptides bound to MHC with stability above a common threshold.</li>
 * <li>{@code C_n}: the total concentration of expressed and translated neo-peptides.</li>
 * <li>{@code C_s}: the total concentration of expressed and translated self-peptides.</li>
 * <li>{@code H_n}: the concentration-weighted mean half-life for dissociation for neo-peptides.</li>
 * <li>{@code H_s}: the concentration-weighted mean half-life for dissociation for neo-peptides.</li>
 * </ul>
 *
 * <p>The three metrics are defined as follows:
 * <ol>
 * <li>
 *   Binding threshold score {@code = B_n / (B_n + B_s)}.
 * </li>
 * <li>
 *   Concentration ratio score {@code = C_n / (C_n + C_s)}.
 * </li>
 * <li>
 *   Stability ratio score {@code = C_n * H_n / (C_n * H_n + C_s * H_s)}.
 * </li>
 * </ol>
 *
 * <p>Each score is a fractional value (on the interval {@code [0,1]}) and
 * and is reported as a probability that a neo-antigen will be presented on
 * the cell surface by the HLA allele.
 */
public final class NAPAlleleScore {
    private final int neoBoundCount;
    private final int selfBoundCount;

    private final double neoTotalConc;
    private final double selfTotalConc;

    private final double neoWtMeanStab;
    private final double selfWtMeanStab;

    private final Probability bindScore;
    private final Probability concScore;
    private final Probability stabScore;

    NAPAlleleScore(int neoBoundCount,
                   int selfBoundCount,
                   double neoTotalConc,
                   double selfTotalConc,
                   double neoWtMeanStab,
                   double selfWtMeanStab) {
        this.neoBoundCount = neoBoundCount;
        this.selfBoundCount = selfBoundCount;

        this.neoTotalConc = neoTotalConc;
        this.selfTotalConc = selfTotalConc;

        this.neoWtMeanStab = neoWtMeanStab;
        this.selfWtMeanStab = selfWtMeanStab;

        validate();

        this.bindScore = computeBindScore();
        this.concScore = computeConcScore();
        this.stabScore = computeStabScore();
    }

    private void validate() {
        if (neoBoundCount < 0)
            throw new IllegalArgumentException("Number of binding neo-antigens cannot be negative.");

        if (selfBoundCount < 0)
            throw new IllegalArgumentException("Number of binding self-antigens cannot be negative.");

        if (DoubleComparator.DEFAULT.isNegative(neoTotalConc))
            throw new IllegalArgumentException("Total neo-antigen concentration cannot be negative.");

        if (DoubleComparator.DEFAULT.isNegative(selfTotalConc))
            throw new IllegalArgumentException("Total self-antigen concentration cannot be negative.");

        if (DoubleComparator.DEFAULT.isNegative(neoWtMeanStab))
            throw new IllegalArgumentException("Mean neo-antigen stability cannot be negative.");

        if (DoubleComparator.DEFAULT.isNegative(selfWtMeanStab))
            throw new IllegalArgumentException("Mean self-antigen stability cannot be negative.");
    }

    private Probability computeBindScore() {
        int numer = neoBoundCount;
        int denom = neoBoundCount + selfBoundCount;

        if (denom > 0)
            return Probability.valueOf(DoubleUtil.ratio(numer, denom));
        else
            return Probability.ZERO;
    }

    private Probability computeConcScore() {
        double numer = neoTotalConc;
        double denom = neoTotalConc + selfTotalConc;

        if (denom > 1.0E-12)
            return Probability.valueOf(numer / denom);
        else
            return Probability.ZERO;
    }

    private Probability computeStabScore() {
        double  neoStab =  neoTotalConc *  neoWtMeanStab;
        double selfStab = selfTotalConc * selfWtMeanStab;

        double numer = neoStab;
        double denom = neoStab + selfStab;

        if (denom > 1.0E-12)
            return Probability.valueOf(numer / denom);
        else
            return Probability.ZERO;
    }

    /**
     * The minimum half-life required for a peptide to be classified
     * as bound to its MHC molecule (in hours).
     */
    public static final double HALF_LIFE_THRESHOLD = 1.0;

    /**
     * Extracts the binding threshold scores from a collection of
     * allele scores.
     *
     * @param alleleScores the allele scores to extract from.
     *
     * @return the binding threshold scores from the input collection.
     */
    public static List<Probability> getBindingThresholdScores(Collection<NAPAlleleScore> alleleScores) {
        return alleleScores.stream().map(x -> x.getBindingThresholdScore()).collect(Collectors.toList());
    }

    /**
     * Returns the concentration ratio scores from a collection of
     * allele scores.
     *
     * @param alleleScores the allele scores to extract from.
     *
     * @return the concentration ratio scores from the input collection.
     */
    public static List<Probability> getConcentrationRatioScores(Collection<NAPAlleleScore> alleleScores) {
        return alleleScores.stream().map(x -> x.getConcentrationRatioScore()).collect(Collectors.toList());
    }

    /**
     * Returns the stability ratio scores from a collection of allele
     * scores.
     *
     * @param alleleScores the allele scores to extract from.
     *
     * @return the stability ratio scores from the input collection.
     */
    public static List<Probability> getStabilityRatioScores(Collection<NAPAlleleScore> alleleScores) {
        return alleleScores.stream().map(x -> x.getStabilityRatioScore()).collect(Collectors.toList());
    }

    /**
     * Returns the number of expressed and translated neo-peptides
     * bound strongly by the HLA allele (above the rank threshold).
     *
     * @return the number of strongly bound neo-peptides.
     */
    public int getNeoBoundCount() {
        return neoBoundCount;
    }

    /**
     * Returns the number of expressed and translated self-peptides
     * bound strongly by the HLA allele (above the rank threshold).
     *
     * @return the number of strongly bound self-peptides.
     */
    public int getSelfBoundCount() {
        return selfBoundCount;
    }

    /**
     * Returns the total concentration of neo-peptides in the tumor.
     *
     * @return the total concentration of neo-peptides.
     */
    public double getNeoTotalConc() {
        return neoTotalConc;
    }

    /**
     * Returns the total concentration of self-peptides in the tumor.
     *
     * @return the total concentration of self-peptides.
     */
    public double getSelfTotalConc() {
        return selfTotalConc;
    }

    /**
     * Returns the concentration-weighted mean stability (half-life
     * for dissociation in hours) for the neo-peptides in the tumor
     * when bound to the HLA allele.
     *
     * @return the concentration-weighted mean stability for the
     * neo-peptides in the tumor.
     */
    public double getNeoWtMeanStab() {
        return neoWtMeanStab;
    }

    /**
     * Returns the concentration-weighted mean stability (half-life
     * for dissociation in hours) for the self-peptides in the tumor
     * when bound to the HLA allele.
     *
     * @return the concentration-weighted mean stability for the
     * self-peptides in the tumor.
     */
    public double getSelfWtMeanStab() {
        return selfWtMeanStab;
    }

    /**
     * Returns the binding threshold score for the tumor and allele.
     *
     * @return the binding threshold score for the tumor and allele.
     */
    public Probability getBindingThresholdScore() {
        return bindScore;
    }

    /**
     * Returns the concentration ratio score for the tumor and allele.
     *
     * @return the concentration ratio score for the tumor and allele.
     */
    public Probability getConcentrationRatioScore() {
        return concScore;
    }

    /**
     * Returns the stability ratio score for the tumor and allele.
     *
     * @return the stability ratio score for the tumor and allele.
     */
    public Probability getStabilityRatioScore() {
        return stabScore;
    }
}
