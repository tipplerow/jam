
package jam.stoch.agent;

import java.util.Collection;
import java.util.List;

import jam.stoch.StochEvent;
import jam.stoch.StochTime;

/**
 * Encapsulates the current state of an agent-based stochastic system.
 */
public interface AgentState<A extends StochAgent> {
    /**
     * Returns the population count for a given agent.
     *
     * @param agent the agent of interest.
     *
     * @return the population count of the specified agent.
     */
    public abstract int countAgent(A agent);

    /**
     * Returns the total population count for a collection of agents.
     *
     * @param agents the agents of interest.
     *
     * @return the total population count across the specified agents.
     */
    public default int countAgents(Collection<A> agents) {
        int total = 0;

        for (A agent : agents)
            total += countAgent(agent);

        return total;
    }

    /**
     * Returns the last event to occur in the stochastic system.
     *
     * @return the last event to occur in the stochastic system
     * ({@code null} if no event has occurred yet).
     */
    public abstract StochEvent lastEvent();
    
    /**
     * Returns the time when the last event occurred.
     *
     * @return the time when the last event occurred.
     */
    public default StochTime lastTime() {
        StochEvent event = lastEvent();

        if (event != null)
            return event.getTime();
        else
            return StochTime.ZERO;
    }

    /**
     * Returns a read-only list of the stochastic agents.
     *
     * @return a read-only list of the stochastic agents.
     */
    public abstract List<A> listAgents();
}
