
package jam.stoch.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.lang.JamException;
import jam.lang.OrdinalMap;
import jam.stoch.RateLink;
import jam.stoch.StochSystem;

/**
 * Represents a system of coupled stochastic processes involving
 * discrete stochastic agents.
 */
public abstract class AgentSystem<A extends StochAgent, P extends AgentProc<A>> extends StochSystem<P> {
    private final OrdinalMap<A> agentMap;
    private final AgentPopulation<A> agentPop;

    /**
     * Creates a new coupled stochastic system containing discrete
     * agents.
     *
     * @param agents the discrete stochastic agents in the system.
     *
     * @param agentPop the initial population of the stochastic agents
     * in the system.
     *
     * @param procs the stochastic process which compose the system.
     *
     * @param links the edges of the directed dependency graph for the
     * system.
     *
     * @throws RuntimeException if any rate links refer to processes
     * not contained in the input collection.
     */
    protected AgentSystem(Collection<A> agents, AgentPopulation<A> agentPop,
                          Collection<P> procs, Collection<RateLink<P>> links) {
        super(procs, links);

        this.agentPop = agentPop;
        this.agentMap = OrdinalMap.hash(agents);
    }

    /**
     * Adds one agent to this system.
     *
     * @param agent the agent to add.
     */
    protected void addAgent(A agent) {
        addAgent(agent, 1);
    }

    /**
     * Adds instances of an agent to this system.
     *
     * @param agent the agent to add.
     *
     * @param count the number of instances to add.
     *
     * @throws IllegalArgumentException unless the count is positive.
     */
    protected void addAgent(A agent, int count) {
        if (count < 1)
            throw new IllegalArgumentException("Agent count must be positive.");

        agentMap.add(agent);
        agentPop.add(agent, count);
    }

    /**
     * Removes one agent from this system.
     *
     * @param agent the agent to remove.
     *
     * @throws IllegalArgumentException unless this system contains at
     * least one instance of the specified agent.
     */
    protected void removeAgent(A agent) {
        removeAgent(agent, 1);
    }

    /**
     * Removes one agent from this system.
     *
     * @param agent the agent to remove.
     *
     * @param count the number of instances to remove.
     *
     * @throws IllegalArgumentException unless this system contains at
     * least {@code count} instances of the agent.
     */
    protected void removeAgent(A agent, int count) {
        int prevCount = countAgent(agent);

        if (prevCount < count)
            throw new IllegalArgumentException("Agent count must remain non-negative.");

        agentPop.remove(agent, count);

        if (prevCount == count)
            agentMap.remove(agent);
    }

    /**
     * Assigns the population of an agent.
     *
     * @param agent the agent to add.
     *
     * @param count the number of agents to assign.
     *
     * @throws IllegalArgumentException if the count is negative.
     */
    protected void setCount(A agent, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Agent count must be non-negative.");

        agentPop.set(agent, count);

        if (count == 0)
            agentMap.remove(agent);
        else
            agentMap.add(agent);
    }

    /**
     * Returns a runtime exception for an invalid agent index.
     *
     * @param index the invalid agent index.
     *
     * @return a runtime exception for the specified agent index.
     */
    public static RuntimeException invalidAgentException(int index) {
        return JamException.runtime("Invalid agent index: [%d].", index);
    }

    /**
     * Returns a runtime exception for an invalid agent.
     *
     * @param agent the invalid agent.
     *
     * @return a runtime exception for the specified agent.
     */
    public static RuntimeException invalidAgentException(StochAgent agent) {
        return invalidAgentException(agent.getAgentIndex());
    }

    /**
     * Identifies agents contained in this stochastic system.
     *
     * @param index the ordinal index of the agent in question.
     *
     * @return {@code true} iff this system contains an agent with
     * the specified index.
     */
    public boolean containsAgent(int index) {
        return agentMap.contains(index);
    }

    /**
     * Counts the number of instances of an agent in this system.
     *
     * @param agent the agent to count.
     *
     * @return the number of instances of the specified agent
     * contained in this system.
     */
    public int countAgent(A agent) {
        return agentPop.count(agent);
    }

    /**
     * Returns the total population count for a collection of agents.
     *
     * @param agents the agents of interest.
     *
     * @return the total population count across the specified agents.
     */
    public int countAgents(Set<A> agents) {
        int total = 0;

        for (A agent : agents)
            total += countAgent(agent);

        return total;
    }

    /**
     * Accesses stochastic agents in this system by their ordinal index.
     *
     * @param index the ordinal index of the desired agent.
     *
     * @return the stochastic agent with the specified index.
     *
     * @throws RuntimeException unless this system contains an agent
     * with the specified index.
     */
    public A getAgent(int index) {
        A agent = agentMap.get(index);

        if (agent != null)
            return agent;
        else
            throw invalidAgentException(index);
    }

    /**
     * Returns a read-only view of the stochastic agents that
     * compose this system.
     *
     * @return a read-only view of the stochastic agents that
     * compose this system.
     */
    public Collection<A> viewAgents() {
        return Collections.unmodifiableCollection(agentMap.values());
    }

    @Override protected void updateState() {
        P lastProc = lastEventProcess();
        Collection<P> dependents = viewDependents(lastProc);

        updatePopulation(lastProc);
        lastProc.updateRate(this);

        for (P dependent : dependents)
            dependent.updateRate(this);
    }

    private void updatePopulation(P lastProc) {
        for (A reactant : lastProc.getReactants())
            removeAgent(reactant);

        for (A product : lastProc.getProducts())
            addAgent(product);
    }
}
