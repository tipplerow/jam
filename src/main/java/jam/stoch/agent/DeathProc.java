
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.stoch.StochRate;

/**
 * Represents the first-order stochastic process of cell death.
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
