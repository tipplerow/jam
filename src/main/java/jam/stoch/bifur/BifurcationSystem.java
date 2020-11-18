
package jam.stoch.bifur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jam.stoch.StochEvent;
import jam.stoch.StochSystem;

/**
 * Represents a stochastic system where a fixed number of objects
 * are irreversibly transformed from an initial state (the source)
 * into one or more final states (the sinks).
 */
public final class BifurcationSystem implements StochSystem<BifurcationPath> {
    private final List<BifurcationPath> paths;

    private BifurcationSystem(List<BifurcationPath> paths) {
        this.paths = paths;
    }

    /**
     * Creates a new bifurcation system with a fixed population and
     * transition rate constants.
     *
     * <p>The final states (sinks) are implicitly defined by the order
     * of the transition rate constants.
     *
     * @param population the fixed number of objects in the system.
     *
     * @param unitRates the unit rate constants for transitions from
     * the initial state (source) to each final state (sink).
     *
     * @return the bifurcation system defined by the input parameters.
     *
     * @throws IllegalArgumentException unless the population is positive
     * and at least one pathway (unit rate) is defined.
     */
    public static BifurcationSystem create(int population, double... unitRates) {
        return new BifurcationSystem(createPaths(population, unitRates));
    }

    private static List<BifurcationPath> createPaths(int population, double[] unitRates) {
        int pathCount = unitRates.length;

        if (pathCount < 1)
            throw new IllegalArgumentException("At least one pathway must be defined.");

        if (population < 1)
            throw new IllegalArgumentException("The population must be positive.");

        BifurcationSource source = BifurcationSource.create(population);
        List<BifurcationPath> paths = new ArrayList<BifurcationPath>(pathCount);
        
        for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
            paths.add(BifurcationPath.create(pathIndex, source, unitRates[pathIndex]));

        return Collections.unmodifiableList(paths);
    }

    /**
     * Returns an indexed sink state in this bifurcation system.
     *
     * @param index the index of the sink state.
     *
     * @return the sink state with the specified index.
     *
     * @throws IllegalArgumentException unless the index is in the
     * valid range {@code [0, size())}.
     */
    public BifurcationSink getSink(int index) {
        return getProcess(index).getSink();
    }

    /**
     * Returns the common source state for this bifurcation system.
     *
     * @return the common source state for this bifurcation system.
     */
    public BifurcationSource getSource() {
        return getProcess(0).getSource();
    }

    @Override public int countProcesses() {
        return paths.size();
    }

    @Override public BifurcationPath getProcess(int index) {
        return paths.get(index);
    }

    @Override public List<BifurcationPath> listProcesses() {
        return paths;
    }

    @Override public Collection<BifurcationPath> processEvent(StochEvent<BifurcationPath> event) {
        // The sink updates its population and the population of the
        // source...
        event.getProcess().getSink().update();

        // All sinks share the same source, and the source population
        // decreases by one member as a result of the event, so all
        // rates are changed...
        return paths;
    }
}
