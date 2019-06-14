
package jam.nap;

import java.util.Map;
import java.util.Set;

import jam.chem.Concentration;
import jam.hla.Allele;
import jam.peptide.Peptide;
import jam.stab.NetStab;
import jam.stab.StabilityCache;
import jam.stab.StabilityRecord;
import jam.tcga.PeptideType;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPeptideConcentrationProfile;

/**
 * Computes the neo-antigen presentation score (NAP) for a single HLA
 * allele.
 */
public final class NAPAlleleScorer {
    private final Allele allele;
    private final TumorBarcode barcode;

    private Set<Peptide> neoPeptides;
    private Set<Peptide> selfPeptides;

    private Map<Peptide, StabilityRecord> stabilityMap;
    private TumorPeptideConcentrationProfile concentrationProfile;

    private int neoBoundCount;
    private int selfBoundCount;

    private double neoTotalConc;
    private double selfTotalConc;

    private double neoWtMeanStab;
    private double selfWtMeanStab;

    private NAPAlleleScorer(TumorBarcode barcode, Allele allele) {
        this.allele = allele;
        this.barcode = barcode;
    }

    /**
     * The minimum stability percentile rank required for a peptide to
     * be classified as bound to its MHC molecule.
     */
    public static final double RANK_THRESHOLD = 2.0;

    /**
     * Computes the neo-antigen presentation score (NAP) for a given
     * tumor and allele.
     *
     * @param barcode the tumor to analyze.
     *
     * @param allele the HLA allele presenting the peptides.
     *
     * @return the neo-antigen presentation score for the specified
     * tumor and allele.
     */
    public static NAPAlleleScore compute(TumorBarcode barcode, Allele allele) {
        NAPAlleleScorer scorer = new NAPAlleleScorer(barcode, allele);
        return scorer.compute();
    }

    private NAPAlleleScore compute() {
        computePeptideConcentration();
        computePeptideMHCStability();
        computeScoreComponents();

        return new NAPAlleleScore(neoBoundCount, selfBoundCount,
                                  neoTotalConc,  selfTotalConc,
                                  neoWtMeanStab, selfWtMeanStab);
    }

    private void computePeptideConcentration() {
        concentrationProfile = TumorPeptideConcentrationProfile.process(barcode);

        neoPeptides = concentrationProfile.viewNeoPeptides();
        selfPeptides = concentrationProfile.viewSelfPeptides();
    }

    private void computePeptideMHCStability() {
        stabilityMap = StabilityRecord.map(StabilityCache.get(allele, concentrationProfile.viewPeptides()));
    }

    private void computeScoreComponents() {
        neoBoundCount = countBound(neoPeptides);
        selfBoundCount = countBound(selfPeptides);

        neoTotalConc = computeTotalConc(neoPeptides);
        selfTotalConc = computeTotalConc(selfPeptides);

        neoWtMeanStab = computeWtMeanStab(neoPeptides, neoTotalConc);
        selfWtMeanStab = computeWtMeanStab(selfPeptides, selfTotalConc);
    }

    private int countBound(Set<Peptide> peptides) {
        int count = 0;

        for (Peptide peptide : peptides) {
            StabilityRecord record = stabilityMap.get(peptide);

            if (record.getPercentile() >= RANK_THRESHOLD)
                ++count;
        }

        return count;
    }

    private double computeTotalConc(Set<Peptide> peptides) {
        double total = 0.0;

        for (Peptide peptide : peptides)
            total += concentrationProfile.getConcentration(peptide).doubleValue();

        return total;
    }

    private double computeWtMeanStab(Set<Peptide> peptides, double totalConc) {
        double wtStab = 0.0;

        for (Peptide peptide : peptides) {
            StabilityRecord stabRecord    = stabilityMap.get(peptide);
            Concentration   concentration = concentrationProfile.getConcentration(peptide);

            wtStab += concentration.doubleValue() * stabRecord.getHalfLife();
        }

        return wtStab / totalConc;
    }
}
