
package jam.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for creating maps.
 */
public final class MapFactory {
    private MapFactory() {}

    /**
     * Creates an empty hash map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @return an empty hash map.
     */
    public static <K, V> HashMap<K, V> hash() {
        return new HashMap<K, V>();
    }

    /**
     * Creates and populates a hash map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new hash map populated with the given keys and
     * values.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same length.
     */
    public static <K, V> HashMap<K, V> hash(K[] keys, V[] values) {
        return hash(Arrays.asList(keys), Arrays.asList(values));
    }

    /**
     * Creates and populates a hash map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new hash map populated with the given keys and
     * values.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same size.
     */
    public static <K, V> HashMap<K, V> hash(Collection<K> keys, Collection<V> values) {
        HashMap<K, V> map = hash();
        MapUtil.putAll(map, keys, values);
        return map;
    }

    /**
     * Creates an empty tree map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @return an empty tree map.
     */
    public static <K, V> TreeMap<K, V> tree() {
        return new TreeMap<K, V>();
    }

    /**
     * Creates and populates a tree map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new tree map populated with the given keys and
     * values.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same length.
     */
    public static <K, V> TreeMap<K, V> tree(K[] keys, V[] values) {
        return tree(Arrays.asList(keys), Arrays.asList(values));
    }

    /**
     * Creates and populates a tree map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new tree map populated with the given keys and
     * values.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same size.
     */
    public static <K, V> TreeMap<K, V> tree(Collection<K> keys, Collection<V> values) {
        TreeMap<K, V> map = tree();
        MapUtil.putAll(map, keys, values);
        return map;
    }
}
