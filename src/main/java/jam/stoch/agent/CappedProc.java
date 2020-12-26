
package jam.stoch.agent;

import java.util.Set;
import com.google.common.collect.Multiset;
import jam.lang.JamException;

/**
 * Represents a capacity-limited process: The rate of an underlying
 * process drops to zero when the total population of a subset of
 * stochastic agents reaches a fixed capacity threshold. (The rate
 * is unchanged below the threshold.)
 */
public final class CappedProc<A extends StochAgent> extends AgentProc<A> {
    private final int capacity;
    private final Set<A> capped;
    private final AgentProc<A> baseProc;

    private CappedProc(AgentProc<A> baseProc, Set<A> capped, int capacity) {
        super(null);

        validateCapacity(capacity);

        this.capped = capped;
        this.capacity = capacity;
        this.baseProc = baseProc;
    }

    /**
     * Creates a new capacity-limited process.
     *
     * @param baseProc the underlying base process.
     *
     * @param capped the stochastic agents that contribute to the
     * population limit.
     *
     * @param capacity the maximum population of the capped agents.
     *
     * @return a new capacity-limited process with the specified
     * parameters.
     */
    public static <A extends StochAgent> CappedProc<A> create(AgentProc<A> baseProc, Set<A> capped, int capacity) {
        return new CappedProc<A>(baseProc, capped, capacity);
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

    @Override public Multiset<A> getReactants() {
        return baseProc.getReactants();
    }

    @Override public Multiset<A> getProducts() {
        return baseProc.getProducts();
    }

    @Override public double getRateConstant(AgentSystem<A, ?> system) {
        if (system.countAgents(capped) < capacity)
            return baseProc.getRateConstant(system);
        else
            return 0.0;
    }
}
