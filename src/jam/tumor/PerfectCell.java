
package jam.tumor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a biological entity that divides into two identical
 * daughter cells (without mutation) at each time step.
 */
public class PerfectCell extends Propagator {
    //
    // The cell is alive when created, but dies at each time step when
    // it divides into two identical daughter cells...
    //
    private State state = State.ALIVE;

    private static int instanceCount = 0;

    private PerfectCell(PerfectCell parent) {
        super(instanceCount++, parent);
    }

    /**
     * Creates a perfect founder.
     *
     * @return a new perfect founder.
     */
    public static PerfectCell founder() {
        return new PerfectCell(null);
    }

    /**
     * Creates a copy of this perfect cell.
     *
     * @return a copy of this perfect cell.
     */
    public PerfectCell daughter() {
        return new PerfectCell(this);
    }

    /**
     * Advances this perfect cell by a single time step: the growth is
     * independent of the local environment.
     *
     * @return a list containing two new identical daughter cells.
     */
    public List<PerfectCell> advance() {
        return advance(null);
    }

    /**
     * Advances this perfect cell by a single time step: the growth is
     * independent of the local environment.
     *
     * @param localEnv the local environment (which does not affect
     * the propagation of perfect cells).
     *
     * @return a list containing two new identical daughter cells.
     */
    @Override public List<PerfectCell> advance(TumorEnv localEnv) {
        if (isDead()) {
            //
            // Dead cells do not divide...
            //
            return Collections.emptyList();
        }
        else {
            //
            // Propagation is independent of the local environment...
            //
            state = State.DEAD;
            return Arrays.asList(daughter(), daughter());
        }
    }

    @Override public State getState() {
        return state;
    }

    @Override public PerfectCell getFounder() {
        return (PerfectCell) super.getFounder();
    }

    @Override public PerfectCell getParent() {
        return (PerfectCell) super.getParent();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<PerfectCell> traceLineage() {
        return (List<PerfectCell>) super.traceLineage();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<PerfectCell> traceLineage(int firstGeneration) {
        return (List<PerfectCell>) super.traceLineage(firstGeneration);
    }
}
