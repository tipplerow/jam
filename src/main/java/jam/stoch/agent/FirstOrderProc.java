
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.stoch.StochRate;

/**
 * Represents a stochastic process with a single reactant, {@code R}.
 * The process occurs at a rate {@code k * nR}, where {@code k} is the
 * instantaneous rate constant and {@code nR} is the current number of
 * instances of {@code R}.  After the process occurs, the population of
 * {@code R} decreases by one: {@code nR => nR - 1}.
 */
public abstract class FirstOrderProc extends AgentProc {
    /**
     * The reactant agent.
     */
    protected final StochAgent reactant;

    /**
     * Creates a new first-order process with a fixed reactant.
     *
     * @param reactant the reactant agent for the process.
     */
    protected FirstOrderProc(StochAgent reactant) {
        this.reactant = reactant;
    }

    /**
     * Returns the reactant agent for this process.
     *
     * @return the reactant agent for this process.
     */
    public StochAgent getReactant() {
        return reactant;
    }

    @Override public StochRate computeRate(AgentSystem system) {
        return computeRate(system, getRateConstant(system), reactant);
    }

    @Override public Multiset<StochAgent> getReactants() {
        return ImmutableMultiset.of(reactant);
    }

    @Override public void updatePopulation(AgentPopulation population) {
        population.remove(reactant);
        population.add(getProducts());
    }
}
