
package jam.stoch;

/**
 * Represents a process that may occur in a stochastic simulation.
 */
public interface StochProc {
    /**
     * Returns the instantaneous rate of this process.
     *
     * @return the instantaneous rate of this process.
     */
    public abstract StochRate getStochRate();
}
