
package jam.stoch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;

/**
 * Provides a base class for systems of coupled stochastic processes.
 */
public abstract class StochSystem<P extends StochProc> {
    private final ProcGraph<P> graph = ProcGraph.create();
    private final Map<Integer, P> procs = new HashMap<Integer, P>();

    // The number of events that have occurred...
    private long eventCount = 0L;

    // The most recent event to occur...
    private StochEvent<P> lastEvent = null;

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
    protected StochSystem(Collection<P> procs, Collection<RateLink<P>> links) {
        addProcesses(procs);
        addLinks(links);
    }

    /**
     * Updates the internal state of this stochastic system after an
     * event occurs.  The most recent event to occur may be accessed
     * by calling {@code lastEvent()}.
     */
    protected abstract void updateState();

    /**
     * Adds a stochastic process to this system.
     *
     * @param proc the stochastic process to add.
     *
     * @throws RuntimeException if this system already contains
     * another process with the same index.
     */
    protected void addProcess(P proc) {
        if (containsProcess(proc.getProcIndex()))
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
    public static RuntimeException invalidProcessException(StochProc proc) {
        return invalidProcessException(proc.getProcIndex());
    }

    /**
     * Identifies processes contained in this stochastic system.
     *
     * @param index the ordinal index of the process in question.
     *
     * @return {@code true} iff this system contains a process with
     * the specified index.
     */
    public boolean containsProcess(int index) {
        return procs.containsKey(index);
    }

    /**
     * Returns the number of events that have occurred.
     *
     * @return the number of events that have occurred.
     */
    public long countEvents() {
        return eventCount;
    }

    /**
     * Returns the number of stochastic processes in this system.
     *
     * @return the number of stochastic processes in this system.
     */
    public int countProcesses() {
        return procs.size();
    }

    /**
     * Accesses processes in this system by their ordinal index.
     *
     * @param index the ordinal index of the desired process.
     *
     * @return the process with the specified index.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public P getProcess(int index) {
        P process = procs.get(index);

        if (process != null)
            return process;
        else
            throw invalidProcessException(index);
    }

    /**
     * Accesses the instantaneous rates of the processes in this
     * system by their ordinal index.
     *
     * @param index the ordinal index of the desired process.
     *
     * @return the instantaneous rate of the process with the
     * specified index.
     *
     * @throws RuntimeException unless this system contains a process
     * with the specified index.
     */
    public StochRate getStochRate(int index) {
        return getProcess(index).getStochRate();
    }

    /**
     * Returns the most recent event to occur in this system.
     *
     * @return the most recent event to occur in this system
     * ({@code null} before any events have occurred).
     */
    public StochEvent<P> lastEvent() {
        return lastEvent;
    }

    /**
     * Returns the most recent process to occur.
     *
     * @return the most recent process to occur ({@code null} before
     * any events have occurred).
     */
    public P lastEventProcess() {
        if (lastEvent != null)
            return lastEvent.getProcess();
        else
            return null;
    }

    /**
     * Returns the (absolute) time when the most recent event occurred.
     *
     * @return the (absolute) time when the most recent event occurred.
     */
    public StochTime lastEventTime() {
        if (lastEvent != null)
            return lastEvent.getTime();
        else
            return StochTime.ZERO;
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

    /**
     * Updates the state of this stochastic system after an event
     * occurs.
     *
     * @param event the most recent event to occur in this system.
     */
    public void updateState(StochEvent<P> event) {
        ++eventCount;
        lastEvent = event;
        updateState();
    }

    /**
     * Returns a read-only view of the stochastic processes that
     * compose this system.
     *
     * @return a read-only view of the stochastic processes that
     * compose this system.
     */
    public Collection<P> viewProcesses() {
        return Collections.unmodifiableCollection(procs.values());
    }

    /**
     * Returns a read-only view of the processes whose rates may
     * change after another process occurs.
     *
     * @param proc a process that affects the rate of other
     * processes.
     *
     * @return a read-only view of the processes whose rates may
     * change after the specified process occurs.
     */
    public Set<P> viewDependents(P proc) {
        return graph.get(proc);
    }
}
