
package jam.stoch.bifur;

/**
 * Represents the absorbing destination state in a bifurcation
 * pathway.
 */
public final class BifurcationSink extends BifurcationState {
    private final BifurcationSource source;

    private BifurcationSink(BifurcationSource source) {
        super(0); // Sinks are initially empty...
        this.source = source;
    }

    /**
     * Creates an empty sink with a fixed source.
     *
     * @param source the source for the pathway into the sink.
     *
     * @return a new empty sink with the specified source.
     */
    public static BifurcationSink create(BifurcationSource source) {
        return new BifurcationSink(source);
    }

    /**
     * Returns the source for the pathway into this sink.
     *
     * @return the source for the pathway into this sink.
     */
    public BifurcationSource getSource() {
        return source;
    }

    /**
     * Increases the population of this sink by one and decreases the
     * population of the source by one.
     *
     * @throws IllegalStateException if the source for this sink is
     * empty.
     */
    @Override void update() {
        source.update();
        setPopulation(getPopulation() + 1);
    }
}
