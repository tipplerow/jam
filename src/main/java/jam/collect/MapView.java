
package jam.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Implements the {@code TableView} interface with storage provided by
 * an in-memory map.
 */
public class MapView<K, V> implements TableView<K, V> {
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
    protected MapView(Map<K, V> map) {
        this.map = map;
    }

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
    public static <K, V> MapView<K, V> wrap(Map<K, V> map) {
        return new MapView<K, V>(map);
    }

    @Override public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override public int count() {
        return map.size();
    }

    @Override public Set<K> keys() {
        return map.keySet();
    }

    @Override public Collection<V> select() {
        return map.values();
    }

    @Override public V select(K key) {
        return map.get(key);
    }

    /**
     * Throws an {@code UnsupportedOperationException}: use
     * {@code equalsView} for equality tests.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    @Override public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Use TableView::equalsView for equality tests.");
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
