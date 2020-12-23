
package jam.stoch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jam.lang.JamException;

/**
 * Represents a system of (possibly coupled) stochastic processes.
 */
public interface StochSystem<P extends StochProc> {
    /**
     * Identifies processes contained in this stochastic system.
     *
     * @param index the ordinal index of the process in question.
     *
     * @return {@code true} iff this system contains a process with
     * the specified index.
     */
    public abstract boolean containsProcess(int index);

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
    public abstract P getProcess(int index);

    /**
     * Returns a read-only view of the stochastic processes that
     * compose this system.
     *
     * @return a read-only view of the stochastic processes that
     * compose this system.
     */
    public abstract Collection<P> viewProcesses();

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
    public abstract Collection<P> viewDependents(P proc);

    /**
     * Updates the state of this stochastic system after an event
     * occurs.
     *
     * @param event the most recent event to occur in this system.
     */
    public abstract void updateState(StochEvent<P> event);

    /**
     * Computes the sum of the instantaneous rates of the stochastic
     * processes in this system.
     *
     * @return the sum of the instantaneous rates of the stochastic
     * processes in this system.
     */
    public default StochRate computeTotalRate() {
        double total = 0.0;

        for (StochProc proc : viewProcesses())
            total += proc.getStochRate().doubleValue();

        return StochRate.valueOf(total);
    }

    /**
     * Identifies processes contained in this stochastic system.
     *
     * @param proc the process in question.
     *
     * @return {@code true} iff this system contains the specified
     * process.
     */
    public default boolean containsProcess(P proc) {
        return containsProcess(proc.getProcIndex());
    }

    /**
     * Returns the number of stochastic processes in this system.
     *
     * @return the number of stochastic processes in this system.
     */
    public default int countProcesses() {
        return viewProcesses().size();
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
    public default StochRate getStochRate(int index) {
        return getProcess(index).getStochRate();
    }

    /**
     * Returns a map containing the instantaneous rate of each
     * stochastic process in this system.
     *
     * @return a map containing the instantaneous rate of each
     * stochastic process in this system.
     */
    public default Map<P, StochRate> mapRates() {
        Map<P, StochRate> rateMap = new HashMap<P, StochRate>(countProcesses());

        for (P proc : viewProcesses())
            rateMap.put(proc, proc.getStochRate());

        return rateMap;
    }
}
