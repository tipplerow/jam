
package jam.stoch.agent;

import java.util.Collection;

import com.google.common.collect.Multiset;

import jam.lang.Ordinal;
import jam.lang.OrdinalIndex;
import jam.lang.JamException;
import jam.math.DoubleComparator;
import jam.stoch.StochProc;
import jam.stoch.StochRate;

/**
 * Represents an agent-based process that may occur in a stochastic
 * simulation.
 */
public abstract class AgentProc<A extends StochAgent> extends Ordinal implements StochProc {
    // The instantaneous rate of this process...
    private StochRate stochRate;

    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

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
        super(ordinalIndex.next());
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
     * @param state the current simulation state.
     *
     * @return the rate constant for this process given the current
     * simulation state.
     */
    public abstract double getRateConstant(AgentState<A> state);

    /**
     * Computes the rate of a first-order process from a rate constant and
     * agent population.
     *
     * @param rateConst the first-order rate constant for the process.
     *
     * @param agentCount the number of instances of the reactive agent
     * that are present.
     *
     * @return the rate of a first-order process with the specified rate
     * constant and agent population.
     */
    public static StochRate computeRate(double rateConst, int agentCount) {
        validateRateConstant(rateConst);
        return StochRate.valueOf(rateConst * agentCount);
    }

    /**
     * Computes the rate of a second-order process from a rate constant
     * and agent populations.
     *
     * @param rateConst the second-order rate constant for the process.
     *
     * @param agent1Count the number of instances of the first reactive
     * agent that are present.
     *
     * @return the rate of a second-order process with the specified rate
     * constant and agent populations.
     */
    public static StochRate computeRate(double rateConst, int agent1Count, int agent2Count) {
        validateRateConstant(rateConst);
        return StochRate.valueOf(rateConst * agent1Count * agent2Count);
    }

    /**
     * Computes the rate of a first-order process from a simulation
     * state, the rate constant, and the single reactive agent.
     *
     * @param state the current simulation state.
     *
     * @param rateConst the rate constant for the process.
     *
     * @param agent the reactive agent in the process.
     *
     * @return the rate of a first-order process given the specified
     * simulation state.
     */
    public static <A extends StochAgent> StochRate computeRate(AgentState<A> state, double rateConst, A agent) {
        return computeRate(rateConst, state.countAgent(agent));
    }

    /**
     * Computes the rate of a second-order process from a simulation
     * state, the rate constant, and the reactive agents.
     *
     * @param state the current simulation state.
     *
     * @param rateConst the rate constant for the process.
     *
     * @param agent1 the first reactive agent in the process.
     *
     * @param agent2 the second reactive agent in the process.
     *
     * @return the rate of a second-order process given the specified
     * simulation state.
     */
    public static <A extends StochAgent> StochRate computeRate(AgentState<A> state, double rateConst, A agent1, A agent2) {
        return computeRate(rateConst, state.countAgent(agent1), state.countAgent(agent2));
    }

    /**
     * Computes the effective rate constant for a capacity-limited
     * process.
     *
     * @param state the current simulation state.
     *
     * @param capped the stochastic agents that contribute to the
     * population limit.
     *
     * @param capacity the maximum population of the capped agents.
     *
     * @param baseRate the rate constant for the process when the
     * capacity constraint is not binding.
     *
     * @return the effective rate constant for the specified state.
     */
    public static <A extends StochAgent> double computeCappedRateConstant(AgentState<A> state,
                                                                          Collection<A> capped,
                                                                          int           capacity,
                                                                          double        baseRate) {
        if (state.countAgents(capped) < capacity)
            return baseRate;
        else
            return 0.0;
    }

    /**
     * Ensures that the capacity of a population-limited process is
     * positive.
     *
     * @param capacity the capacity to validate.
     *
     * @throws RuntimeException unless the capacity is positive.
     */
    public static void validateCapacity(int capacity) {
        if (capacity < 1)
            throw JamException.runtime("Capacity must be positive.");
    }

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
     * @param state the current simulation state.
     *
     * @return the instantaneous rate of this process for the given
     * simulation state.
     */
    public StochRate computeRate(AgentState<A> state) {
        double rate = getRateConstant(state);
        validateRateConstant(rate);

        for (A reactant : getReactants())
            rate *= state.countAgent(reactant);

        return StochRate.valueOf(rate);
    }

    /**
     * Updates the population of stochastic agents after this process
     * occurs.
     *
     * @param population the population of stochastic agents prior to
     * the occurrence of this process.
     */
    public void updatePopulation(AgentPopulation<A> population) {
        for (A reactant : getReactants())
            population.remove(reactant);

        for (A product : getProducts())
            population.add(product);
    }

    /**
     * Updates the instantaneous rate of this process after an event
     * occurs.
     *
     * @param state the simulation state following the occurrence of
     * the last event.
     */
    public void updateRate(AgentState<A> state) {
        stochRate = computeRate(state);
    }

    @Override public int getProcIndex() {
        return (int) getIndex();
    }

    @Override public StochRate getStochRate() {
        if (stochRate != null)
            return stochRate;
        else
            throw new IllegalStateException("The process rate has not been assigned.");
    }
}
