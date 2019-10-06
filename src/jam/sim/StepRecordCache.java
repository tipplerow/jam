
package jam.sim;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jam.util.AutoList;
import jam.util.ConcatIterator;
import jam.util.MapFactory;
import jam.util.ReadOnlyIterator;

/**
 * Maintains a cache of step records keyed by trial index and time
 * step.
 */
public final class StepRecordCache<R extends StepRecord> extends AbstractCollection<R> {
    //
    // The outer collection is a list indexed by simulation trial,
    // (because data will almost certainly be stored for each trial
    // (storage is "dense" for trials).  We choose a tree map for the
    // inner collection (storage is "sparse" for time steps), because
    // the reporter may choose to record data only at large intervals
    // (especially in very long simulations).
    //
    private final List<Map<Integer, R>> records;

    private StepRecordCache() {
        this.records = AutoList.create(MapFactory.tree());
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
     * Identifies records in this cache.
     *
     * @param trialIndex the trial index of interest.
     *
     * @param timeStep the time step of interest.
     *
     * @return {@code true} iff this cache contains a record for the
     * specified trial index and time step.
     */
    public boolean contains(int trialIndex, int timeStep) {
        return records.get(trialIndex).containsKey(timeStep);
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
        return records.get(trialIndex).get(timeStep);
    }

    /**
     * Retrieves the records for a given trial.
     *
     * @param trialIndex the desired trial index.
     *
     * @return a read-only collection containing the records for the
     * specified trial index orderd by time step (an empty collection
     * if this cache does not contain any records for the given trial).
     */
    public Collection<R> lookupTrial(int trialIndex) {
        return Collections.unmodifiableCollection(records.get(trialIndex).values());
    }

    /**
     * Retrieves the records for a given time step.
     *
     * @param timeStep the desired time step.
     *
     * @return a read-only collection containing the records for the
     * specified time step ordered by trial index (an empty collection
     * if this cache does not contain any records for the given time
     * step).
     */
    public Collection<R> lookupTimeStep(int timeStep) {
        List<R> result = new ArrayList<R>(records.size());

        for (Map<Integer, R> map : records) {
            R record = map.get(timeStep);

            if (record != null)
                result.add(record);
        }

        return Collections.unmodifiableCollection(result);
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
        return records.get(trialIndex).remove(timeStep);
    }

    @Override public boolean add(R record) {
        records.get(record.getTrialIndex()).put(record.getTimeStep(), record);
        return true;
    }

    @Override public boolean contains(Object obj) {
        return (obj instanceof StepRecord) && containsRecord((StepRecord) obj);
    }

    private boolean containsRecord(StepRecord record) {
        return contains(record.getTrialIndex(), record.getTimeStep());
    }

    @Override public Iterator<R> iterator() {
        List<Iterator<R>> iterators = new ArrayList<Iterator<R>>(records.size());

        for (Map<Integer, R> map : records)
            iterators.add(map.values().iterator());

        return ReadOnlyIterator.create(ConcatIterator.concat(iterators));
    }

    @Override public boolean isEmpty() {
        for (Map<Integer, R> map : records)
            if (!map.isEmpty())
                return false;

        return true;
    }

    @Override public boolean remove(Object obj) {
        if (obj instanceof StepRecord)
            return removeRecord((StepRecord) obj);
        else
            return false;
    }

    private boolean removeRecord(StepRecord record) {
        return remove(record.getTrialIndex(), record.getTimeStep()) != null;
        /*
        R prev = remove(record.getTrialIndex(), record.getTimeStep());

        if (prev != null)
            return true;
        else
            return false;
        */
    }

    @Override public int size() {
        int size = 0;

        for (Map<Integer, R> map : records)
            size += map.size();

        return size;
    }
}
