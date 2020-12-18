
package jam.stoch.agent;

import java.util.List;

import jam.stoch.StochRate;

/**
 * Represents the stochastic process of cellular division with the
 * possibility of mutation.  With the parent agent {@code P} and the
 * child agent {@code C}, the birth process occurs at a rate equal to
 * {@code k * nP}, where {@code k} is the instantaneous rate constant
 * and {@code nP} is the number of instances of {@code P}.  After the
 * birth process occurs, the number of child instances increases by
 * one: {@code nC => nC + 1}.
 *
 * <p>Notes: (1) For cellular division without mutation, the parent
 * and child agents are identical. (2) The rate constant may change
 * through time and/or be context-dependent.
 */
public abstract class BirthProc<A extends StochAgent> extends AgentProc<A> {
    private final A parent;
    private final A child;

    /**
     * Creates a new birth process with fixed parent and child agents.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     */
    protected BirthProc(A parent, A child) {
        super();

        this.child = child;
        this.parent = parent;
    }

    /**
     * Creates a new birth process with fixed parent and child agents.
     *
     * @param rate the initial rate of the process.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     */
    protected BirthProc(StochRate rate, A parent, A child) {
        super(rate);

        this.child = child;
        this.parent = parent;
    }

    /**
     * Computes the rate of a birth process from a rate constant and
     * parent population.
     *
     * @param rateConst the first-order rate constant for the process.
     *
     * @param parentCount the number of instances of the parent agent
     * that are present.
     *
     * @return the rate of a birth process with the specified rate
     * constant and parent population.
     */
    public static StochRate computeRate(double rateConst, int parentCount) {
        validateRateConstant(rateConst);
        return StochRate.valueOf(rateConst * parentCount);
    }

    /**
     * Computes the rate of a birth process from a rate constant, a
     * parent agent, and a simulation state.
     *
     * @param rateConst the rate constant for the process.
     *
     * @param parent the parent agent in the process.
     *
     * @param state the current simulation state.
     *
     * @return the rate of a birth process with the specified rate
     * constant and parent agent considering the current simulation
     * state.
     */
    public static <A extends StochAgent> StochRate computeRate(double rateConst, A parent, AgentState<A> state) {
        return computeRate(rateConst, state.countAgent(parent));
    }

    /**
     * Returns the instantaneous rate constant for this process, which
     * may be time or context-dependent.
     *
     * @param state the current simulation state.
     *
     * @return the rate constant for this birth process in the current
     * simulation state.
     */
    public abstract double getRateConstant(AgentState<A> state);

    /**
     * Returns the parent agent for this birth process.
     *
     * @return the parent agent for this birth process.
     */
    public A getParent() {
        return parent;
    }

    /**
     * Returns the child agent for this birth process.
     *
     * @return the child agent for this birth process.
     */
    public A getChild() {
        return child;
    }

    @Override public List<A> affects() {
        return List.of(child);
    }

    @Override public List<A> depends() {
        return List.of(parent);
    }

    @Override public StochRate computeRate(AgentState<A> state) {
        return computeRate(getRateConstant(state), parent, state);
    }

    @Override public void updatePopulation(AgentPopulation<A> population) {
        population.add(child);
    }
}
