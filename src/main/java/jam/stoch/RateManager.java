
package jam.stoch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Efficiently maintains the total instantaneous transition rate for a
 * <em>fixed</em> system of stochastic processes. The behavior of this
 * class is undefined if any stochastic processes are added or removed
 * from the system.
 */
public final class RateManager<P extends StochProc> {
    private final StochSystem<P> system;
    private final Map<P, StochRate> rateMap;

    private final int ageThreshold;
    private final int procThreshold;

    private int rateAge;
    private double totalRate;

    private static final int MAX_AGE_THRESHOLD = 1000000;

    private RateManager(StochSystem<P> system) {
        this.system = system;
        this.rateMap = new HashMap<P, StochRate>();

        this.ageThreshold = computeAgeThreshold(system);
        this.procThreshold = computeProcThreshold(system);

        updateFull();
    }

    private static int computeAgeThreshold(StochSystem<?> system) {
        //
        // Explicitly recompute the total reaction rate if the number
        // of partial updates exceeds the lesser of MAX_AGE_THRESHOLD
        // or 100 times the number of processes...
        //
        return Math.min(MAX_AGE_THRESHOLD, 100 * system.countProcesses());
    }

    private static int computeProcThreshold(StochSystem<?> system) {
        //
        // Explicitly recompute the total reaction rate if half or
        // more of the processes have new rates...
        //
        return system.countProcesses() / 2;
    }

    private boolean allowPartialUpdate(Collection<P> successors) {
        return rateAge < ageThreshold && successors.size() < procThreshold;
    }

    private void updateFull() {
        rateAge = 0;
        totalRate = 0.0;

        for (P proc : system.viewProcesses()) {
            StochRate rate = proc.getStochRate();

            rateMap.put(proc, rate);
            totalRate += rate.doubleValue();
        }
    }

    private void updatePartial(P eventProc, Collection<P> successors) {
        ++rateAge;
        updateProc(eventProc);

        for (P successor : successors)
            updateProc(successor);
    }

    private void updateProc(P proc) {
        StochRate oldRate = rateMap.get(proc);
        StochRate newRate = proc.getStochRate();

        rateMap.put(proc, newRate);
        totalRate += (newRate.doubleValue() - oldRate.doubleValue());
    }

    /**
     * Creates a new rate manager for a stochastic system.
     *
     * @param system the system to monitor.
     *
     * @return a new rate manager for the specified system.
     */
    public static <P extends StochProc> RateManager<P> create(StochSystem<P> system) {
        return new RateManager<P>(system);
    }

    /**
     * Returns the total instantaneous transition rate for the
     * stochastic system.
     *
     * @return the total instantaneous transition rate for the
     * stochastic system.
     */
    public StochRate getTotalRate() {
        return StochRate.valueOf(totalRate);
    }

    /**
     * Updates the total instantaneous transition rate after an event
     * occurs.
     *
     * @param eventProc the stochastic processes that occurred.
     */
    public void updateTotalRate(P eventProc) {
        Collection<P> successors = system.viewDependents(eventProc);

        if (allowPartialUpdate(successors))
            updatePartial(eventProc, successors);
        else
            updateFull();
   }
}
