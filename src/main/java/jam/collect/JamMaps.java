
package jam.collect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Provides utility methods operating on maps.
 */
public final class JamMaps {
    private JamMaps() {}

    /**
     * Fills a map with values whose keys are generated by a function.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param map the map to be filled.
     *
     * @param values the values to add to the map.
     *
     * @param keyFunc a function to generate keys for each value.
     */
    public static <K, V> void fill(Map<K, V> map, Iterable<V> values, Function<V, K> keyFunc) {
        for (V value : values)
            map.put(keyFunc.apply(value), value);
    }

    /**
     * Creates a new hash map and uses a key-generating function to
     * fill the new map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param values the values to add to the map.
     *
     * @param keyFunc a function to generate keys for each value.
     *
     * @return a new hash map filled with the supplied values and
     * mapped with the given key function.
     */
    public static <K, V> HashMap<K, V> hash(Iterable<V> values, Function<V, K> keyFunc) {
        HashMap<K, V> map = new HashMap<K, V>();
        fill(map, values, keyFunc);
        return map;
    }

    /**
     * Creates a new linked hash map and uses a key-generating
     * function to fill the new map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param values the values to add to the map.
     *
     * @param keyFunc a function to generate keys for each value.
     *
     * @return a new linked hash map filled with the supplied values
     * and mapped with the given key function.
     */
    public static <K, V> HashMap<K, V> linkedHash(Iterable<V> values, Function<V, K> keyFunc) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        fill(map, values, keyFunc);
        return map;
    }

    /**
     * Creates a new tree map and uses a key-generating function to
     * fill the new map.
     *
     * @param <K> the runtime type for the map keys.
     *
     * @param <V> the runtime type for the map values.
     *
     * @param values the values to add to the map.
     *
     * @param keyFunc a function to generate keys for each value.
     *
     * @return a new tree map filled with the supplied values and
     * mapped with the given key function.
     */
    public static <K, V> TreeMap<K, V> tree(Iterable<V> values, Function<V, K> keyFunc) {
        TreeMap<K, V> map = new TreeMap<K, V>();
        fill(map, values, keyFunc);
        return map;
    }
}

