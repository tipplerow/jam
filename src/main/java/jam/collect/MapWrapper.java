
package jam.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Wraps an existing {@code Map} in a map view.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the indexed records.
 */
public class MapWrapper<K, V> implements MapView<K, V> {
    /**
     * The underlying map storage.
     */
    protected final Map<K, V> map;

    /**
     * Creates a new view over an underlying map.
     *
     * @param map the underlying map (changes to the map will be
     * reflected in the view).
     */
    protected MapWrapper(Map<K, V> map) {
        this.map = map;
    }

    /**
     * Wraps a map in a map view; subsequent changes to the map will
     * be reflected in the view.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param map the map to view.
     *
     * @return a view backed by the specified map.
     */
    public static <K, V> MapView<K, V> wrap(Map<K, V> map) {
        return new MapWrapper<K, V>(map);
    }

    @Override public int count() {
        return map.size();
    }

    @Override public Collection<V> fetch() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override public V fetch(K key) {
        return map.get(key);
    }

    @Override public Set<K> keys() {
        return Collections.unmodifiableSet(map.keySet());
    }

    /**
     * Throws an {@code UnsupportedOperationException}: use
     * {@code equalsView} for equality tests.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    @Override public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Use MapView::equalsView for equality tests.");
    }

    /**
     * Throws an {@code UnsupportedOperationException}: views are not
     * suitable as hash keys.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    @Override public int hashCode() {
        throw new UnsupportedOperationException("Views are not suitable for hash keys.");
    }

    /**
     * Returns a string containing every mapping in this view.
     *
     * @return a string containing every mapping in this view.
     */
    @Override public String toString() {
        return map.toString();
    }
}
