
package jam.tumor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single tumor cell.
 */
public abstract class TumorCell extends UniformCarrier {
    //
    // Tumor cells are alive when created; the state becomes DEAD
    // during the time-step advancement if a death event occurs.
    //
    private State state = State.ALIVE;

    // Number of instances created...
    private static int instanceCount = 0;

    private static int nextIndex() {
        return instanceCount++;
    }

    /**
     * Creates all tumor cells.
     *
     * @param parent the parent cell; {@code null} for a founding cell.
     *
     * @param growthRate the intrinsic growth rate of the cell.
     *
     * @param originalMut the mutations originating in the cell.
     */
    protected TumorCell(UniformCarrier parent, GrowthRate growthRate, MutationList originalMut) {
        super(nextIndex(), parent, growthRate, originalMut);
    }

    /**
     * Creates a new daughter cell with this tumor cell as the parent.
     *
     * @param originalMut the mutations originating in the daughter.
     *
     * @return the new daughter cell.
     */
    public abstract TumorCell daughter(MutationList originalMut);

    /**
     * Advances this tumor cell by one time step.
     *
     * @param localEnv the local environment where this tumor cell
     * resides during the time step.
     *
     * @return a list containing any new tumor cells created by cell
     * division; the list will be empty if this parent cell does not
     * divide in the time step.
     */
    @Override public List<? extends TumorCell> advance(TumorEnv localEnv) {
        // Dead cells do not divide...
        if (isDead())
            return Collections.emptyList();

        // Stochastically sample the event to occur on this step...
        GrowthRate  growthRate  = getLocalGrowthRate(localEnv);
        GrowthCount growthCount = growthRate.sample(1);

        assert growthCount.getEventCount() <= 1;

        if (growthCount.getBirthCount() == 1)
            return advanceBirth(localEnv);
        else if (growthCount.getDeathCount() == 1)
            return advanceDeath();
        else
            return Collections.emptyList(); // Nothing happened...
    }

    private List<TumorCell> advanceBirth(TumorEnv localEnv) {
        //
        // This cell dies and is replaced by two daughters...
        //
        state = State.DEAD;
        return Arrays.asList(daughter(localEnv), daughter(localEnv));
    }

    private TumorCell daughter(TumorEnv localEnv) {
        return daughter(getMutator().generate(localEnv.getTimeStep()));
    }

    private List<TumorCell> advanceDeath() {
        //
        // This cell dies without reproducing...
        //
        state = State.DEAD;
        return Collections.emptyList();
    }

    @Override public State getState() {
        return state;
    }
}
