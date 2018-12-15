
package jam.mhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jam.lang.ObjectFactory;
import jam.math.DoubleRange;
import jam.peptide.Peptide;

/**
 * Represents a complete set of MHC alleles.
 */
public final class Genotype implements PeptidePresenter {
    private final Collection<MHC> alleles;

    /**
     * Creates a new fixed genotype.
     *
     * @param alleles the complete set of MHC alleles in the genotype.
     */
    public Genotype(Collection<? extends MHC> alleles) {
        this.alleles = Collections.unmodifiableCollection(new ArrayList<MHC>(alleles));
    }

    /**
     * Creates a new fixed genotype with the number of alleles
     * specified via system properties.
     *
     * @param factory the creator of individual MHC alleles.
     *
     * @return the new fixed genotype.
     */
    public static Genotype create(ObjectFactory<? extends MHC> factory) {
        int alleleCount = MHCProperties.getAlleleCount();
        List<MHC> alleleList = new ArrayList<MHC>(alleleCount);

        while (alleleList.size() < alleleCount)
            alleleList.add(newAllele(factory));

        return new Genotype(alleleList);
    }

    /**
     * Returns a new allele with a canonical presentation rate within
     * the allowed region specified by the system properties.
     *
     * @param <V> the runtime MHC type.
     *
     * @param factory the creator of individual MHC alleles.
     *
     * @return a new allele with a canonical presentation rate within
     * the allowed region specified by the system properties.
     */
    public static <V extends MHC> V newAllele(ObjectFactory<V> factory) {
        DoubleRange presentationRange = MHCProperties.getPresentationRange();
        List<Peptide> canonicalTargets = MHCProperties.enumerateCanonicalTargets();

        for (int attempt = 0; attempt < 1000; ++attempt) {
            V allele = factory.newInstance();
            double preRate = allele.computePresentationRate(canonicalTargets);

            if (presentationRange.contains(preRate))
                return allele;
        }

        throw new IllegalStateException("Failed to generate an allele with a valid presentation rate.");
    }

    /**
     * Returns a read-only view of the alleles in this genotype.
     *
     * @return a read-only view of the alleles in this genotype.
     */
    public Collection<MHC> viewAlleles() {
        return alleles;
    }

    @Override public boolean isPresented(Peptide target) {
        for (MHC allele : alleles)
            if (allele.isPresented(target))
                return true;

        return false;
    }
}
