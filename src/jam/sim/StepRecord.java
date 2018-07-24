
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

    /**
     * Keys this record by trial index and time step.
     *
     * @return a new key for this record.
     */
    public final Key key() {
        return new Key(this);
    }

    /**
     * Creates a step record key.
     *
     * @param trialIndex the index of the simulation trial.
     *
     * @param timeStep the time step in the simulation trial.
     *
     * @return a new key for the trial index and time step.
     */
    public static Key key(int trialIndex, int timeStep) {
        return new Key(trialIndex, timeStep);
    }

    /**
     * Keys simulation records by trial index and time step.
     */
    public static final class Key {
        private final int trialIndex;
        private final int timeStep;

        private Key(StepRecord parent) {
            this(parent.getTrialIndex(), parent.getTimeStep());
        }

        private Key(int trialIndex, int timeStep) {
            this.trialIndex = trialIndex;
            this.timeStep   = timeStep;
        }

        @Override public boolean equals(Object obj) {
            return (obj instanceof Key) && equalsKey((Key) obj);
        }

        private boolean equalsKey(Key that) {
            return this.timeStep == that.timeStep && this.trialIndex == that.trialIndex;
        }

        @Override public int hashCode() {
            return timeStep + (trialIndex << 16);
        }
    }
}
