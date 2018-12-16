
package jam.tcell;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import jam.peptide.Peptide;

/**
 * Represents the complete set of T cell receptors circulating in the
 * periphery.
 */
public final class Repertoire extends AbstractCollection<TCR> {
    private final Collection<TCR> receptors;

    private Repertoire(Collection<? extends TCR> receptors) {
        this.receptors = Collections.unmodifiableCollection(receptors);
    }

    /**
     * Returns a repertoire view of a receptor collection.
     *
     * @param receptors the complete set of T cell receptors
     * circulating in the periphery.
     *
     * @return a repertoire view of the specified receptors.
     */
    public static Repertoire wrap(Collection<? extends TCR> receptors) {
        return new Repertoire(receptors);
    }

    /**
     * Identifies peptide targets that are recognized by this
     * repertoire.
     *
     * @param target the target to examine.
     *
     * @return {@code true} iff at least one receptor in this
     * repertoire recognizes the specified target peptide.
     */
    public boolean isRecognized(Peptide target) {
        for (TCR receptor : receptors)
            if (receptor.isRecognized(target))
                return true;

        return false;
    }

    @Override public Iterator<TCR> iterator() {
        return receptors.iterator();
    }

    @Override public int size() {
        return receptors.size();
    }
}
