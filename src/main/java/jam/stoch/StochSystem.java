
package jam.stoch;

import java.util.Collection;

/**
 * Represents a system of (possibly coupled) stochastic processes.
 */
public interface StochSystem<P extends StochProc> {
    /**
     * Updates the state of this stochastic system after an event
     * occurs and returns the processes whose rates have changed.
     *
     * @param event the most recent event to occur in this system.
     *
     * @return the processes whose rates have changed as a result
     * of the specified event.
     */
    public abstract Collection<P> update(StochEvent<P> event);

    /**
     * Returns a read-only view of the stochastic processes that
     * compose this system.
     *
     * @return a read-only view of the stochastic processes that
     * compose this system.
     */
    public abstract Collection<P> viewProcesses();

    /**
     * Returns the number of stochastic processes in this system.
     *
     * @return the number of stochastic processes in this system.
     */
    public default int processCount() {
        return viewProcesses().size();
    }

    /**
     * Returns the sum of the instantaneous rates of the stochastic
     * processes in this system.
     *
     * @return the sum of the instantaneous rates of the stochastic
     * processes in this system.
     */
    public default StochRate totalRate() {
        double total = 0.0;

        for (StochProc proc : viewProcesses())
            total += proc.getStochRate().doubleValue();

        return StochRate.valueOf(total);
    }
}
