
package jam.stoch.agent;

import java.util.Collection;

import jam.lang.JamException;

/**
 * Represents a birth process with a fixed rate constant but a limit
 * on the total population of a subset of agents.  The capped process
 * acts like a fixed-rate birth process when the total population of
 * the capped agents is below the capacity threshold, but the birth
 * rate falls to zero at and above the threshold.
 */
public final class CappedBirthProc<A extends StochAgent> extends BirthProc<A> {
    private final int capacity;
    private final double baseRate;
    private final Collection<A> capped;

    private CappedBirthProc(A parent,
                            A child,
                            int capacity,
                            double baseRate,
                            Collection<A> capped) {
        super(parent, child);

        validateCapacity(capacity);
        validateRateConstant(baseRate);

        this.capped = capped;
        this.baseRate = baseRate;
        this.capacity = capacity;
    }

    /**
     * Creates a new birth process with a fixed rate constant and a
     * limit on the total population of a subset of agents.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     *
     * @param capacity the maximum population of the capped agents.
     *
     * @param baseRate the first-order rate constant for the process
     * when the capacity constraint is not binding.
     *
     * @param capped the stochastic agents that contribute to the
     * population limit.
     *
     * @return a new birth process with the specified parameters.
     */
    public static <A extends StochAgent> BirthProc<A> create(A parent,
                                                             A child,
                                                             int capacity,
                                                             double baseRate,
                                                             Collection<A> capped) {
        return new CappedBirthProc<A>(parent, child, capacity, baseRate, capped);
    }

    /**
     * Computes the effective rate constant for a given simulation
     * state.
     *
     * @param capacity the maximum population of the capped agents.
     *
     * @param baseRate the first-order rate constant for the process
     * when the capacity constraint is not binding.
     *
     * @param capped the stochastic agents that contribute to the
     * population limit.
     *
     * @param state the current simulation state.
     *
     * @return the effective rate constant for the specified state.
     */
    public static <A extends StochAgent> double computeRateConstant(int capacity,
                                                                    double baseRate,
                                                                    Collection<A> capped,
                                                                    AgentState<A> state) {
        if (state.countAgents(capped) < capacity)
            return baseRate;
        else
            return 0.0;
    }

    /**
     * Ensures that an agent capacity is positive.
     *
     * @param capacity the capacity to validate.
     *
     * @throws RuntimeException unless the capacity is positive.
     */
    public static void validateCapacity(int capacity) {
        if (capacity < 1)
            throw JamException.runtime("Capacity must be positive.");
    }

    @Override public double getRateConstant(AgentState<A> state) {
        return computeRateConstant(capacity, baseRate, capped, state);
    }
}
