
package jam.tumor;

import java.util.List;

/**
 * Represents a biological entity that carries a unique set of
 * mutations mutations, e.g., a single tumor cell or a lineage
 * of identical cells.
 */
public abstract class UniformCarrier extends Carrier {
    // The intrinsic growth rate of this carrier...
    private final GrowthRate growthRate;

    // Only those mutations that originated in this carrier...
    private final MutationList originalMut;

    /**
     * Creates all uniform carriers.
     *
     * @param index the ordinal index of the carrier.
     *
     * @param parent the parent carrier; {@code null} for a founding carrier.
     *
     * @param growthRate the intrinsic growth rate of the carrier.
     *
     * @param originalMut the mutations originating in the carrier.
     */
    protected UniformCarrier(long index, UniformCarrier parent, GrowthRate growthRate, MutationList originalMut) {
        super(index, parent);

        this.growthRate  = growthRate;
        this.originalMut = originalMut;
    }

    /**
     * Returns the mutation generator for this carrier.
     *
     * @return the mutation generator for this carrier.
     */
    public abstract Mutator getMutator();

    /**
     * Computes the intrinsic growth rate of a daughter carrier,
     * derived from the intrinsic growth rate of this parent and
     * the new mutations originating in the daughter.
     *
     * @param originalMut the mutations originating in the daughter.
     *
     * @return the intrinsic growth rate of the daughter carrier.
     */
    public GrowthRate getDaughterGrowthRate(MutationList originalMut) {
        return Mutation.mutate(originalMut, growthRate);
    }

    /**
     * Returns the intrinsic growth rate of this carrier.
     *
     * @return the intrinsic growth rate of this carrier.
     */
    public final GrowthRate getIntrinsicGrowthRate() {
        return growthRate;
    }

    /**
     * Returns the growth rate of this carrier in a given local
     * environment.
     *
     * @param localEnv the local environment where the growth occurs.
     *
     * @return the growth rate of this carrier in the specified 
     * local environment.
     */
    public final GrowthRate getLocalGrowthRate(TumorEnv localEnv) {
        return localEnv.adjustGrowthRate(growthRate);
    }

    /**
     * Returns the mutations that originated in this carrier.
     *
     * @return the mutations that originated in this carrier.
     */
    public final MutationList getOriginalMutations() {
        return originalMut;
    }
}
