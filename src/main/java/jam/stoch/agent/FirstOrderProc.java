
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.stoch.StochRate;

/**
 * Represents a stochastic process with a single reactant, {@code R}.
 * The process occurs at a rate {@code k * nR}, where {@code k} is the
 * instantaneous rate constant and {@code nR} is the current number of
 * instances of {@code R}.
 */
public abstract class FirstOrderProc<A extends StochAgent> extends AgentProc<A> {
    /**
     * The reactant agent.
     */
    protected final A reactant;

    /**
     * Creates a new first-order process with a fixed reactant.
     *
     * @param reactant the reactant agent for the process.
     */
    protected FirstOrderProc(A reactant) {
        this.reactant = reactant;
    }

    /**
     * Returns the reactant agent for this process.
     *
     * @return the reactant agent for this process.
     */
    public A getReactant() {
        return reactant;
    }

    @Override public StochRate computeRate(AgentSystem<A, ?> system) {
        return computeRate(system, getRateConstant(system), reactant);
    }

    @Override public Multiset<A> getReactants() {
        return ImmutableMultiset.of(reactant);
    }

    @Override public void updatePopulation(AgentPopulation<A> population) {
        population.remove(reactant);
        population.add(getProducts());
    }
}
