
package jam.tumor;

import java.util.Collection;

import jam.lang.Ordinal;

/**
 * Represents a mutation that alters the growth rate of a propagating
 * entity.
 */
public abstract class Mutation extends Ordinal {
    private final int creationTime;

    // Number of instances created...
    private static int instanceCount = 0;

    private static int nextIndex() {
        return instanceCount++;
    }

    /**
     * The single mutation responsible for transformation to malignancy.
     */
    public static final Mutation TRANSFORMER = neutral(0);

    /**
     * Creates a new mutation with an automatically generated index.
     *
     * @param creationTime the index of the current time step (when 
     * the mutation is created).
     */
    protected Mutation(int creationTime) {
        super(nextIndex());
        this.creationTime = creationTime;
    }

    /**
     * Creates a new neutral (passenger) mutation.
     *
     * @param creationTime the index of the current time step (when
     * the mutation is created).
     *
     * @return the new neutral mutation.
     */
    public static Mutation neutral(int creationTime) {
        return new Neutral(creationTime);
    }

    private static final class Neutral extends Mutation {
        private Neutral(int creationTime) {
            super(creationTime);
        }

        @Override public boolean isNeutral() {
            return true;
        }

        @Override public boolean isSelective() {
            return false;
        }

        @Override public GrowthRate mutate(GrowthRate rate) {
            //
            // Neutral mutations leave the growth rate unchanged...
            //
            return rate;
        }
    }

    /**
     * Identifies neutral (passenger) mutations.
     *
     * @return {@code true} iff this mutation was neutral for the
     * originating carrier.
     */
    public abstract boolean isNeutral();

    /**
     * Identifies selective (driver) mutations.
     *
     * @return {@code true} iff this mutation provided a selective
     * advantage or disadvantage for the originating carrier.
     */
    public abstract boolean isSelective();

    /**
     * Evaluates the effect of this mutation on a given growth rate.
     *
     * @param rate the original growth rate.
     *
     * @return the growth rate following the mutation; the original
     * rate is unchanged.
     */
    public abstract GrowthRate mutate(GrowthRate rate);

    /**
     * Returns the index of the time step when the mutation was
     * created.
     *
     * @return the index of the time step when the mutation was
     * created.
     */
    public final int getCreationTime() {
        return creationTime;
    }

    /**
     * Evaluates the net effect of a collection of mutations on a
     * given growth rate.
     *
     * @param mutations the mutations to evaluate.
     *
     * @param rate the original growth rate.
     *
     * @return the growth rate following the mutation; the original
     * rate is unchanged.
     */
    public static GrowthRate mutate(Collection<Mutation> mutations, GrowthRate rate) {
        for (Mutation mutation : mutations)
            rate = mutation.mutate(rate);

        return rate;
    }

    @Override public String toString() {
        return String.format("%s(%d; %d)", getClass().getSimpleName(), getIndex(), getCreationTime());
    }
}
