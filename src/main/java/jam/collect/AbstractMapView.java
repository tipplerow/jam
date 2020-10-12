
package jam.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Implements the {@code MapView} interface using an in-memory map
 * for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the table records.
 */
public abstract class AbstractMapView<K, V> implements MapView<K, V> {
    /**
     * The underlying map storage.
     */
    protected final Map<K, V> backMap;

    /**
     * Creates a new view over an existing map.
     *
     * @param backMap the backing map for record storage.
     */
    protected AbstractMapView(Map<K, V> backMap) {
        this.backMap = backMap;
    }

    @Override public boolean contains(K key) {
        return backMap.containsKey(key);
    }

    @Override public int count() {
        return backMap.size();
    }

    @Override public Set<K> keys() {
        return Collections.unmodifiableSet(backMap.keySet());
    }

    @Override public Collection<V> select() {
        return Collections.unmodifiableCollection(backMap.values());
    }

    @Override public V select(K key) {
        return backMap.get(key);
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
     * Throws an {@code UnsupportedOperationException}: views and
     * tables are not suitable as hash keys.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    @Override public int hashCode() {
        throw new UnsupportedOperationException("Views and tables are not suitable for hash keys.");
    }

    /**
     * Returns a string containing every mapping in this view.
     *
     * @return a string containing every mapping in this view.
     */
    @Override public String toString() {
        return backMap.toString();
    }
}
