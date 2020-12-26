
package jam.stoch.agent;

import com.google.common.collect.Multiset;

import jam.math.DoubleComparator;
import jam.stoch.StochProc;
import jam.stoch.StochRate;

/**
 * Represents an agent-based process that may occur in a stochastic
 * simulation.
 */
public abstract class AgentProc<A extends StochAgent> extends StochProc {
    // The instantaneous rate of this process, updated as the
    // underlying stochastic system evolves...
    private StochRate stochRate;

    /**
     * Creates a new agent-based process with an unknown initial rate.
     * The rate must be assigned by calling {@code updateRate()} prior
     * to the first step in the stochastic simulation.
     */
    protected AgentProc() {
        this(null);
    }

    /**
     * Creates a new agent-based process with a known initial rate.
     *
     * @param stochRate the initial rate of the process.
     */
    protected AgentProc(StochRate stochRate) {
        super();
        this.stochRate = stochRate;
    }

    /**
     * Returns the reactive agents in this process, which are consumed
     * when this process occurs.
     *
     * @return the reactive agents in this process.
     */
    public abstract Multiset<A> getReactants();

    /**
     * Returns the agents that are produced when this process occurs.
     *
     * @return the agents that are produced when this process occurs.
     */
    public abstract Multiset<A> getProducts();

    /**
     * Returns the instantaneous rate constant for this process, which
     * may depend on the simulation time or context.
     *
     * @param system the stochastic system that contains this process.
     *
     * @return the rate constant for this process given the current
     * state of the stochastic system.
     */
    public abstract double getRateConstant(AgentSystem<A, ?> system);

    /**
     * Validates a rate constant.
     *
     * @param rateConst the rate constant for a stochastic process.
     *
     * @throws IllegalArgumentException if the rate constant is negative.
     */
    public static void validateRateConstant(double rateConst) {
        if (DoubleComparator.DEFAULT.isNegative(rateConst))
            throw new IllegalArgumentException("Negative rate constant.");
    }

    /**
     * Computes the instantaneous rate of this process, which may
     * depend on the simulation time or context.
     *
     * @param system the stochastic system that contains this process.
     *
     * @return the instantaneous rate of this process in the current
     * state of the stochastic system.
     */
    public StochRate computeRate(AgentSystem<A, ?> system) {
        double rate = getRateConstant(system);
        validateRateConstant(rate);

        for (A reactant : getReactants())
            rate *= system.countAgent(reactant);

        return StochRate.valueOf(rate);
    }

    /**
     * Updates the instantaneous rate of this process after an event
     * occurs.
     *
     * @param system the stochastic system that contains this process.
     */
    public void updateRate(AgentSystem<A, ?> system) {
        stochRate = computeRate(system);
    }

    @Override public StochRate getStochRate() {
        if (stochRate != null)
            return stochRate;
        else
            throw new IllegalStateException("The process rate has not been assigned.");
    }
}
