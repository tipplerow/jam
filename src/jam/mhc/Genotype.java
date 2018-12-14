
package jam.mhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jam.lang.ObjectFactory;
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
        return create(factory, MHCProperties.getAlleleCount());
    }

    /**
     * Creates a new fixed genotype with a given number of alleles.
     *
     * @param factory the creator of individual MHC alleles.
     *
     * @param count the number of alleles to include in the genotype.
     *
     * @return the new fixed genotype.
     */
    public static Genotype create(ObjectFactory<? extends MHC> factory, int count) {
        return new Genotype(factory.newInstances(count));
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
