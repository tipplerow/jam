
package jam.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides utility operations on maps.
 */
public final class MapUtil {
    private MapUtil() {}

    /**
     * Gets a list of values from a map.
     *
     * @param map the map on which to operate.
     *
     * @param keys the keys of the values to get.
     *
     * @return a list containing the value associated with each key in
     * the input collection.
     */
    public static <K, V> List<V> get(Map<K, V> map, Collection<K> keys) {
        List<V> values = new ArrayList<V>(keys.size());

        for (K key : keys)
            values.add(map.get(key));

        return values;
    }

    /**
     * Groups objects in a collection by an arbitrary attribute.
     *
     * @param <K> the runtime type of the attribute (the key type for
     * the map).
     *
     * @param <V> the runtime type of the objects to group.
     *
     * @param objects the objects to group.
     *
     * @param mapper a function that maps each object to a key value.
     *
     * @return a new map containing the objects in collections keyed
     * by the attribute.
     */
    public static <K, V> Map<K, Collection<V>> group(Collection<V> objects, Function<V, K> mapper) {
        Map<K, Collection<V>> map = new HashMap<K, Collection<V>>();
        group(map, objects, mapper);
        return map;
    }

    /**
     * Groups objects in a collection by an arbitrary attribute.
     *
     * @param <K> the runtime type of the attribute (the key type for
     * the map).
     *
     * @param <V> the runtime type of the objects to group.
     *
     * @param map the map in which to place the grouped objects.
     *
     * @param objects the objects to group.
     *
     * @param mapper a function that maps each object to a key value.
     */
    public static <K, V> void group(Map<K, Collection<V>> map, Collection<V> objects, Function<V, K> mapper) {
        for (V object : objects) {
            K key = mapper.apply(object);

            if (!map.containsKey(key))
                map.put(key, new ArrayList<V>());

            map.get(key).add(object);
        }
    }

    /**
     * Adds mappings to an existing map.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @param map the map to fill.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same length.
     */
    public static <K, V> void putAll(Map<K, V> map, K[] keys, V[] values) {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Keys and values have unequal sizes.");

        for (int index = 0; index < keys.length; ++index)
            map.put(keys[index], values[index]);
    }

    /**
     * Adds mappings to an existing map.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @param map the map to fill.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same size.
     */
    public static <K, V> void putAll(Map<K, V> map, Collection<? extends K> keys, Collection<? extends V> values) {
        if (keys.size() != values.size())
            throw new IllegalArgumentException("Keys and values have unequal sizes.");

        Iterator<? extends K> keyItr = keys.iterator();
        Iterator<? extends V> valItr = values.iterator();

        while (keyItr.hasNext())
            map.put(keyItr.next(), valItr.next());
    }

    /**
     * Combines arrays of keys and values into a {@code HashMap}.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new {@code HashMap} with mappings {@code keys[k] => values[k]}
     * for {@code k} in {@code [0, keys.length)}.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same length.
     */
    public static <K, V> Map<K, V> zipHash(K[] keys, V[] values) {
        Map<K, V> map = new HashMap<K, V>(keys.length);
        putAll(map, keys, values);

        return map;
    }

    /**
     * Combines arrays of keys and values into a {@code HashMap}.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
     *
     * @return a new {@code HashMap} with mappings {@code keys.get(k) => values.get(k)}
     * for {@code k} in {@code [0, keys.size())}.
     *
     * @throws IllegalArgumentException unless the keys and values
     * have the same size.
     */
    public static <K, V> Map<K, V> zipHash(List<K> keys, List<V> values) {
        Map<K, V> map = new HashMap<K, V>(keys.size());
        putAll(map, keys, values);

        return map;
    }
}
