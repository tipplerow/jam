
package jam.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a map containing {@link ArrayList} values, which are
 * created on demand by the first call to {@code list(key)}.
 *
 * @param K the type of keys maintained by this map.
 *
 * @param V the type of values maintained by the mapped lists.
 */
public final class ListMap<K, V> extends MapWrapper<K, List<V>> {
    private ListMap(Map<K, List<V>> map) {
        super(map);
    }

    /**
     * Creates a new {@code ListMap} backed by a {@code HashMap}.
     *
     * @param <K> the type of keys maintained by the map.
     *
     * @param <V> the type of values maintained by the mapped lists.
     *
     * @return a new {@code ListMap} backed by a {@code HashMap}.
     */
    public static <K, V> ListMap<K, V> hash() {
        return new ListMap<K, V>(new HashMap<K, List<V>>());
    }

    /**
     * Creates a new {@code ListMap} backed by a {@code TreeMap}.
     *
     * @param <K> the type of keys maintained by the map.
     *
     * @param <V> the type of values maintained by the mapped lists.
     *
     * @return a new {@code ListMap} backed by a {@code TreeMap}.
     */
    public static <K, V> ListMap<K, V> tree() {
        return new ListMap<K, V>(new TreeMap<K, List<V>>());
    }

    /**
     * Concatenates all lists into a single list, in the order
     * returned by the {@code values()} method.
     *
     * @return a single list containing the concatenation of each
     * mapped list.
     */
    public List<V> cat() {
        return ListUtil.cat(values());
    }

    /**
     * Returns the list indexed by a given key; an empty list is
     * created and mapped to the key if the mapping is not already
     * present.
     *
     * @param key the index of the list.
     *
     * @return the list indexed by the given key.
     */
    public List<V> list(K key) {
        List<V> list = map.get(key);

        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }

        return list;
    }
}

