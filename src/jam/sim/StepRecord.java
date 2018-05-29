
package jam.sim;

import jam.math.IntRange;

/**
 * Records data from a single time step of one simulation trial.
 */
public abstract class StepRecord extends TrialRecord {
    private final int timeStep;

    /**
     * Creates a new record for a given trial and time step.
     *
     * @param trialIndex the index of the trial described by the record.
     *
     * @param timeStep the time step described by the record.
     */
    protected StepRecord(int trialIndex, int timeStep) {
        super(trialIndex);

        IntRange.NON_NEGATIVE.validate("Time step", timeStep);
        this.timeStep = timeStep;
    }

    /**
     * Returns the time step described by this record.
     *
     * @return the time step described by this record.
     */
    public final int getTimeStep() {
        return timeStep;
    }
}
