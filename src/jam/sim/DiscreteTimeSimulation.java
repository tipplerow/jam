
package jam.sim;

/**
 * Provides a base class for discrete-time simulation applications.
 */
public abstract class DiscreteTimeSimulation {
    // The global time step index...
    private static int timeStep = 0;

    /**
     * Returns the (global) index of the current time step.
     *
     * @return the (global) index of the current time step.
     */
    public static int getTimeStep() {
        return timeStep;
    }

    /**
     * Advances the (global) time step index.
     */
    protected void advanceTimeStep() {
        ++timeStep;
    }

}
