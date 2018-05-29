
package jam.sim;

import jam.app.JamApp;
import jam.app.JamProperties;

/**
 * Provides a base class for discrete-time simulation applications.
 */
public abstract class DiscreteTimeSimulation extends JamApp {
    // The number of trials already executed in the current
    // simulation...
    private int trialCount = 0;

    // The number of time steps already executed during the
    // current trial...
    private int timeStep = 0;

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
     * Returns the offset to apply to the trial counter to generate a
     * global (across different simulation runs) trial index.
     *
     * <p>This base class implementation simply returns zero.
     *
     * @return the trial index offset.
     */
    protected int getTrialIndexOffset() {
        return 0;
    }

    /**
     * Returns the total number of trials to execute.
     *
     * @return the total number of trials to execute.
     */
    public abstract int getTrialTarget();

    /**
     * Returns the number of trials that have been executed in the
     * current simulation.
     *
     * @return the number of trials that have been executed in the
     * current simulation.
     */
    public int getTrialCount() {
        return trialCount;
    }

    /**
     * Returns the index of the current time step in the active trial.
     *
     * @return the index of the current time step in the active trial.
     */
    public int getTimeStep() {
        return timeStep;
    }

    /**
     * Returns the global (across different simulation runs) index of
     * the current trial: the sum of the trial count and the <em>trial
     * index offset</em>.
     *
     * @return the global index of the current trial.
     */
    public int getTrialIndex() {
        return getTrialCount() + getTrialIndexOffset();
    }

    /**
     * Runs a complete simulation.
     */
    public void runSimulation() {
        trialCount = 0;
        initializeSimulation();
        
        while (continueSimulation()) {
            //
            // Note that the trial index is incremented AFTER the
            // trial, because we start the indexing at zero to be
            // consistent with the indexing of collection items...
            //
            runTrial();
            ++trialCount;
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
     * <p>This base class implementation continues iff the number of
     * executed trials is less than the number requested (returned by
     * {@code getTrialTarget()}).
     *
     * @return {@code true} if the current simulation should be
     * continued.
     */
    protected boolean continueSimulation() {
        return getTrialCount() < getTrialTarget();
    }

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
