
package jam.tumor;

import java.util.List;

/**
 * Represents a lineage of perfectly replicating cells.
 */
public final class PerfectLineage extends Lineage {
    private PerfectLineage(PerfectLineage parent, GrowthRate growthRate, int cellCount) {
        super(parent, growthRate, MutationList.EMPTY, cellCount);
    }

    /**
     * Creates a new founding lineage.
     *
     * @param growthRate the initial growth rate of the lineage.
     *
     * @param cellCount the initial number of cells in the lineage.
     *
     * @return the founding lineage.
     *
     * @throws IllegalArgumentException unless the cell count is
     * positive.
     */
    public static PerfectLineage founder(GrowthRate growthRate, int cellCount) {
        return new PerfectLineage(null, growthRate, cellCount);
    }

    @Override public Mutator getMutator() {
        return Mutator.EMPTY;
    }

    @Override public PerfectLineage fission(int cellCount) {
        PerfectLineage parent     = this;
        GrowthRate     growthRate = this.getIntrinsicGrowthRate();

        return new PerfectLineage(parent, growthRate, cellCount);
    }

    @Override public PerfectLineage daughter(MutationList originalMut) {
        //
        // A perfect lineage should never need to create daughter
        // lineages...
        //
        throw new UnsupportedOperationException();
    }

    @Override public PerfectLineage getParent() {
        return (PerfectLineage) super.getParent();
    }
}
