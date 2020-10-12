
package jam.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.SetMultimap;

/**
 * Implements the {@code MultiView} interface using an in-memory
 * multimap for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the table records.
 */
public abstract class AbstractMultiView<K, V> implements MultiView<K, V> {
    /**
     * The underlying multimap storage.
     */
    protected final SetMultimap<K, V> backMap;

    /**
     * Creates a new view over an existing multimap.
     *
     * @param backMap the backing map for record storage.
     */
    protected AbstractMultiView(SetMultimap<K, V> backMap) {
        this.backMap = backMap;
    }

    @Override public int count() {
        return backMap.size();
    }

    @Override public Collection<V> fetch() {
        return Collections.unmodifiableCollection(backMap.values());
    }

    @Override public Set<V> fetch(K key) {
        return Collections.unmodifiableSet(backMap.get(key));
    }

    @Override public Set<K> keys() {
        return Collections.unmodifiableSet(backMap.keySet());
    }

    /**
     * Throws an {@code UnsupportedOperationException}: use
     * {@code equalsView} for equality tests.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    @Override public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Use MultiView::equalsView for equality tests.");
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
        return backMap.toString();
    }
}
