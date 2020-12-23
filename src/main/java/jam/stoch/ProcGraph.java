
package jam.stoch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Represents a directed dependency graph for a system of coupled
 * stochastic processes.
 */
public final class ProcGraph<P extends StochProc> {
    //
    // The forward dependency relationships: Calling
    // forward.get(proc) returns all processes whose
    // rates depend on "proc"...
    //
    private final SetMultimap<P, P> forward = HashMultimap.create();

    // The reverse dependency relationships: Calling
    // reverse.get(proc) returns all processes that
    // determine the rate of "proc"...
    //
    private final SetMultimap<P, P> reverse = HashMultimap.create();

    private ProcGraph() {
    }

    private ProcGraph(Collection<RateLink<P>> links) {
        for (RateLink<P> link : links)
            link(link.getPredecessor(), link.getSuccessor());
    }

    /**
     * Creates an empty dependency graph.
     *
     * @return an empty dependency graph.
     */
    public static <P extends StochProc> ProcGraph<P> create() {
        return new ProcGraph<P>();
    }

    /**
     * Creates and populates a dependency graph.
     *
     * @param links the links that define the process couplings.
     *
     * @return a new graph containing the specified dependencies.
     */
    public static <P extends StochProc> ProcGraph<P> create(Collection<RateLink<P>> links) {
        return new ProcGraph<P>(links);
    }

    /**
     * Adds a predecessor process and its direct successors to this
     * graph.
     *
     * @param predecessor the predecessor process to add.
     *
     * @param successors the direct successors of the predecessor
     * process.
     *
     * @throws RuntimeException unless the predecessor is separate and
     * distinct process from the all successors.
     */
    @SuppressWarnings("unchecked")
    public void add(P predecessor, P... successors) {
        add(predecessor, List.of(successors));
    }

    /**
     * Adds a predecessor process and its direct successors to this
     * graph.
     *
     * @param predecessor the predecessor process to add.
     *
     * @param successors the direct successors of the predecessor
     * process.
     *
     * @throws RuntimeException unless the predecessor is separate and
     * distinct process from the all successors.
     */
    public void add(P predecessor, Collection<P> successors) {
        for (P successor : successors)
            link(predecessor, successor);
    }

    /**
     * Returns all direct successor processes to a given predecessor
     * process.
     *
     * @param predecessor a predecessor process of interest.
     *
     * @return a read-only set containing all direct successors of the
     * specified process.
     */
    public Set<P> get(P predecessor) {
        return Collections.unmodifiableSet(forward.get(predecessor));
    }

    /**
     * Adds an edge to this directed dependency graph.  The rate of
     * the successor process may change when the predecessor process
     * occurs.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the direct successor (dependent) process.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public void link(P predecessor, P successor) {
        RateLink.validate(predecessor, successor);
        forward.put(predecessor, successor);
        reverse.put(successor, predecessor);
    }

    /**
     * Removes all edges containing a given process from this graph.
     *
     * @param process the process to remove.
     */
    public void remove(P process) {
        forward.removeAll(process);
        reverse.removeAll(process);
    }

    /**
     * Removes an edge from this directed dependency graph (if it exists).
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor (dependent) process.
     */
    public void remove(P predecessor, P successor) {
        forward.remove(predecessor, successor);
        reverse.remove(successor, predecessor);
    }
}
