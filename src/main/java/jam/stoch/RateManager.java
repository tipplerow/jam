
package jam.stoch;

import java.util.Collection;
import java.util.List;

import jam.vector.JamVector;

/**
 * Efficiently maintains the total instantaneous transition rate for a
 * system of stochastic processes.
 */
public final class RateManager<P extends StochProc> {
    private final JamVector rates;
    private final StochSystem<P> system;

    private final int ageThreshold;
    private final int procThreshold;

    private int rateAge;
    private double totalRate;

    private static final int MAX_AGE_THRESHOLD = 1000000;

    private RateManager(StochSystem<P> system) {
        system.validateIndexing();

        this.rates = new JamVector(system.countProcesses());
        this.system = system;

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

    private boolean allowPartialUpdate(Collection<P> procs) {
        return rateAge < ageThreshold && procs.size() < procThreshold;
    }

    private void updateFull() {
        rateAge = 0;
        totalRate = 0.0;

        for (int index = 0; index < system.countProcesses(); ++index) {
            double rate = system.getStochRate(index).doubleValue();

            totalRate += rate;
            rates.set(index, rate);
        }
    }

    private void updatePartial(Collection<P> procs) {
        ++rateAge;

        for (P proc : procs) {
            int index = proc.getIndex();

            double oldRate = rates.get(index);
            double newRate = system.getStochRate(index).doubleValue();

            rates.set(index, newRate);
            totalRate += (newRate - oldRate);
        }
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
     * Updates the total instantaneous transition rate following an
     * event that results in changes to the transition rates of some
     * stochastic processes.
     *
     * @param procs the stochastic processes whose rates were changed
     * as a result of the most recent event.
     */
    public void updateTotalRate(Collection<P> procs) {
        if (allowPartialUpdate(procs))
            updatePartial(procs);
        else
            updateFull();
   }
}
