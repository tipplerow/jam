
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.SetMultimap;

/**
 * Provides a read-only view of a one-to-many mapping from keys to
 * records.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public interface MultiView<K, V> extends RecordView<K, V> {
    /**
     * Wraps a multimap in a view; subsequent changes to the
     * underlying multimap will be reflected in the view.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param map the multimap to view.
     *
     * @return a multimap view backed by a copy of the specified map.
     */
    public static <K, V> MultiView<K, V> wrap(SetMultimap<K, V> map) {
        return MultiWrapper.wrap(map);
    }

    /**
     * Determines whether another view is identical to this view.
     *
     * @param that the other view to compare with this.
     *
     * @return {@code true} iff the other view contains exactly the
     * same key/value mappings as this view.
     */
    public default boolean equalsView(MultiView<K, V> that) {
        int thisCount = this.count();
        int thatCount = that.count();

        if (thisCount != thatCount)
            return false;

        Set<K> thisKeys = this.keys();
        Set<K> thatKeys = that.keys();

        if (!thisKeys.equals(thatKeys))
            return false;

        for (K key : thisKeys) {
            Collection<V> thisValues = this.fetch(key);
            Collection<V> thatValues = that.fetch(key);

            if (!JamCollections.equalsContents(thisValues, thatValues))
                return false;
        }

        return true;
    }

    /**
     * Returns the records indexed by a given key.
     *
     * @param key the key of the records to fetch.
     *
     * @return the records with the specified key (or an empty
     * collection if there are no matching records).
     */
    public abstract Collection<V> fetch(K key);

    @Override public default Collection<V> fetch(Collection<K> keys) {
        Collection<V> records = new ArrayList<V>();

        for (K key : keys)
            records.addAll(fetch(key));

        return records;
    }
}
