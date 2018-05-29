
package jam.sim;

import jam.math.IntRange;

/**
 * Records data from a single simulation trial.
 */
public abstract class TrialRecord {
    private final int trialIndex;

    /**
     * Creates a new record for a given trial.
     *
     * @param trialIndex the index of the trial described by the record.
     */
    protected TrialRecord(int trialIndex) {
        IntRange.NON_NEGATIVE.validate("Trial index", trialIndex);
        this.trialIndex = trialIndex;
    }

    /**
     * Returns the index of the trial described by this record.
     *
     * @return the index of the trial described by this record.
     */
    public final int getTrialIndex() {
        return trialIndex;
    }
}
