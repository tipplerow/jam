
package jam.stoch;

import java.util.Collection;
import java.util.List;

import jam.lang.JamException;

/**
 * Represents a system of (possibly coupled) stochastic processes.
 */
public interface StochSystem<P extends StochProc> {
    /**
     * Returns a read-only list of the stochastic processes that
     * compose this system.
     *
     * @return a read-only list of the stochastic processes that
     * compose this system.
     */
    public abstract List<P> listProcesses();

    /**
     * Updates the state of this stochastic system after an event
     * occurs and returns the processes whose rates have changed.
     *
     * @param event the most recent event to occur in this system.
     *
     * @return the processes whose rates have changed as a result
     * of the specified event.
     */
    public abstract Collection<P> processEvent(StochEvent<P> event);

    /**
     * Ensures that a list of stochastic processes are properly
     * indexed.
     *
     * @param processList a list of processes to validate.
     *
     * @throws RuntimeException unless {@code processList.get(k).getIndex() == k}
     * for all indexes {@code k = 0, 1, ..., processList.size()}.
     */
    public static <P extends StochProc> void validateIndexing(List<P> processList) {
        for (int index = 0; index < processList.size(); ++index)
            if (processList.get(index).getIndex() != index)
                throw JamException.runtime("Invalid index for process [%d].", index);
    }

    /**
     * Computes the sum of the instantaneous rates of the stochastic
     * processes in this system.
     *
     * @return the sum of the instantaneous rates of the stochastic
     * processes in this system.
     */
    public default StochRate computeTotalRate() {
        double total = 0.0;

        for (StochProc proc : listProcesses())
            total += proc.getStochRate().doubleValue();

        return StochRate.valueOf(total);
    }

    /**
     * Returns the number of stochastic processes in this system.
     *
     * @return the number of stochastic processes in this system.
     */
    public default int countProcesses() {
        return listProcesses().size();
    }

    /**
     * Accesses processes in this system by their ordinal index.
     *
     * @param index the ordinal index of the desired process.
     *
     * @return the process with the specified index.
     *
     * @throws IndexOutOfBoundsException unless the index is valid:
     * {@code 0 <= index && index < countProcesses()}.
     */
    public default P getProcess(int index) {
        return listProcesses().get(index);
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
     * @throws IndexOutOfBoundsException unless the index is valid:
     * {@code 0 <= index && index < countProcesses()}.
     */
    public default StochRate getStochRate(int index) {
        return getProcess(index).getStochRate();
    }

    /**
     * Ensures that the stochastic processes are properly indexed.
     *
     * @throws RuntimeException unless {@code getProcess(k).getIndex() == k}
     * for all indexes {@code k = 0, 1, ..., countProcesses()}.
     */
    public default void validateIndexing() {
        validateIndexing(listProcesses());
    }
}
