
package jam.tumor;

import java.util.Collection;
import java.util.List;

/**
 * Represents a biological entity that carries and accumulates
 * mutations, e.g., a tumor cell, cell lineage, or deme.
 */
public abstract class Carrier extends Propagator {
    private MutationList accumulatedMut = null;

    /**
     * Creates all carriers.
     *
     * @param index the ordinal index of the carrier.
     *
     * @param parent the parent of the new carrier; {@code null}
     * for a founding carrier.
     */
    protected Carrier(long index, Carrier parent) {
        super(index, parent);
    }

    /**
     * Accumulates the original mutations from a collection of
     * carriers.
     *
     * @param carriers the carriers to aggregate.
     *
     * @return a new mutation list containing all original mutations
     * contained in the input carriers in the order returned by the
     * collection iterator.
     */
    public static MutationList accumulate(Collection<? extends Carrier> carriers) {
        MutationList accumulated = MutationList.EMPTY;
            
        for (Carrier carrier : carriers)
            accumulated = accumulated.append(carrier.getOriginalMutations());

        return accumulated;
    }

    /**
     * Returns the mutations that originated in this carrier.
     *
     * @return the mutations that originated in this carrier.
     */
    public abstract MutationList getOriginalMutations();

    /**
     * Returns all mutations that have accumulated in this carrier
     * (traced back to the original founding carrier).
     *
     * @return all mutations that have accumulated in this carrier.
     */
    public final MutationList getAccumulatedMutations() {
        if (accumulatedMut == null)
            accumulateMutations();

        return accumulatedMut;
    }

    private void accumulateMutations() {
        //
        // Start with an empty list, then append all mutations,
        // starting with the founder and moving forward through 
        // the lineage...
        //
        accumulatedMut = accumulate(traceLineage());
    }

    @SuppressWarnings("unchecked")
    @Override public List<? extends Carrier> traceLineage() {
        return (List<? extends Carrier>) super.traceLineage();
    }

    @SuppressWarnings("unchecked")
    @Override public List<? extends Carrier> traceLineage(int firstGeneration) {
        return (List<? extends Carrier>) super.traceLineage(firstGeneration);
    }
}
