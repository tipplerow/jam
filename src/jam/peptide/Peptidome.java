
package jam.peptide;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jam.lang.ObjectFactory;
import jam.peptide.Peptide;
import jam.util.ListUtil;

/**
 * Represents an immutable set of peptides.
 */
public final class Peptidome extends AbstractSet<Peptide> {
    private final Set<Peptide> peptides;

    private Peptidome(Set<Peptide> peptides) {
        this.peptides = Collections.unmodifiableSet(peptides);
    }

    /**
     * The single peptidome containing no peptides.
     */
    public static final Peptidome EMPTY = new Peptidome(Collections.emptySet());

    /**
     * Creates a new peptidome with a fixed set of peptides.
     *
     * @param peptides the members of the peptidome.
     *
     * @return the new peptidome.
     */
    public static Peptidome create(Collection<? extends Peptide> peptides) {
        return new Peptidome(new LinkedHashSet<Peptide>(peptides));
    }

    /**
     * Creates a new peptidome with a specified size.
     *
     * @param factory the creator of individual peptides.
     *
     * @param size the number of peptides to create.
     *
     * @return the new peptidome.
     */
    public static Peptidome create(ObjectFactory<? extends Peptide> factory, int size) {
        Set<Peptide> peptides = new LinkedHashSet<Peptide>(size);

        while (peptides.size() < size)
            peptides.add(factory.newInstance());

        return new Peptidome(peptides);
    }

    /**
     * Reads peptides from a flat file containing one peptide per line
     * (and no header line).
     *
     * @param fileName the name of the flat file to read.
     *
     * @return a peptidome containing all peptides listed in the
     * specified file.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static Peptidome load(String fileName) {
        return Peptidome.create(Peptide.loadFlatFile(fileName));
    }

    /**
     * Randomly mutates the peptides in this peptidome and returns the
     * mutated peptides in a new peptidome; this peptidome is unchanged.
     *
     * <p>In one iteration, a parent peptide is selected at random and
     * then mutated; the process repeats {@code mutationCount} times.
     * Some parents may be selected more than once, some not at all.
     *
     * @param mutationCount the desired number of mutated peptides to
     * generate.
     *
     * @return a new peptidome containing the mutated peptides.
     */
    public Peptidome mutate(int mutationCount) {
        //
        // Dump the parents into an ArrayList for efficient random
        // selection...
        //
        List<Peptide> parents = new ArrayList<Peptide>(peptides);
        Set<Peptide>  mutants = new HashSet<Peptide>(mutationCount);

        while (mutants.size() < mutationCount)
            mutants.add(ListUtil.select(parents).mutate());

        return new Peptidome(mutants);
    }

    @Override public Iterator<Peptide> iterator() {
        return peptides.iterator();
    }

    @Override public int size() {
        return peptides.size();
    }
}
