
package jam.stoch.agent;

import java.util.List;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.stoch.StochRate;

/**
 * Represents the stochastic process of cell death.  With a target
 * agent {@code A} and an instantaneous rate constant {@code k}, the
 * death process occurs at a rate equal to {@code k * nA}, where
 * {@code nA} is the number of instances of {@code A}.  After the
 * death process occurs, the number of target agents decreases by
 * one: {@code nA => nA - 1}.
 */
public abstract class DeathProc<A extends StochAgent> extends AgentProc<A> {
    private final A agent;

    private final Multiset<A> products;
    private final Multiset<A> reactants;

    /**
     * Creates a new death process with a fixed agent.
     *
     * @param agent the target agent for the process.
     */
    protected DeathProc(A agent) {
        this(null, agent);
    }

    /**
     * Creates a new death process with a fixed agent.
     *
     * @param rate the initial rate of the process.
     *
     * @param agent the target agent for the process.
     */
    protected DeathProc(StochRate rate, A agent) {
        super(rate);

        this.agent = agent;
        this.products = ImmutableMultiset.of();
        this.reactants = ImmutableMultiset.of(agent);
    }

    /**
     * Returns the target agent for this death process.
     *
     * @return the target agent for this death process.
     */
    public A getAgent() {
        return agent;
    }

    @Override public Multiset<A> getReactants() {
        return reactants;
    }

    @Override public Multiset<A> getProducts() {
        return products;
    }

    @Override public void updatePopulation(AgentPopulation<A> population) {
        population.remove(agent);
    }
}
