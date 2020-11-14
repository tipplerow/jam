
package jam.stoch;

/**
 * Represents a discrete event in a stochastic simulation.
 */
public final class StochEvent<P extends StochProc> {
    private final P proc;
    private final StochTime time;

    private StochEvent(P proc, StochTime time) {
        this.proc = proc;
        this.time = time;
    }

    /**
     * Creates a new event.
     *
     * @param proc the stochastic process that occurred.
     *
     * @param time the time when the event occurred.
     */
    public static <P extends StochProc> StochEvent<P> create(P proc, StochTime time) {
        return new StochEvent<P>(proc, time);
    }

    /**
     * Returns the process that occurred.
     *
     * @return the process that occurred.
     */
    public P getProcess() {
        return proc;
    }

    /**
     * Returns the (absolute) time when the event occurred.
     *
     * @return the (absolute) time when the event occurred.
     */
    public StochTime getTime() {
        return time;
    }

    @Override public String toString() {
        return String.format("StochEvent(%s @ %f)", proc, time);
    }
}
