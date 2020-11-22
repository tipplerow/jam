
package jam.stoch;

import jam.math.JamRandom;

/**
 * Represents a discrete event in a stochastic simulation.
 */
public final class StochEvent<P extends StochProc> implements Comparable<StochEvent<P>> {
    private final P proc;
    private final StochTime time;

    private StochEvent(P proc, StochTime time) {
        this.proc = proc;
        this.time = time;
    }

    /**
     * Creates a new event.
     *
     * @param proc the stochastic process that has occurred or will
     * occur.
     *
     * @param time the (absolute) time when the event last occurred or
     * will occur next.
     */
    public static <P extends StochProc> StochEvent<P> create(P proc, StochTime time) {
        return new StochEvent<P>(proc, time);
    }

    /**
     * Creates the next event for a stochastic process by sampling
     * from an exponential probability distribution with a mean time
     * equal to the inverse of the instantaneous rate of the process.
     *
     * @param proc the stochastic process that will occur.
     *
     * @param time the (absolute) time when the most recent event
     * occurred (considering <em>all</em> events in the stochastic
     * system).
     *
     * @param random a random number source.
     *
     * @return the next event for the stochastic process for the
     * <em>next reaction method</em> simulation algorithm.
     */
    public static <P extends StochProc> StochEvent<P> next(P proc, StochTime time, JamRandom random) {
        return create(proc, proc.getStochRate().sampleTime(time, random));
    }

    /**
     * Returns the ordinal index of the process that has occurred or
     * will occur.
     *
     * @return the ordinal index of the process that has occurred or
     * will occur.
     */
    public int getIndex() {
        return proc.getIndex();
    }

    /**
     * Returns the process that has occurred or will occur.
     *
     * @return the process that has occurred or will occur.
     */
    public P getProcess() {
        return proc;
    }

    /**
     * Returns the (absolute) time when the event last occurred or
     * will occur next.
     *
     * @return the (absolute) time when the event last occurred or
     * will occur next.
     */
    public StochTime getTime() {
        return time;
    }

    /**
     * Defines the natural ordering of events as their chronological
     * order, with ties broken by the index of the process (with the
     * lower index occuring first).
     *
     * @param that an event to compare with this event.
     *
     * @return an integer less than, equal to, or greater than zero
     * according whether this event occurs before, at the same time,
     * or later than the input event.
     */
    @Override public int compareTo(StochEvent<P> that) {
        int timeCmp = this.time.compareTo(that.time);

        if (timeCmp != 0)
            return timeCmp;
        else
            return Integer.compare(this.proc.getIndex(), that.proc.getIndex());
    }

    @Override public String toString() {
        return String.format("StochEvent(%s @ %f)", proc, time.doubleValue());
    }
}
