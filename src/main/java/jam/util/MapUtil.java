
package jam.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import jam.lang.JamException;

/**
 * Provides utility operations on maps.
 */
public final class MapUtil {
    private MapUtil() {}

    /**
     * Gets a list of values from a map.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
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
     * @param map the map to fill.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
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
     * @param map the map to fill.
     *
     * @param keys the mapping keys.
     *
     * @param values the mapping values.
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
     * Adds a mapping to an existing map and requires the mapping to
     * be unique (either the map does not yet contain the key or the
     * value already mapped to the key is equal to the input value).
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param map the map to fill.
     *
     * @param key the mapping key.
     *
     * @param value the mapping value.
     *
     * @throws RuntimeException if the map already contains the key
     * mapped to a different value.
     */
    public static <K, V> void putUnique(Map<K, V> map, K key, V value) {
        V oldValue = map.put(key, value);

        if (oldValue != null && !value.equals(oldValue))
            throw JamException.runtime("Duplicate key: [%s]", key);
    }

    /**
     * Removes the mappings for a collection of keys.
     *
     * @param <K> the runtime key type.
     *
     * @param <V> the runtime value type.
     *
     * @param map the map from which to remove items.
     *
     * @param keys the keys of the items to remove.
     *
     * @return a list containing the removed items.
     */
    public static <K, V> List<V> removeAll(Map<K, V> map, Collection<? extends K> keys) {
        if (keys instanceof Set)
            return removeSet(map, (Set<? extends K>) keys);
        else
            return removeCollection(map, keys);
    }

    private static <K, V> List<V> removeCollection(Map<K, V> map, Collection<? extends K> keys) {
        List<V> removed = new ArrayList<V>();

        for (K key : keys) {
            V value = map.remove(key);

            if (value != null)
                removed.add(value);
        }

        return removed;
    }

    private static <K, V> List<V> removeSet(Map<K, V> map, Set<? extends K> keys) {
        if (keys.size() < map.size()) {
            //
            // Fewer target keys than map entries: iterate over the
            // target keys...
            //
            return removeCollection(map, keys);
        }
        else {
            //
            // Fewer map entries than target keys: iterate over the
            // map entries...
            //
            List<V> removed = new ArrayList<V>();
            Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();

                if (keys.contains(entry.getKey())) {
                    removed.add(entry.getValue());
                    iterator.remove();
                }
            }

            return removed;
        }
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
