
package jam.nap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.chem.Concentration;
import jam.hla.Allele;
import jam.peptide.Peptide;
import jam.stab.NetStab;
import jam.stab.StabilityCache;
import jam.stab.StabilityRecord;
import jam.tcga.TumorPeptideConcentrationProfile;

/**
 * Computes the neo-antigen presentation score (NAP) for a single HLA
 * allele.
 */
public final class NAPAlleleScorer {
    private final Allele allele;
    private final TumorPeptideConcentrationProfile profile;

    private Map<Peptide, StabilityRecord> stabilityMap;

    private int neoBoundCount;
    private int selfBoundCount;

    private double neoTotalConc;
    private double selfTotalConc;

    private double neoWtMeanStab;
    private double selfWtMeanStab;

    private NAPAlleleScorer(Allele allele, TumorPeptideConcentrationProfile profile) {
        this.allele = allele;
        this.profile = profile;
    }

    /**
     * Computes the neo-antigen presentation score (NAP) for a given
     * allele and tumor protein concentration profile.
     *
     * @param allele the HLA allele presenting the peptides.
     *
     * @param profile the concentration of expressed and translated
     * proteins in the tumor under analysis.
     *
     * @return the neo-antigen presentation score for the specified
     * allele and concentration profile.
     */
    public static NAPAlleleScore compute(Allele allele, TumorPeptideConcentrationProfile profile) {
        NAPAlleleScorer scorer = new NAPAlleleScorer(allele, profile);
        return scorer.compute();
    }

    private NAPAlleleScore compute() {
        computePeptideMHCStability();
        computeScoreComponents();

        return new NAPAlleleScore(neoBoundCount, selfBoundCount,
                                  neoTotalConc,  selfTotalConc,
                                  neoWtMeanStab, selfWtMeanStab);
    }

    private void computePeptideMHCStability() {
        //
        // Use the stability cache for the self-peptides, but
        // compute the stability for neo-peptides on the fly...
        //
        Set<Peptide> neoPeptides = profile.viewNeoPeptides();
        Set<Peptide> selfPeptides = profile.viewSelfPeptides();

        List<StabilityRecord> neoRecords = NetStab.run(allele, neoPeptides);
        List<StabilityRecord> selfRecords = StabilityCache.get(allele, selfPeptides);

        stabilityMap = StabilityRecord.map(selfRecords);

        for (StabilityRecord neoRecord : neoRecords)
            stabilityMap.put(neoRecord.getPeptide(), neoRecord);
    }

    private void computeScoreComponents() {
        Set<Peptide> neoPeptides = profile.viewNeoPeptides();
        Set<Peptide> selfPeptides = profile.viewSelfPeptides();

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

            if (record.getPercentile() >= NAPAlleleScore.RANK_THRESHOLD)
                ++count;
        }

        return count;
    }

    private double computeTotalConc(Set<Peptide> peptides) {
        double total = 0.0;

        for (Peptide peptide : peptides)
            total += profile.getConcentration(peptide).doubleValue();

        return total;
    }

    private double computeWtMeanStab(Set<Peptide> peptides, double totalConc) {
        double wtStab = 0.0;

        for (Peptide peptide : peptides) {
            StabilityRecord stabRecord    = stabilityMap.get(peptide);
            Concentration   concentration = profile.getConcentration(peptide);

            wtStab += concentration.doubleValue() * stabRecord.getHalfLife();
        }

        return wtStab / totalConc;
    }
}
