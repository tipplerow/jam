
package jam.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Implements the {@code MapTable} interface using an in-memory map
 * for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the table records.
 */
public abstract class AbstractMapTable<K, V> implements MapTable<K, V> {
    /**
     * The underlying map storage.
     */
    protected final Map<K, V> backMap;

    /**
     * The function that extracts keys from records.
     */
    protected final Function<V, K> keyFunc;

    /**
     * Creates a new table with an underlying map for storage and a
     * function to extract keys from records.
     *
     * @param backMap the backing map for record storage.
     *
     * @param keyFunc a function to extract keys from records.
     */
    protected AbstractMapTable(Map<K, V> backMap, Function<V, K> keyFunc) {
        this.backMap = backMap;
        this.keyFunc = keyFunc;
    }

    /**
     * Extracts the key from a record.
     *
     * @param record the record of interest.
     *
     * @return the key for the specified record.
     */
    public K getKey(V record) {
        return keyFunc.apply(record);
    }

    @Override public int count() {
        return backMap.size();
    }

    @Override public void delete() {
        backMap.clear();
    }

    @Override public boolean delete(V record) {
        return backMap.remove(getKey(record)) != null;
    }

    @Override public Collection<V> fetch() {
        return Collections.unmodifiableCollection(backMap.values());
    }

    @Override public V fetch(K key) {
        return backMap.get(key);
    }

    @Override public Set<K> keys() {
        return Collections.unmodifiableSet(backMap.keySet());
    }


    @Override public void store(V record) {
        backMap.put(getKey(record), record);
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
        throw new UnsupportedOperationException("Tables are not suitable for hash keys.");
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
