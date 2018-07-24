
package jam.sim;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a cache of step records keyed by trial index and time
 * step.
 */
public final class StepRecordCache<R extends StepRecord> {
    private final Map<StepRecord.Key, R> records = new HashMap<StepRecord.Key, R>();

    private StepRecordCache() {
    }

    /**
     * Creates an empty cache.
     *
     * @param <R> the concrete step record type.
     *
     * @return an empty cache.
     */
    public static <R extends StepRecord> StepRecordCache<R> create() {
        return new StepRecordCache<R>();
    }

    /**
     * Adds a record to this cache.
     *
     * @param record the record to add.
     *
     * @return the previous record associated with the key of the
     * input record ({@code null} if there was none).
     */
    public R add(R record) {
        return records.put(record.key(), record);
    }

    /**
     * Retrieves the record with a given trial index and time step.
     *
     * @param trialIndex the desired trial index.
     *
     * @param timeStep the desired time step.
     *
     * @return the record with the specified trial index and time
     * step; {@code null} if there is no matching record in this
     * cache.
     */
    public R lookup(int trialIndex, int timeStep) {
        return lookup(StepRecord.key(trialIndex, timeStep));
    }

    /**
     * Retrieves the record with a given key.
     *
     * @param key the desired record key.
     *
     * @return the record with the specified key; {@code null} if
     * there is no matching record in this cache.
     */
    public R lookup(StepRecord.Key key) {
        return records.get(key);
    }

    /**
     * Removes the record with a given trial index and time step.
     *
     * @param trialIndex the desired trial index.
     *
     * @param timeStep the desired time step.
     *
     * @return the record with the specified trial index and time
     * step; {@code null} if there was no matching record in this
     * cache.
     */
    public R remove(int trialIndex, int timeStep) {
        return remove(StepRecord.key(trialIndex, timeStep));
    }

    /**
     * Removes the record with a given key.
     *
     * @param key the desired record key.
     *
     * @return the record with the specified key; {@code null} if
     * there was no matching record in this cache.
     */
    public R remove(StepRecord.Key key) {
        return records.remove(key);
    }

    /**
     * Retrieves the record with a given trial index and time step.
     *
     * @param trialIndex the desired trial index.
     *
     * @param timeStep the desired time step.
     *
     * @return the record with the specified trial index and time
     * step.
     *
     * @throws IllegalStateException if there is no matching record in
     * this cache.
     */
    public R require(int trialIndex, int timeStep) {
        return require(StepRecord.key(trialIndex, timeStep));
    }

    /**
     * Retrieves the record with a given key.
     *
     * @param key the desired record key.
     *
     * @return the record with the specified key.
     *
     * @throws IllegalStateException if there is no matching record in
     * this cache.
     */
    public R require(StepRecord.Key key) {
        R record = lookup(key);

        if (record != null)
            return record;
        else
            throw new IllegalStateException("Required step record was not found.");
    }
}
