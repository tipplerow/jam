
package jam.tumor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jam.lang.Ordinal;

/**
 * Represents a biologial entity that can propagate itself, e.g., a
 * cell, virus, or deme.
 *
 * <p>This class hierarcy is defined in the context of discrete-time
 * simulation: Propagators advance through time in discrete steps; at
 * each step, a propagator may produce offspring, become dormant, or
 * die (e.g., by apoptosis).
 */
public abstract class Propagator extends Ordinal {
    private final int generation;
    private final Propagator parent;
    private final Propagator founder;

    /**
     * Possible replication states of a propagator.
     */
    public enum State { ALIVE, DORMANT, DEAD };

    /**
     * Creates all propagators.
     *
     * @param index the ordinal index of the propagator.
     *
     * @param parent the parent of the new propagator; {@code null}
     * for a founding propagator.
     */
    protected Propagator(long index, Propagator parent) {
        super(index);
        this.parent = parent;

        if (parent == null) {
            this.founder    = this;
            this.generation = 0;
        }
        else {
            this.founder    = parent.founder;
            this.generation = parent.generation + 1;
        }
    }

    /**
     * Advances this propagator through one discrete time step.
     *
     * <p>After calling this method, the replication state (identified
     * by the {@code getState()} method) may be changed.
     *
     * <p>Subclasses are encouraged to change the return type to the
     * most concrete type possible.
     *
     * @param tumorEnv the tumor environment where this propagator
     * resides during the time step.
     *
     * @return any new propagators created during the time step.
     */
    public abstract Collection<? extends Propagator> advance(TumorEnv tumorEnv);

    /**
     * Returns the current replication state of this propagator.
     *
     * @return the current replication state of this propagator.
     */
    public abstract State getState();

    /**
     * Returns the generation index for this propagator, with index
     * {@code 0} denoting the founding propagator.
     *
     * @return the generation index for this propagator.
     */
    public final int getGeneration() {
        return generation;
    }

    /**
     * Returns the founder of this propagator.
     *
     * <p>Subclasses may override this method and cast the return
     * value to the appropriate class.
     *
     * @return the founder of this propagator.
     */
    public Propagator getFounder() {
        return founder;
    }

    /**
     * Returns the parent of this propagator, or {@code null} if this was
     * a founding propagator.
     *
     * <p>Subclasses may override this method and cast the return
     * value to the appropriate class.
     *
     * @return the parent of this propagator, or {@code null} if this was
     * a founding propagator.
     */
    public Propagator getParent() {
        return parent;
    }

    /**
     * Identifies founding propagators.
     *
     * @return {@code true} iff this is a founding propagator.
     */
    public final boolean isFounder() {
        return founder == this;
    }

    /**
     * Identifies propagators that are still alive.
     *
     * @return {@code true} iff this propagator is still alive.
     */
    public final boolean isAlive() {
        return getState().equals(State.ALIVE);
    }

    /**
     * Identifies propagators that have entered a dormant state.
     *
     * @return {@code true} iff this propagator is in a dormant state.
     */
    public final boolean isDormant() {
        return getState().equals(State.DORMANT);
    }

    /**
     * Identifies propagators that have died.
     *
     * @return {@code true} iff this propagator has died.
     */
    public final boolean isDead() {
        return getState().equals(State.DEAD);
    }

    /**
     * Traces the lineage of this propagator.
     *
     * @return a list containing all parents of this propagator (and
     * this propagator itself), ordered by generation starting with
     * the founder.
     */
    public List<? extends Propagator> traceLineage() {
        return traceLineage(0);
    }

    /**
     * Traces the lineage of this propagator back to a specific
     * generation.
     *
     * @param firstGeneration the earliest generation to include in
     * the lineage.
     *
     * @return a list containing all parents of this propagator (and
     * this propagator itself), ordered by generation starting with
     * the propagator at the given generation.
     *
     * @throws IllegalArgumentException if the first generation is
     * negative.
     */
    public List<? extends Propagator> traceLineage(int firstGeneration) {
        if (firstGeneration < 0)
            throw new IllegalArgumentException("First generation must be non-negative.");

        Propagator propagator = this;
        LinkedList<Propagator> lineage = new LinkedList<Propagator>();

        while (propagator != null && propagator.getGeneration() >= firstGeneration) {
            //
            // Push the current cycle onto the front of the
            // list...
            //
            lineage.addFirst(propagator);
            propagator = propagator.getParent();
        }

        // Return an ArrayList which will provide better performance
        // in most situations...
        return new ArrayList<Propagator>(lineage);
    }
}
