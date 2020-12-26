
package jam.stoch.agent;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Maintains a number count of each agent in a stochastic simulation.
 */
public final class AgentPopulation<A extends StochAgent> {
    private final Multiset<A> counts = HashMultiset.create();

    private AgentPopulation() {
    }

    /**
     * Creates a new empty agent population.
     *
     * @return a new empty agent population.
     */
    public static <A extends StochAgent> AgentPopulation<A> create() {
        return new AgentPopulation<A>();
    }

    /**
     * Creates a new agent population.
     *
     * @param counts the initial population counts for the stochastic
     * agents.
     *
     * @return a new agent population with the specified agent counts.
     */
    public static <A extends StochAgent> AgentPopulation<A> create(Multiset<A> counts) {
        AgentPopulation<A> population = create();

        for (Multiset.Entry<A> entry : counts.entrySet())
            population.set(entry.getElement(), entry.getCount());

        return population;
    }

    /**
     * Adds one agent to this population.
     *
     * @param agent the agent to add.
     */
    public void add(A agent) {
        counts.add(agent);
    }

    /**
     * Adds instances of an agent to this population.
     *
     * @param agent the agent to add.
     *
     * @param count the number of instances to add.
     *
     * @throws IllegalArgumentException unless the count is positive.
     */
    public void add(A agent, int count) {
        if (count < 1)
            throw new IllegalArgumentException("Agent count must be positive.");
        else
            counts.add(agent, count);
    }

    /**
     * Counts the number of instances of an agent in this population.
     *
     * @param agent the agent to count.
     *
     * @return the number of instances of the specified agent
     * contained in this population.
     */
    public int count(A agent) {
        return counts.count(agent);
    }

    /**
     * Removes one agent from this population.
     *
     * @param agent the agent to remove.
     *
     * @throws IllegalArgumentException unless this population
     * contains at least one instance of the specified agent.
     */
    public void remove(A agent) {
        remove(agent, 1);
    }

    /**
     * Removes one agent from this population.
     *
     * @param agent the agent to remove.
     *
     * @param count the number of instances to remove.
     *
     * @throws IllegalArgumentException unless this population
     * contains at least {@code count} instances of the agent.
     */
    public void remove(A agent, int count) {
        if (counts.count(agent) < count)
            throw new IllegalArgumentException("Agent count must remain non-negative.");
        else
            counts.remove(agent, count);
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
    public void set(A agent, int count) {
        if (count < 0)
            throw new IllegalArgumentException("Agent count must be non-negative.");
        else
            counts.setCount(agent, count);
    }
}
