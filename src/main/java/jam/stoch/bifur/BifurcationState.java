
package jam.stoch.bifur;

import jam.math.IntRange;

/**
 * Represents the available states in a bifurcation system.
 */
public abstract class BifurcationState {
    private int population;

    private static final IntRange POPULATION_RANGE = IntRange.NON_NEGATIVE;

    /**
     * Creates a new bifurcation state with a given initial
     * population.
     *
     * @param population the initial population of the state.
     *
     * @throws IllegalArgumentException if the population is
     * negative.
     */
    protected BifurcationState(int population) {
        setPopulation(population);
    }

    /**
     * Assigns a new population to this state.
     *
     * @param population the population to assign.
     *
     * @throws IllegalArgumentException if the population is
     * negative.
     */
    protected void setPopulation(int population) {
        POPULATION_RANGE.validate("Population", population);
        this.population = population;
    }

    /**
     * Updates the population of this state after a transition event.
     */
    abstract void update();

    /**
     * Returns the current population of this state.
     *
     * @return the current population of this state.
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Identifies empty states.
     *
     * @return {@code true} iff this state has zero population.
     */
    public boolean isEmpty() {
        return population <= 0;
    }
}
