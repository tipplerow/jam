
package jam.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import jam.stream.JamStreams;

/**
 * Defines a common interface for views (unmodifiable collections) of
 * records indexed by keys.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public interface RecordView<K, V> extends Iterable<V> {
    /**
     * Counts the number of records in this view.
     *
     * @return the number of records in this view.
     */
    public abstract int count();

    /**
     * Retrieves all records in this view.
     *
     * @return an unmodifiable collection containing every record in
     * this view (in no particular order).
     */
    public abstract Collection<V> fetch();

    /**
     * Retrieves the records indexed by collection of keys.
     *
     * @param keys the keys of the records to select.
     *
     * @return an unmodifiable collection containing the records that
     * match the specified keys (in no particular order).
     */
    public abstract Collection<V> fetch(Collection<K> keys);

    /**
     * Retrieves the records in this view that pass a given filter.
     *
     * @param predicate the filter function to apply to all records.
     *
     * @return an unmodifiable collection containing the records that
     * pass the specified filter (in no particular order).
     */
    public default Collection<V> fetch(Predicate<V> predicate) {
        return JamStreams.filterParallel(fetch(), predicate);
    }

    /**
     * Retrieves the unique keys in this view.
     *
     * @return an unmodifiable set containing every unique key in this
     * view.
     */
    public abstract Set<K> keys();

    @Override public default Iterator<V> iterator() {
        return ReadOnlyIterator.create(fetch());
    }
}
