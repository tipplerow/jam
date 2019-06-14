
package jam.nap;

import java.util.Collections;
import java.util.Map;

import jam.hla.Allele;
import jam.math.Probability;
import jam.tcga.TumorPeptideConcentrationProfile;

/**
 * Encapsulates neo-antigen presentation (NAP) scores for a complete
 * HLA genotype.
 */
public final class NAPGenotypeScore {
    private final Map<Allele, NAPAlleleScore> alleleScores;

    private final int mutatedGeneCount;
    private final int expressedGeneCount;
    private final int totalMutationCount;

    private final Probability bindScore;
    private final Probability concScore;
    private final Probability stabScore;

    NAPGenotypeScore(Map<Allele, NAPAlleleScore> alleleScores,
                     TumorPeptideConcentrationProfile concProfile) {
        this.alleleScores = alleleScores;

        this.mutatedGeneCount = concProfile.getMutatedGeneCount();
        this.expressedGeneCount = concProfile.getExpressedGeneCount();
        this.totalMutationCount = concProfile.getTotalMutationCount();

        this.bindScore = computeBindScore();
        this.concScore = computeConcScore();
        this.stabScore = computeStabScore();
    }

    private Probability computeBindScore() {
        return Probability.anyOccur(NAPAlleleScore.getBindingThresholdScores(alleleScores.values()));
    }

    private Probability computeConcScore() {
        return Probability.anyOccur(NAPAlleleScore.getConcentrationRatioScores(alleleScores.values()));
    }

    private Probability computeStabScore() {
        return Probability.anyOccur(NAPAlleleScore.getStabilityRatioScores(alleleScores.values()));
    }

    /**
     * Returns the number of expressed genes in the tumor.
     *
     * @return the number of expressed genes in the tumor.
     */
    public int getExpressedGeneCount() {
        return expressedGeneCount;
    }

    /**
     * Returns the number of mutated genes in the tumor.
     *
     * @return the number of mutated genes in the tumor.
     */
    public int getMutatedGeneCount() {
        return mutatedGeneCount;
    }

    /**
     * Returns the total number of mutations in the tumor.
     *
     * @return the total number of mutations in the tumor.
     */
    public int getTotalMutationCount() {
        return totalMutationCount;
    }

    /**
     * Returns the binding threshold score for the tumor and genotype.
     *
     * @return the binding threshold score for the tumor and genotype.
     */
    public Probability getBindingThresholdScore() {
        return bindScore;
    }

    /**
     * Returns the concentration ratio score for the tumor and genotype.
     *
     * @return the concentration ratio score for the tumor and genotype.
     */
    public Probability getConcentrationRatioScore() {
        return concScore;
    }

    /**
     * Returns the stability ratio score for the tumor and genotype.
     *
     * @return the stability ratio score for the tumor and genotype.
     */
    public Probability getStabilityRatioScore() {
        return stabScore;
    }

    /**
     * Returns a read-only view of the individual allele scores.
     *
     * @return a read-only view of the individual allele scores.
     */
    public Map<Allele, NAPAlleleScore> viewAlleleScores() {
        return Collections.unmodifiableMap(alleleScores);
    }
}
