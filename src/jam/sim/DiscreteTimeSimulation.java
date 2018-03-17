
package jam.sim;

import jam.app.JamApp;

/**
 * Provides a base class for discrete-time simulation applications.
 */
public abstract class DiscreteTimeSimulation extends JamApp {
    // The global trial index...
    private static int trialIndex;

    // The global time step index...
    private static int timeStep;

    /**
     * Creates a new simulation instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles one or more files containing the system
     * properties that define the simulation parameters.
     *
     * @throws IllegalArgumentException unless at least one property
     * file is specified.
     */
    protected DiscreteTimeSimulation(String[] propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Returns the (global) index of the current trial.
     *
     * <p><b>The first trial is assigned index zero.</b>
     *
     * @return the (global) index of the current trial.
     */
    public static int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Returns the (global) index of the current time step.
     *
     * @return the (global) index of the current time step.
     */
    public static int getTimeStep() {
        return timeStep;
    }

    /**
     * Runs a complete simulation.
     */
    public void runSimulation() {
        trialIndex = 0;
        initializeSimulation();
        
        while (continueSimulation()) {
            //
            // Note that the trial index is incremented AFTER the
            // trial, because we start the indexing at zero to be
            // consistent with the indexing of collection items...
            //
            runTrial();
            ++trialIndex;
        }

        finalizeSimulation();
    }

    /**
     * Prepares the system for the simulation trials: loads system
     * properties, opens output streams, etc.
     *
     * <p>This method is called immediately before the first
     * simulation trial is executed.
     */
    protected abstract void initializeSimulation();

    /**
     * Decides whether or not to continue the current simulation
     * (execute another trial).
     *
     * @return {@code true} if the current simulation should be
     * continued.
     */
    protected abstract boolean continueSimulation();

    /**
     * Executes tasks after all simulation trials have finished:
     * generates summary reports, closes output streams, etc.
     *
     * <p>This method is called immediately after the last
     * simulation trial is executed.
     */
    protected abstract void finalizeSimulation();

    /**
     * Executes one independent simulation trial.
     */
    protected void runTrial() {
        timeStep = 0;
        initializeTrial();

        while (continueTrial()) {
            ++timeStep;
            advanceTrial();
        }

        finalizeTrial();
    }

    /**
     * Prepares the system for a new simulation trial.
     *
     * <p>This method is called immediately before the first step in
     * the new simulation trial.
     */
    protected abstract void initializeTrial();

    /**
     * Decides whether or not to continue the current trial (execute
     * the next time step).
     *
     * @return {@code true} if the current trial will be continued.
     */
    protected abstract boolean continueTrial();

    /**
     * Executes one time step in the current simulation trial.
     */
    protected abstract void advanceTrial();

    /**
     * Executes tasks after the latest simulation trial has finished.
     *
     * <p>This method is called immediately after the last step in the
     * latest simulation trial.
     */
    protected abstract void finalizeTrial();
}
