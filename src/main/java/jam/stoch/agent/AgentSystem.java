
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
import jam.stoch.StochProc;
import jam.stoch.StochSystem;

/**
 * Represents a system of coupled stochastic processes involving
 * discrete stochastic agents.
 */
public abstract class AgentSystem extends StochSystem {
    private final AgentMap agentMap;
    private final AgentPopulation agentPop;

    /**
     * Creates a new coupled stochastic system containing discrete
     * agents.
     *
     * @param agentMap the discrete stochastic agents in the system.
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
    protected AgentSystem(AgentMap agentMap,
                          AgentPopulation agentPop,
                          Collection<AgentProc> procs,
                          Collection<RateLink> links) {
        super(procs, links);

        this.agentMap = agentMap;
        this.agentPop = agentPop;

        initRates();
    }

    private void initRates() {
        for (AgentProc proc : viewProcesses())
            proc.updateRate(this);
    }

    /**
     * Adds a new agent to this system.
     *
     * @param agent the agent to add.
     *
     * @param count the number of instances to add.
     *
     * @throws IllegalArgumentException if the count is negative.
     */
    protected void addAgent(StochAgent agent, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Agent count must be non-negative.");

        agentMap.add(agent);
        agentPop.add(agent, count);
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
    public int countAgent(StochAgent agent) {
        return agentPop.count(agent);
    }

    /**
     * Returns the total population count for a collection of agents.
     *
     * @param agents the agents of interest.
     *
     * @return the total population count across the specified agents.
     */
    public int countAgents(Set<StochAgent> agents) {
        int total = 0;

        for (StochAgent agent : agents)
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
    public StochAgent getAgent(int index) {
        StochAgent agent = agentMap.get(index);

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
    public Collection<StochAgent> viewAgents() {
        return Collections.unmodifiableCollection(agentMap.values());
    }

    @Override protected void updateState() {
        AgentProc lastProc = lastEventProcess();
        Set<? extends AgentProc> dependents = viewDependents(lastProc);

        lastProc.updatePopulation(this.agentPop);
        lastProc.updateRate(this);

        for (AgentProc dependent : dependents)
            dependent.updateRate(this);
    }

    @Override public AgentProc lastEventProcess() {
        return (AgentProc) super.lastEventProcess();
    }

    @SuppressWarnings("unchecked")
    @Override public Set<? extends AgentProc> viewDependents(StochProc proc) {
        return (Set<? extends AgentProc>) super.viewDependents(proc);
    }

    @SuppressWarnings("unchecked")
    @Override public Collection<? extends AgentProc> viewProcesses() {
        return (Collection<? extends AgentProc>) super.viewProcesses();
    }
}
