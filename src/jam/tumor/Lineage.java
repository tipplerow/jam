
package jam.tumor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.dist.BinomialDistribution;
import jam.math.Probability;

/**
 * Represents a multi-cell lineage in which each cell is identical
 * (has accumulated the same mutations).
 */
public abstract class Lineage extends UniformCarrier {
    // The number of cells in this lineage...
    private int cellCount; 

    // Number of instances created...
    private static int instanceCount = 0;

    private static int nextIndex() {
        return instanceCount++;
    }

    /**
     * Creates all lineages.
     *
     * @param parent the parent lineage; {@code null} for a founding
     * lineage.
     *
     * @param growthRate the intrinsic growth rate of the cells in the
     * lineage.
     *
     * @param originalMut the mutations originating in the lineage.
     *
     * @param cellCount the initial number of cells in this lineage.
     */
    protected Lineage(Lineage parent, GrowthRate growthRate, MutationList originalMut, int cellCount) {
        super(nextIndex(), parent, growthRate, originalMut);

        validateInitialCellCount(cellCount);
        this.cellCount = cellCount;
    }

    private static void validateInitialCellCount(int cellCount) {
        if (cellCount <= 0)
            throw new IllegalArgumentException("Initial cell count must be positive.");
    }

    /**
     * Lineages with cell counts above this limit will be treated in a
     * <em>semi-stochastic</em> manner for improved efficiency; those
     * with cell counts at or below this limit will be treated with
     * exact iteration over all member cells.
     */
    public static final int EXACT_ENUMERATION_LIMIT = 10;

    /**
     * Number of cells in a newly mutated daughter lineage.
     */
    public static final int DAUGHTER_CELL_COUNT = 1;

    /**
     * Creates a new single-cell lineage with one daughter cell (and
     * this lineage as the parent).
     *
     * @param originalMut the mutations originating in the daughter.
     *
     * @return a single-cell lineage containing the daughter cell.
     */
    public abstract Lineage daughter(MutationList originalMut);

    /**
     * Creates a new multi-cell lineage to represent the "fission
     * product" produced when this lineage divides.
     *
     * @param cellCount the number of cells in the fission product.
     *
     * @return a new multi-cell daughter lineage with no original
     * mutations.
     */
    public abstract Lineage fission(int cellCount);

    /**
     * Stochastically partitions the cells in this lineage between
     * this and a new "fission product".
     *
     * <p>The size of the new lineage is a random variable drawn from
     * the binomial distribution {@code B(n, 1 - p)}, where {@code n}
     * is the original size of this lineage and {@code p} is the
     * retention probability.
     *
     * <p>Note that this lineage shrinks by the number of cells moved
     * to the fission product, therefore <em>this lineage may be empty
     * following the division</em>.
     *
     * @param retentionProb the probability that this lineage will
     * retain any given cell.
     *
     * @return the fission product, or {@code null} if this lineage
     * retained all cells (not unlikely for a small lineage).
     */
    public Lineage divide(Probability retentionProb) {
        Probability transferProb = retentionProb.not();
        BinomialDistribution transferDist = BinomialDistribution.create(cellCount, transferProb);

        Lineage fissionObj   = null;
        int     fissionCount = transferDist.sample();

        if (fissionCount > 0) {
            fissionObj  = fission(fissionCount);
            cellCount  -= fissionCount;
        }

        return fissionObj;
    }

    /**
     * Advances this lineage by one time step.
     *
     * @param localEnv the local environment where this lineage
     * resides during the time step.
     *
     * @return a list containing any new lineages created by mutation;
     * the list will be empty if no mutations originate in the cycle.
     */
    @Override public List<Lineage> advance(TumorEnv localEnv) {
        //
        // Dead lineages do not advance further...
        //
        if (isDead())
            return Collections.emptyList();

        // Update the cell count for the number of birth and death
        // events...
        GrowthCount growthCount = resolveGrowthCount(localEnv);

        cellCount += growthCount.getBirthCount();
        cellCount -= growthCount.getDeathCount();

        // Each birth event creates two daughter cells... 
        int daughterCount = 2 * growthCount.getBirthCount();
        assert daughterCount <= cellCount;

        // Store each mutated daughter cell as a new single-cell
        // lineage...
        List<Lineage> daughters = new ArrayList<Lineage>();

        for (int daughterIndex = 0; daughterIndex < daughterCount; ++daughterIndex) {
            //
            // Stochastically generate the mutations originating in
            // this daughter cell...
            //
            MutationList daughterMutations = getMutator().generate(localEnv.getTimeStep());

            if (!daughterMutations.isEmpty()) {
                //
                // Spawn a new lineage for this mutated daughter
                // cell...
                //
                daughters.add(daughter(daughterMutations));
                --cellCount;
            }
        }

        assert cellCount >= 0;
        return daughters;
    }

    private GrowthCount resolveGrowthCount(TumorEnv localEnv) {
        GrowthRate growthRate = getLocalGrowthRate(localEnv);

        if (cellCount <= localEnv.getExactEnumerationLimit())
            return growthRate.sample(cellCount);
        else
            return growthRate.compute(cellCount);
    }

    /**
     * Returns the number of cells in this lineage.
     *
     * @return the number of cells in this lineage.
     */
    public final int countCells() {
        return cellCount;
    }

    /**
     * Identifies empty (extinguished or dead) lineages.
     *
     * @return {@code true} iff there are no cells remaining in this
     * lineage.
     */
    public final boolean isEmpty() {
        return cellCount == 0;
    }

    @Override public State getState() {
        return isEmpty() ? State.DEAD : State.ALIVE;
    }

    @Override public String toString() {
        return String.format("%s(%d; %d x %s)", getClass().getSimpleName(), getIndex(), 
                             countCells(), getOriginalMutations().toString());
    }
}
