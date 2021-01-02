
package jam.stoch.agent;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jam.lang.JamException;
import jam.stoch.StochRate;

/**
 * Represents a first-order transition process from one stochastic
 * agent to another (a cis-trans isomerization, for example). With a
 * reactive agent {@code R} and product agent {@code P}, the process
 * occurs at a rate equal to {@code k * nR}, where {@code k} is the
 * instantaneous rate constant and {@code nR} is the current number of
 * instances of {@code R}.  After the process occurs, one reactant is
 * consumed and one product is produced: {@code nR => nR - 1} and
 * {@code nP => nP + 1}.
 */
public abstract class TransitionProc extends FirstOrderProc {
    /**
     * The agent produced by this process.
     */
    protected final StochAgent product;

    /**
     * Creates a new transition process with a fixed reactant and
     * product.
     *
     * @param reactant the reactant agent for the process.
     *
     * @param product the product agent for the process.
     *
     * @throws RuntimeException if the reactant and product are
     * identical.
     */
    protected TransitionProc(StochAgent reactant, StochAgent product) {
        super(reactant);

        if (product.equals(reactant))
            throw JamException.runtime("Reactant and product must be distinct.");

        this.product = product;
    }

    /**
     * Returns the product agent for this process.
     *
     * @return the product agent for this process.
     */
    public StochAgent getProduct() {
        return product;
    }

    @Override public Multiset<StochAgent> getProducts() {
        return ImmutableMultiset.of(product);
    }

    @Override public void updatePopulation(AgentPopulation population) {
        population.remove(reactant);
        population.add(product);
    }
}
