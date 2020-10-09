
package jam.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.SetMultimap;

/**
 * Wraps an existing {@code SetMultimap} in a multimap view.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the indexed records.
 */
public class MultiWrapper<K, V> implements MultiView<K, V> {
    /**
     * The underlying multimap storage.
     */
    protected final SetMultimap<K, V> map;

    /**
     * Creates a new view over an underlying multimap.
     *
     * @param map the underlying multimap (changes to the multimap
     * will be reflected in the view).
     */
    protected MultiWrapper(SetMultimap<K, V> map) {
        this.map = map;
    }

    /**
     * Wraps a multimap in a view; subsequent changes to the multimap
     * will be reflected in the view.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param map the multimap to view.
     *
     * @return a multimap view backed by the specified map.
     */
    public static <K, V> MultiView<K, V> wrap(SetMultimap<K, V> map) {
        return new MultiWrapper<K, V>(map);
    }

    @Override public int count() {
        return map.size();
    }

    @Override public Collection<V> fetch() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override public Set<V> fetch(K key) {
        return Collections.unmodifiableSet(map.get(key));
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
        return map.toString();
    }
}
