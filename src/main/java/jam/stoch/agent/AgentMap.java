
package jam.stoch.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jam.lang.OrdinalMap;

/**
 * Provides a collection of stochastic agents indexed by their ordinal
 * value.
 */
public final class AgentMap extends OrdinalMap<StochAgent> {
    private AgentMap(Map<Long, StochAgent> map) {
        super(map);
    }

    /**
     * Creates a new, empty agent map backed by a {@code HashMap}.
     *
     * @return a new, empty agent map backed by a {@code HashMap}.
     */
    public static AgentMap create() {
        return new AgentMap(new HashMap<Long, StochAgent>());
    }

    /**
     * Creates an agent map backed by a {@code HashMap} and
     * populates it with a collection of stochastic agents.
     *
     * @param agents the stochastic agents to add to the map.
     *
     * @return an agent map backed by a {@code HashMap}
     * containing the specified agents.
     */
    public static AgentMap create(Collection<? extends StochAgent> agents) {
        AgentMap map = create();
        map.addAll(agents);
        return map;
    }
}
