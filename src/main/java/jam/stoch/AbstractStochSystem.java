
package jam.stoch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;

/**
 * Provides a base class for coupled stochastic systems.
 */
public abstract class AbstractStochSystem<P extends StochProc> implements StochSystem<P> {
    private final ProcGraph<P> graph = ProcGraph.create();
    private final Map<Integer, P> procs = new HashMap<Integer, P>();

    /**
     * Creates a new coupled stochastic system.
     *
     * @param procs the stochastic process which compose the system.
     *
     * @param links the edges of the directed dependency graph for the
     * system.
     *
     * @throws RuntimeException if any rate links refer to processes
     * not contained in the input collection.
     */
    protected AbstractStochSystem(Collection<P> procs, Collection<RateLink<P>> links) {
        addProcesses(procs);
        addLinks(links);
    }

    /**
     * Adds a stochastic process to this system.
     *
     * @param proc the stochastic process to add.
     *
     * @throws RuntimeException if this system already contains
     * another process with the same index.
     */
    protected void addProcess(P proc) {
        if (containsProcess(proc))
            throw JamException.runtime("Duplicate process index: [%d].", proc.getProcIndex());

        procs.put(proc.getProcIndex(), proc);
    }

    /**
     * Adds stochastic process to this system.
     *
     * @param procs the stochastic processes to add.
     *
     * @throws RuntimeException if this system already contains any
     * processes with the same indexes.
     */
    protected void addProcesses(Collection<P> procs) {
        for (P proc : procs)
            addProcess(proc);
    }

    /**
     * Adds a rate link between two processes in this system.
     *
     * @param link the rate link to add.
     *
     * @throws RuntimeException unless this system contains both
     * processes in the link.
     */
    protected void addLink(RateLink<P> link) {
        addLink(link.getPredecessor(), link.getSuccessor());
    }

    /**
     * Adds a rate link between two processes in this system.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the direct successor process.
     *
     * @throws RuntimeException unless this system contains both
     * processes in the link.
     */
    protected void addLink(P predecessor, P successor) {
        requireProcess(predecessor);
        requireProcess(successor);

        graph.link(predecessor, successor);
    }

    /**
     * Adds rate link between processes in this system.
     *
     * @param links the rate links to add.
     *
     * @throws RuntimeException unless this system contains every
     * process in the links.
     */
    protected void addLinks(Collection<RateLink<P>> links) {
        for (RateLink<P> link : links)
            addLink(link);
    }

    /**
     * Removes a stochastic process from this system.
     *
     * @param index the unique ordinal index of the process to
     * removee.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    protected void removeProcess(int index) {
        requireProcess(index);

        procs.remove(index);
        graph.remove(getProcess(index));
    }

    /**
     * Removes a stochastic process from this system.
     *
     * @param proc the process to removee.
     *
     * @throws RuntimeException unless this system contains the
     * specified process.
     */
    protected void removeProcess(P proc) {
        removeProcess(proc.getProcIndex());
    }

    /**
     * Returns a runtime exception for an invalid process index.
     *
     * @param index the invalid process index.
     *
     * @return a runtime exception for the specified process index.
     */
    public static RuntimeException invalidProcessException(int index) {
        return JamException.runtime("Invalid process index: [%d].", index);
    }

    /**
     * Returns a runtime exception for an invalid process.
     *
     * @param proc the invalid process.
     *
     * @return a runtime exception for the specified process.
     */
    public RuntimeException invalidProcessException(P proc) {
        return invalidProcessException(proc.getProcIndex());
    }


    /**
     * Requires that this system contains a specific process.
     *
     * @param index the unique ordinal index of the required process.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public void requireProcess(int index) {
        if (!containsProcess(index))
            throw invalidProcessException(index);
    }

    /**
     * Requires that this system contains a specific process.
     *
     * @param proc the required process.
     *
     * @throws RuntimeException unless this system contains the
     * specified process.
     */
    public void requireProcess(P proc) {
        requireProcess(proc.getProcIndex());
    }

    @Override public boolean containsProcess(int index) {
        return procs.containsKey(index);
    }

    @Override public int countProcesses() {
        return procs.size();
    }

    @Override public P getProcess(int index) {
        P process = procs.get(index);

        if (process != null)
            return process;
        else
            throw invalidProcessException(index);
    }

    @Override public Collection<P> viewProcesses() {
        return Collections.unmodifiableCollection(procs.values());
    }

    @Override public Set<P> viewDependents(P proc) {
        return graph.get(proc);
    }
}
