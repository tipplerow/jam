
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.stoch.StochRate;

/**
 * Represents the first-order stochastic process of cell death.  Given
 * the cellular agent {@code C}, the process occurs at a rate equal to
 * {@code k * nC}, where {@code k} is the instantaneous rate constant
 * and {@code nC} is the current number of cells.  After the process
 * occurs, the number of cells decreases by one: {@code nC => nC - 1}.
 */
public abstract class DeathProc<A extends StochAgent> extends FirstOrderProc<A> {
    /**
     * Creates a new death process with a fixed agent.
     *
     * @param agent the target agent for the process.
     */
    protected DeathProc(A agent) {
        super(agent);
    }

    @Override public Multiset<A> getProducts() {
        return ImmutableMultiset.of();
    }

    @Override public void updatePopulation(AgentPopulation<A> population) {
        population.remove(reactant);
    }
}
