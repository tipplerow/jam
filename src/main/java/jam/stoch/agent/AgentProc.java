
package jam.stoch.agent;

import java.util.Collection;

import jam.lang.Ordinal;
import jam.lang.OrdinalIndex;
import jam.math.DoubleComparator;
import jam.stoch.StochProc;
import jam.stoch.StochRate;

/**
 * Represents an agent-based process that may occur in a stochastic
 * simulation.
 */
public abstract class AgentProc<A extends StochAgent> extends Ordinal implements StochProc {
    /**
     * The instantaneous rate of this process.
     */
    protected StochRate rate;

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
     * @param rate the initial rate of the process.
     */
    protected AgentProc(StochRate rate) {
        super(ordinalIndex.next());
        this.rate = rate;
    }

    /**
     * Returns the stochastic agents whose population numbers change
     * when this process occurs.
     *
     * @return the stochastic agents whose population numbers change
     * when this process occurs.
     */
    public abstract Collection<A> affects();

    /**
     * Returns the stochastic agents on which the rate of this process
     * depends (their population numbers are required to compute the
     * rate of this process).
     *
     * @return the stochastic agents on which the rate of this process
     * depends.
     */
    public abstract Collection<A> depends();

    /**
     * Computes the instantaneous rate of this process, which may be
     * time or context-dependent.
     *
     * @param state the current simulation state.
     *
     * @return the instantaneous rate of this process for the given
     * simulation state.
     */
    public abstract StochRate computeRate(AgentState<A> state);

    /**
     * Updates the population of stochastic agents after this process
     * occurs.
     *
     * @param population the population of stochastic agents prior to
     * the occurrence of this process.
     */
    public abstract void updatePopulation(AgentPopulation<A> population);

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
     * Updates the instantaneous rate of this process after an event
     * occurs.
     *
     * @param state the simulation state following the occurrence of
     * the last event.
     */
    public void updateRate(AgentState<A> state) {
        rate = computeRate(state);
    }

    @Override public int getProcIndex() {
        return (int) getIndex();
    }

    @Override public StochRate getStochRate() {
        if (rate != null)
            return rate;
        else
            throw new IllegalStateException("The process rate has not been assigned.");
    }
}
