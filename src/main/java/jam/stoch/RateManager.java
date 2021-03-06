
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
public final class RateManager {
    private final StochSystem system;
    private final Map<StochProc, StochRate> rateMap;

    private final int ageThreshold;
    private final int procThreshold;

    private int rateAge;
    private double totalRate;

    private static final int MAX_AGE_THRESHOLD = 1000000;

    private RateManager(StochSystem system) {
        this.system = system;
        this.rateMap = new HashMap<StochProc, StochRate>();

        this.ageThreshold = computeAgeThreshold(system);
        this.procThreshold = computeProcThreshold(system);

        updateFull();
    }

    private static int computeAgeThreshold(StochSystem system) {
        //
        // Explicitly recompute the total reaction rate if the number
        // of partial updates exceeds the lesser of MAX_AGE_THRESHOLD
        // or 100 times the number of processes...
        //
        return Math.min(MAX_AGE_THRESHOLD, 100 * system.countProcesses());
    }

    private static int computeProcThreshold(StochSystem system) {
        //
        // Explicitly recompute the total reaction rate if half or
        // more of the processes have new rates...
        //
        return system.countProcesses() / 2;
    }

    private boolean allowPartialUpdate(Collection<? extends StochProc> dependents) {
        return rateAge < ageThreshold && dependents.size() < procThreshold;
    }

    private void updateFull() {
        rateAge = 0;
        totalRate = 0.0;

        for (StochProc proc : system.viewProcesses()) {
            StochRate rate = proc.getStochRate();

            rateMap.put(proc, rate);
            totalRate += rate.doubleValue();
        }
    }

    private void updatePartial(StochProc eventProc, Collection<? extends StochProc> dependents) {
        ++rateAge;
        updateProc(eventProc);

        for (StochProc dependent : dependents)
            updateProc(dependent);
    }

    private void updateProc(StochProc proc) {
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
    public static RateManager create(StochSystem system) {
        return new RateManager(system);
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
     *
     * @param dependents the stochastic processes whose rates have
     * changed as a result of the latest event (excluding the process
     * that occurred).
     */
    public void updateTotalRate(StochProc eventProc, Collection<? extends StochProc> dependents) {
        if (allowPartialUpdate(dependents))
            updatePartial(eventProc, dependents);
        else
            updateFull();
   }
}
