
package jam.stoch.agent;

import java.util.Collection;
import java.util.List;

import jam.lang.JamException;

/**
 * Represents a system of (possibly coupled) stochastic processes
 * involving discrete stochastic agents.
 */
public interface AgentSystem<A extends StochAgent, P extends AgentProc<A>> {
    /**
     * Returns a read-only list of the stochastic agents that
     * compose this system.
     *
     * @return a read-only list of the stochastic agents that
     * compose this system.
     */
    public abstract List<A> listAgents();

    /**
     * 
     */
    public abstract int count(A agent);

    /**
     * Returns the number of stochastic agents in this system.
     *
     * @return the number of stochastic agents in this system.
     */
    public default int countAgents() {
        return listAgents().size();
    }

    /**
     * Accesses agents in this system by their ordinal index.
     *
     * @param index the ordinal index of the desired agent.
     *
     * @return the agent with the specified index.
     *
     * @throws IndexOutOfBoundsException unless the index is valid:
     * {@code 0 <= index && index < countAgents()}.
     */
    public default A getAgent(int index) {
        return listAgents().get(index);
    }

    /**
     * Ensures that the stochastic agents are properly indexed.
     *
     * @throws RuntimeException unless the stochastic agents are
     * indexed from {@code 0} through {@code countAgents() - 1}.
     */
    public default void validateAgents() {
        StochAgent.validateList(listAgents());
    }
}
