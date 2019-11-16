
package jam.nap;

import java.util.Map;
import java.util.TreeMap;

import jam.hla.Allele;
import jam.hla.Genotype;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPeptideConcentrationProfile;

/**
 * Computes the neo-antigen presentation score (NAP) for a complete
 * HLA genotype.
 */
public final class NAPGenotypeScorer {
    private final Genotype genotype;
    private final TumorBarcode barcode;

    private Map<Allele, NAPAlleleScore> alleleScores;
    private TumorPeptideConcentrationProfile concProfile;

    private NAPGenotypeScorer(TumorBarcode barcode, Genotype genotype) {
        this.barcode = barcode;
        this.genotype = genotype;
    }

    /**
     * Computes the neo-antigen presentation score (NAP) for a given
     * tumor and genotype.
     *
     * @param barcode the tumor to analyze.
     *
     * @param genotype the complete HLA genotype.
     *
     * @return the neo-antigen presentation score for the specified
     * tumor and genotype.
     */
    public static NAPGenotypeScore compute(TumorBarcode barcode, Genotype genotype) {
        NAPGenotypeScorer scorer = new NAPGenotypeScorer(barcode, genotype);
        return scorer.compute();
    }

    private NAPGenotypeScore compute() {
        alleleScores = new TreeMap<Allele, NAPAlleleScore>();
        concProfile  = TumorPeptideConcentrationProfile.process(barcode);

        for (Allele allele : genotype.viewUniqueAlleles())
            alleleScores.put(allele, NAPAlleleScorer.compute(allele, concProfile));

        return new NAPGenotypeScore(alleleScores, concProfile);
    }
}
