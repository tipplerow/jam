
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

    @Override public double getRateConstant(AgentState<A> state) {
        return computeCappedRateConstant(state, capped, capacity, baseRate);
    }
}
