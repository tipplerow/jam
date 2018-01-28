
package jam.tumor;

import java.util.List;

/**
 * Represents a multi-cell lineage in which <em>neutral</em> mutations
 * arise at a fixed rate.
 */
public final class NeutralLineage extends Lineage {
    private final Mutator mutator;

    private NeutralLineage(NeutralLineage parent, 
                           GrowthRate     growthRate, 
                           MutationList   originalMut,
                           int            cellCount,
                           Mutator        mutator) {
        super(parent, growthRate, originalMut, cellCount);
        this.mutator = mutator;
    }

    /**
     * Creates a new founding lineage with no mutations.
     *
     * @param growthRate the initial growth rate of the lineage.
     *
     * @param mutationRate the fixed mutation rate of the neutral
     * lineage.
     *
     * @param cellCount the initial number of cells in the lineage.
     *
     * @return the founding lineage.
     *
     * @throws IllegalArgumentException unless the cell count is
     * positive.
     */
    public static NeutralLineage founder(GrowthRate growthRate, MutationRate mutationRate, int cellCount) {
        NeutralLineage parent      = null;
        MutationList   originalMut = MutationList.EMPTY;
        Mutator        mutator     = Mutator.neutral(mutationRate);

        return new NeutralLineage(parent, growthRate, originalMut, cellCount, mutator);
    }

    /**
     * Creates a new founding lineage with the single mutation
     * responsible for the transformation to malignancy.
     *
     * @param growthRate the initial growth rate of the lineage.
     *
     * @param mutationRate the fixed mutation rate of the neutral
     * lineage.
     *
     * @param cellCount the initial number of cells in the lineage.
     *
     * @return the transforming lineage.
     *
     * @throws IllegalArgumentException unless the cell count is
     * positive.
     */
    public static NeutralLineage transformer(GrowthRate growthRate, MutationRate mutationRate, int cellCount) {
        NeutralLineage parent      = null;
        MutationList   originalMut = MutationList.create(Mutation.TRANSFORMER);
        Mutator        mutator     = Mutator.neutral(mutationRate);

        return new NeutralLineage(parent, growthRate, originalMut, cellCount, mutator);
    }

    @Override public Mutator getMutator() {
        return mutator;
    }

    @Override public NeutralLineage fission(int cellCount) {
        NeutralLineage parent      = this;
        GrowthRate     growthRate  = this.getIntrinsicGrowthRate();
        MutationList   originalMut = MutationList.EMPTY;
        Mutator        mutator     = this.getMutator();

        return new NeutralLineage(parent, growthRate, originalMut, cellCount, mutator);
    }

    @Override public NeutralLineage daughter(MutationList originalMut) {
        NeutralLineage parent      = this;
        GrowthRate     growthRate  = this.getDaughterGrowthRate(originalMut);
        int            cellCount   = DAUGHTER_CELL_COUNT;
        Mutator        mutator     = this.mutator;

        return new NeutralLineage(parent, growthRate, originalMut, cellCount, mutator);
    }

    @Override public NeutralLineage getParent() {
        return (NeutralLineage) super.getParent();
    }
}
