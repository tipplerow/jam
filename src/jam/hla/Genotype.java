
package jam.hla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

import org.apache.commons.math3.util.Combinations;

import jam.lang.JamException;
import jam.util.AbstractImmutableMultiset;

/**
 * Represents the complete HLA genotype for a single MHC class.
 */
public final class Genotype extends AbstractImmutableMultiset<Allele> implements Comparable<Genotype> {
    private Genotype(ImmutableSortedMultiset<Allele> alleles) {
        super(alleles);
    }

    /**
     * Creates a new fixed genotype.
     *
     * @param alleles the alleles that compose the genotype.
     *
     * @return the new genotype.
     */
    public static Genotype instance(Allele... alleles) {
        return new Genotype(ImmutableSortedMultiset.copyOf(alleles));
    }

    /**
     * Creates a new fixed genotype.
     *
     * @param alleles the alleles that compose the genotype.
     *
     * @return the new genotype.
     */
    public static Genotype instance(Collection<Allele> alleles) {
        return new Genotype(ImmutableSortedMultiset.copyOf(alleles));
    }

    /**
     * Constructs a new fixed genotype by parsing a string
     * representation.
     *
     * @param s the string representation.
     *
     * @param delim the delimiter separating the individual alleles.
     *
     * @return the fixed genotype described by the input string.
     */
    public static Genotype parse(String s, Pattern delim) {
        String[] fields  = delim.split(s);
        Allele[] alleles = new Allele[fields.length];

        for (int index = 0; index < fields.length; ++index)
            alleles[index] = Allele.instance(fields[index]);

        return instance(alleles);
    }

    /**
     * Returns the number of unique alleles in this genotype.
     *
     * @return the number of unique alleles in this genotype.
     */
    public int countUniqueAlleles() {
        return elementSet().size();
    }

    /**
     * Removes one or more alleles from this genotype to simulate a
     * loss-of-heterozygosity event.
     *
     * <p>If this genotype contains more than one copy of an allele in
     * the argument list, all copies of that allele will be removed.
     *
     * @param alleles the alleles to remove.
     *
     * @return a new genotype with the specified alleles removed.
     *
     * @throws RuntimeException unless this genotype contains every
     * allele in the argument list.
     */
    public Genotype delete(Allele... alleles) {
        return delete(List.of(alleles));
    }

    /**
     * Removes one or more alleles from this genotype to simulate a
     * loss-of-heterozygosity event.
     *
     * <p>If this genotype contains more than one copy of an allele in
     * the argument collection, all copies of that allele will be removed.
     *
     * @param alleles the alleles to remove.
     *
     * @return a new genotype with the specified alleles removed.
     *
     * @throws RuntimeException unless this genotype contains every
     * allele in the argument collection.
     */
    public Genotype delete(Collection<Allele> alleles) {
        Multiset<Allele> keep = HashMultiset.create(this);

        for (Allele allele : alleles)
            if (keep.contains(allele))
                keep.setCount(allele, 0);
            else
                throw JamException.runtime("Genotype does not contain allele [%s].", allele);

        return instance(keep);
    }

    /**
     * Enumerates all genotypes that may be formed from loss of
     * heterozygosity.
     *
     * @param count the number of unique alleles lost.
     *
     * @return a set containing all unique genotypes that may be
     * formed by losing the specified number of alleles.
     */
    public Set<Genotype> enumerateLOH(int count) {
        if (count < 1)
            throw new IllegalArgumentException("LOH count must be positive.");

        List<Allele> uniqueAlleles = new ArrayList<Allele>(viewUniqueAlleles());

        if (count > uniqueAlleles.size())
            throw new IllegalArgumentException("LOU count must not exceed the number of unique alleles.");

        Set<Genotype> lohGenotypes = new TreeSet<Genotype>();
        Combinations  combinations = new Combinations(uniqueAlleles.size(), count);

        for (int[] indexes : combinations) {
            Set<Allele> lohAlleles = copyUniqueAlleles();

            for (int index : indexes)
                lohAlleles.remove(uniqueAlleles.get(index));

            lohGenotypes.add(instance(lohAlleles));
        }

        return lohGenotypes;
    }

    /**
     * Identifies heterozygous genotypes.
     *
     * @return {@code true} iff this genotype contains exactly one
     * copy of each allele.
     */
    public boolean isHeterozygous() {
        return !isHomozygous();
    }

    /**
     * Identifies homozygous genotypes.
     *
     * @return {@code true} iff this genotype contains two or more
     * copies of any allele.
     */
    public boolean isHomozygous() {
        for (Multiset.Entry<Allele> entry : entrySet())
            if (entry.getCount() > 1)
                return true;

        return false;
    }

    /**
     * Returns the unique alleles in this genotype in a new set.
     *
     * @return the unique alleles in this genotype in a new set.
     */
    public Set<Allele> copyUniqueAlleles() {
        return new TreeSet<Allele>(elementSet());
    }

    /**
     * Returns a read-only view of the unique alleles in this genotype.
     *
     * @return a read-only view of the unique alleles in this genotype.
     */
    public Set<Allele> viewUniqueAlleles() {
        return elementSet();
    }

    @Override public int compareTo(Genotype that) {
        List<Allele> thisAlleles = new ArrayList<Allele>(this.elementSet());
        List<Allele> thatAlleles = new ArrayList<Allele>(that.elementSet());

        int minLength =
            Math.min(thisAlleles.size(), thatAlleles.size());

        for (int index = 0; index < minLength; ++index) {
            int alleleComp = thisAlleles.get(index).compareTo(thatAlleles.get(index));

            if (alleleComp != 0)
                return alleleComp;
        }
        
        return 0;
    }
}
