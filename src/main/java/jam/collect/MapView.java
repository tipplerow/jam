
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;
import jam.lang.ObjectUtil;

/**
 * Provides a read-only view of a one-to-one mapping from keys to
 * records.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public interface MapView<K, V> extends RecordView<K, V> {
    /**
     * Wraps a map in a view; subsequent changes to the
     * underlying map will be reflected in the view.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param map the map to view.
     *
     * @return a map view backed by a copy of the specified map.
     */
    public static <K, V> MapView<K, V> wrap(Map<K, V> map) {
        return MapWrapper.wrap(map);
    }

    /**
     * Determines whether another view is identical to this view.
     *
     * @param that the other view to compare with this.
     *
     * @return {@code true} iff the other view contains exactly the
     * same key/value mappings as this view.
     */
    public default boolean equalsView(MapView<K, V> that) {
        int thisCount = this.count();
        int thatCount = that.count();

        if (thisCount != thatCount)
            return false;

        Set<K> thisKeys = this.keys();
        Set<K> thatKeys = that.keys();

        if (!thisKeys.equals(thatKeys))
            return false;

        for (K key : thisKeys) {
            V thisValue = this.fetch(key);
            V thatValue = that.fetch(key);

            if (!ObjectUtil.equals(thisValue, thatValue))
                return false;
        }

        return true;
    }

    /**
     * Retrieves the record indexed by a given key.
     *
     * @param key the key of the record to fetch.
     *
     * @return the record with the specified key (or
     * {@code null} if there is no matching record).
     */
    public abstract V fetch(K key);

    /**
     * Retrieves the record indexed by a given key.
     *
     * @param key the key of the record to fetch.
     *
     * @return the record with the specified key.
     *
     * @throws RuntimeException if there is no matching record.
     */
    public default V require(K key) {
        V record = fetch(key);

        if (record != null)
            return record;
        else
            throw JamException.runtime("Missing required record: [%s].", key);
    }

    /**
     * Returns the records indexed by a collection of keys.
     *
     * @param keys the keys of the records to fetch.
     *
     * @return the list of size {@code n} where {@code n} is the size
     * of the input collection and {@code list.get(k)} is the record
     * fetched for the {@code k}th key returned by the key collection
     * iterator.
     *
     * @throws RuntimeException unless all records are present in this
     * view.
     */
    public default List<V> require(Collection<K> keys) {
        List<V> records = new ArrayList<V>(keys.size());

        for (K key : keys)
            records.add(require(key));

        return records;
    }

    @Override public default Collection<V> fetch(Collection<K> keys) {
        Collection<V> records = new ArrayList<V>(keys.size());

        for (K key : keys) {
            V record = fetch(key);

            if (record != null)
                records.add(record);
        }

        return records;
    }
}
