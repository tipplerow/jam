
package jam.stoch;

import java.util.Collection;

import jam.math.JamRandom;

/**
 * Provides a base class for stochastic simulation algorithms.
 */
public abstract class StochAlgo<P extends StochProc> {
    /**
     * The random number source.
     */
    protected final JamRandom random;

    /**
     * The stochastic system being simulated.
     */
    protected final StochSystem<P> system;

    // The number of events that have occurred...
    private long eventCount = 0L;

    // The most recent event to occur...
    private StochEvent<P> event = null;

    /**
     * Creates a new stochastic simulation algorithm.
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     */
    protected StochAlgo(JamRandom random, StochSystem<P> system) {
        this.random = random;
        this.system = system;

        system.validateIndexing();
    }

    /**
     * Selects the next event to occur in the simulation.
     *
     * @return the next event in the simulation.
     */
    protected abstract StochEvent<P> nextEvent();

    /**
     * Updates the internal state of this algorithm after an event
     * has occurred.
     *
     * @param changed the stochastic processes whose rates have
     * changed as a result of the most recent event (the event
     * returned by the method {@code getEvent()}.
     */
    protected abstract void updateState(Collection<P> changed);

    /**
     * Advances the simulation by selecting the next stochastic event
     * and updating the instantaneous process rates in the underlying
     * stochastic system.
     */
    public void advance() {
        ++eventCount;
        event = nextEvent();

        Collection<P> changed = system.processEvent(event);
        updateState(changed);
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
     * Returns the most recent event that has occured.
     *
     * @return the most recent event that has occured ({@code null}
     * before any events have occurred).
     */
    public StochEvent<P> getEvent() {
        return event;
    }

    /**
     * Returns the most recent process to occur.
     *
     * @return the most recent process to occur ({@code null} before
     * any events have occurred).
     */
    public P getEventProcess() {
        if (event != null)
            return event.getProcess();
        else
            return null;
    }

    /**
     * Returns the (absolute) time when the most recent event occurred.
     *
     * @return the (absolute) time when the most recent event occurred.
     */
    public StochTime getEventTime() {
        if (event != null)
            return event.getTime();
        else
            return StochTime.ZERO;
    }
}
