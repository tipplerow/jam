
package jam.stoch.agent;

import java.util.List;

import jam.lang.JamException;

/**
 * Represents the discrete agents in a stochastic process.
 */
public interface StochAgent {
    /**
     * Returns a unique integer index for this agent.
     *
     * @return a unique integer index for this agent.
     */
    public abstract int getAgentIndex();

    /**
     * Ensures that a list of stochastic agents is arranged such that
     * {@code agents.get(k).getAgentIndex() == k} for all elements in
     * a list.
     *
     * @param agents the list to validate.
     *
     * @throws RuntimeException unless {@code agents.get(k).getAgentIndex() == k}
     * for every agent in the list.
     */
    public static void validateList(List<? extends StochAgent> agents) {
        for (int index = 0; index < agents.size(); ++index)
            if (agents.get(index).getAgentIndex() != index)
                throw JamException.runtime("Agents are not arranged in index order.");
    }
}
