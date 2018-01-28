
package jam.tumor;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a biological entity that lives forever and clones itself
 * (produces one copy of itself) at each time step.
 */
public class Replicator extends Propagator {
    private static int instanceCount = 0;

    private Replicator(Replicator parent) {
        super(instanceCount++, parent);
    }

    /**
     * Creates a founding replicator.
     *
     * @return a new founding replicator.
     */
    public static Replicator founder() {
        return new Replicator(null);
    }

    /**
     * Creates a copy of this replicator.
     *
     * @return a copy of this replicator.
     */
    public Replicator daughter() {
        return new Replicator(this);
    }

    /**
     * Advances this replicator by a single time step: the growth is
     * independent of the local environment.
     *
     * @return a list containing one new identical daughter cell.
     */
    public List<Replicator> advance() {
        return advance(null);
    }

    /**
     * Advances this replicator by a single time step: the growth is
     * independent of the local environment.
     *
     * @param localEnv the local environment (which does not affect
     * the propagation of replicators).
     *
     * @return a list containing one new identical daughter cell.
     */
    @Override public List<Replicator> advance(TumorEnv localEnv) {
        //
        // Propagation is independent of the local environment...
        //
        return Arrays.asList(daughter());
    }

    @Override public State getState() {
        return State.ALIVE;
    }

    @Override public Replicator getFounder() {
        return (Replicator) super.getFounder();
    }

    @Override public Replicator getParent() {
        return (Replicator) super.getParent();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<Replicator> traceLineage() {
        return (List<Replicator>) super.traceLineage();
    }

    @SuppressWarnings("unchecked") 
    @Override public List<Replicator> traceLineage(int firstGeneration) {
        return (List<Replicator>) super.traceLineage(firstGeneration);
    }
}
