
package jam.stoch;

import java.util.Comparator;
import java.util.List;

import jam.lang.JamException;

/**
 * Represents a process that may occur in a stochastic simulation.
 */
public interface StochProc {
    /**
     * A comparator that orders processes by their instantaneous rates
     * in ascending order (slowest process first).
     */
    public static final Comparator<StochProc> ASCENDING_RATE_COMPARATOR =
        new Comparator<StochProc>() {
            @Override public int compare(StochProc proc1, StochProc proc2) {
                return StochRate.ASCENDING_COMPARATOR.compare(proc1.getStochRate(),
                                                              proc2.getStochRate());
            }
        };

    /**
     * A comparator that orders processes by their instantaneous rates
     * in descending order (fastest process first).
     */
    public static final Comparator<StochProc> DESCENDING_RATE_COMPARATOR =
        new Comparator<StochProc>() {
            @Override public int compare(StochProc proc1, StochProc proc2) {
                return StochRate.DESCENDING_COMPARATOR.compare(proc1.getStochRate(),
                                                               proc2.getStochRate());
            }
        };

    /**
     * Ensures that a list of stochastic processes is arranged such that
     * {@code procs.get(k).getProcIndex() == k} for all elements in the
     * list.
     *
     * @param procs a list of processes to validate.
     *
     * @throws RuntimeException unless {@code procs.get(k).getProcIndex() == k}
     * for every element in the list.
     */
    public static void validateList(List<? extends StochProc> procs) {
        for (int index = 0; index < procs.size(); ++index)
            if (procs.get(index).getProcIndex() != index)
                throw JamException.runtime("Invalid index for process [%d].", index);
    }

    /**
     * Returns a unique ordinal index for this process.
     *
     * @return a unique ordinal index for this process.
     */
    public abstract int getProcIndex();

    /**
     * Returns the instantaneous rate of this process.
     *
     * @return the instantaneous rate of this process.
     */
    public abstract StochRate getStochRate();
}
