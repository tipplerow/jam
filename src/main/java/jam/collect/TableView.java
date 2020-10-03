
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;
import jam.stream.JamStreams;

/**
 * Provides a read-only view of records indexed by a key.
 *
 * @param <K> the runtime type of the table keys.
 *
 * @param <V> the runtime type of the table values.
 */
public interface TableView<K, V> {
    /**
     * Wraps a map in a table view; subsequent changes to the map will
     * be reflected in the view.
     *
     * @param <K> the runtime time for the map keys.
     *
     * @param <V> the runtime time for the map values.
     *
     * @param map the map to view.
     *
     * @return a map view backed by a copy of the specified map.
     */
    public static <K, V> TableView<K, V> wrap(Map<K, V> map) {
        return MapView.wrap(map);
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param key the key to search for.
     *
     * @return {@code true} iff this table contains a record with the
     * specified key.
     */
    public default boolean contains(K key) {
        return select(key) != null;
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param keys the key to search for.
     *
     * @return a list where element {@code k} is {@code true} iff
     * this table contains a record with key {@code keys.get(k)}.
     */
    public default List<Boolean> contains(List<K> keys) {
        return JamStreams.apply(keys, key -> contains(key));
    }

    /**
     * Returns the number of records in this table.
     *
     * @return the number of records in this table.
     */
    public abstract int count();

    /**
     * Determines whether another view is equal to this view.
     *
     * @param that the other view to compare with this.
     *
     * @return {@code true} iff the other view contains exactly the
     * same keys and values as this view (regardless of the backing
     * implementation).
     */
    public default boolean equalsView(TableView<K, V> that) {
        int thisCount = this.count();
        int thatCount = that.count();

        if (thisCount != thatCount)
            return false;

        Set<K> thisKeys = this.keys();
        Set<K> thatKeys = that.keys();

        if (!thisKeys.equals(thatKeys))
            return false;

        for (K key : thisKeys) {
            V thisValue = this.select(key);
            V thatValue = that.select(key);

            if (thatValue == null || !thisValue.equals(thatValue))
                return false;
        }

        return true;
    }

    /**
     * Returns a read-only view of the keys in this table.
     *
     * @return a read-only view of the keys in this table.
     */
    public abstract Set<K> keys();

    /**
     * Returns the record indexed by a given key.
     *
     * @param key the key of the record to select.
     *
     * @return the record with the specified key.
     *
     * @throws RuntimeException if there is no matching record.
     */
    public default V require(K key) {
        V record = select(key);

        if (record != null)
            return record;
        else
            throw JamException.runtime("Missing required record: [%s].", key);
    }

    /**
     * Returns all records in this table.
     *
     * @return all records in this table.
     */
    public abstract Collection<V> select();

    /**
     * Returns the record indexed by a given key.
     *
     * @param key the key of the record to select.
     *
     * @return the record with the specified key (or
     * {@code null} if there is no matching record).
     */
    public abstract V select(K key);

    /**
     * Returns the records indexed by collection of keys.
     *
     * @param keys the keys of the records to select.
     *
     * @return a list containing the records matching the specified key.
     * The matching records are returned in the same order as their keys
     * appear in the input collection, except that {@code null} values
     * are omitted.
     */
    public default List<V> select(Collection<K> keys) {
        List<V> records = new ArrayList<V>(keys.size());

        for (K key : keys) {
            V record = select(key);

            if (record != null)
                records.add(record);
        }

        return records;
    }
}
