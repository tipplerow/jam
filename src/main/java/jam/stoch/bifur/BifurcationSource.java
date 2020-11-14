
package jam.stoch.bifur;

/**
 * Represents the initial state in a bifurcation pathway.
 */
public final class BifurcationSource extends BifurcationState {
    private BifurcationSource(int population) {
        super(population);
    }

    /**
     * Creates a bifurcation source with a given initial population.
     *
     * @param population the initial population of the source.
     *
     * @return a new bifurcation source with the specified initial
     * population.
     */
    public static BifurcationSource create(int population) {
        return new BifurcationSource(population);
    }

    /**
     * Decreases the population of this source by one.
     *
     * @throws IllegalStateException if this source is empty.
     */
    @Override void update() {
        setPopulation(getPopulation() - 1);
    }
}
