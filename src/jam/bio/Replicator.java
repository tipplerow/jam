
package jam.bio;

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
