
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

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

    private final Multiset<A> products;
    private final Multiset<A> reactants;

    /**
     * Creates a new birth process with fixed parent and child agents.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     */
    protected BirthProc(A parent, A child) {
        this(null, parent, child);
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

        this.products = ImmutableMultiset.of(parent, child);
        this.reactants = ImmutableMultiset.of(parent);
    }

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

    @Override public Multiset<A> getReactants() {
        return reactants;
    }

    @Override public Multiset<A> getProducts() {
        return products;
    }

    @Override public void updatePopulation(AgentPopulation<A> population) {
        population.add(child);
    }
}
