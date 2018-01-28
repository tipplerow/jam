
package jam.tumor;

import java.util.List;

/**
 * Represents a single tumor cell in which <em>neutral</em> mutations
 * arise at a fixed rate.
 */
public final class NeutralTumorCell extends TumorCell {
    private final Mutator mutator;

    private NeutralTumorCell(TumorCell    parentCell,
                             GrowthRate   growthRate, 
                             MutationList originalMut, 
                             Mutator      mutator) {
        super(parentCell, growthRate, originalMut);
        this.mutator = mutator;
    }

    /**
     * Creates a new founding cell with no mutations.
     *
     * @param growthRate the initial growth rate of the cell.
     *
     * @param mutationRate the fixed mutation rate of the cell.
     *
     * @return the founding cell.
     */
    public static NeutralTumorCell founder(GrowthRate growthRate, MutationRate mutationRate) {
        TumorCell    parentCell  = null;
        MutationList originalMut = MutationList.EMPTY;
        Mutator      mutator     = Mutator.neutral(mutationRate);

        return new NeutralTumorCell(parentCell, growthRate, originalMut, mutator);
    }

    @Override public Mutator getMutator() {
        return mutator;
    }

    @Override public NeutralTumorCell daughter(MutationList originalMut) {
        TumorCell  parentCell  = this;
        GrowthRate growthRate  = this.getDaughterGrowthRate(originalMut);
        Mutator    mutator     = this.mutator;

        return new NeutralTumorCell(parentCell, growthRate, originalMut, mutator);
    }

    @Override public NeutralTumorCell getParent() {
        return (NeutralTumorCell) super.getParent();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<NeutralTumorCell> advance(TumorEnv localEnv) {
        return (List<NeutralTumorCell>) super.advance(localEnv);
    }
}
