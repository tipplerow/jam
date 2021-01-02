
package jam.stoch;

import java.util.Collection;

import jam.math.JamRandom;

/**
 * Provides a base class for stochastic simulation algorithms.
 */
public abstract class StochAlgo {
    /**
     * The random number source.
     */
    protected final JamRandom random;

    /**
     * The stochastic system being simulated.
     */
    protected final StochSystem system;

    /**
     * Creates a new stochastic simulation algorithm.
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     */
    protected StochAlgo(JamRandom random, StochSystem system) {
        this.random = random;
        this.system = system;
    }

    /**
     * Selects the next event to occur in the simulation.
     *
     * @return the next event in the simulation.
     */
    protected abstract StochEvent nextEvent();

    /**
     * Updates the internal state of this algorithm after an event
     * occurs.
     *
     * @param event the most recent event to occur.
     *
     * @param dependents processes whose rates have changed as a
     * result of the event (excluding the process that occurred).
     */
    protected abstract void updateState(StochEvent event, Collection<? extends StochProc> dependents);

    /**
     * Advances the simulation by selecting the next stochastic event
     * and updating the instantaneous process rates in the underlying
     * stochastic system.
     */
    public void advance() {
        StochEvent event = nextEvent();
        system.updateState(event);
        updateState(event, system.viewDependents(event.getProcess()));
    }

    /**
     * Returns the random number source.
     *
     * @return the random number source.
     */
    public JamRandom getRandom() {
        return random;
    }

    /**
     * Returns the stochastic system being simulated.
     *
     * @return the stochastic system being simulated.
     */
    public StochSystem getSystem() {
        return system;
    }
}
