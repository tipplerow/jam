
package jam.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jam.lang.ObjectFactory;

/**
 * Implements an indexed table of values with two keys.
 */
public final class PairKeyTable<K1, K2, V> {
    private final Map<K1, Map<K2, V>> outerMap;
    private final ObjectFactory<Map<K2, V>> innerMapFactory;

    private PairKeyTable(Map<K1, Map<K2, V>> outerMap,
                       ObjectFactory<Map<K2, V>> innerMapFactory) {
        this.outerMap = outerMap;
        this.innerMapFactory = innerMapFactory;
    }

    /**
     * Creates a new empty table using {@code HashMap}s for the
     * underlying storage.
     *
     * @return a new empty table using {@code HashMap}s for the
     * underlying storage.
     */
    public static <K1, K2, V> PairKeyTable<K1, K2, V> hash() {
        Map<K1, Map<K2, V>> outerMap =
            new HashMap<K1, Map<K2, V>>();

        ObjectFactory<Map<K2, V>> innerMapFactory =
            new ObjectFactory<Map<K2, V>>() {
                @Override public Map<K2, V> newInstance() {
                    return new HashMap<K2, V>();
                };
            };

        return new PairKeyTable<K1, K2, V>(outerMap, innerMapFactory);
    }

    /**
     * Creates a new empty table using {@code TreeMap}s for the
     * underlying storage.
     *
     * @return a new empty table using {@code TreeMap}s for the
     * underlying storage.
     */
    public static <K1, K2, V> PairKeyTable<K1, K2, V> tree() {
        Map<K1, Map<K2, V>> outerMap =
            new TreeMap<K1, Map<K2, V>>();

        ObjectFactory<Map<K2, V>> innerMapFactory =
            new ObjectFactory<Map<K2, V>>() {
                @Override public Map<K2, V> newInstance() {
                    return new TreeMap<K2, V>();
                };
            };

        return new PairKeyTable<K1, K2, V>(outerMap, innerMapFactory);
    }

    private Map<K2, V> innerMap(K1 key1, boolean create) {
        Map<K2, V> innerMap = outerMap.get(key1);

        if (innerMap == null && create) {
            innerMap = innerMapFactory.newInstance();
            outerMap.put(key1, innerMap);
        }

        return innerMap;
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @return {@code true} iff this table contains a value indexed
     * by the specified keys.
     */
    public boolean contains(K1 key1, K2 key2) {
        Map<K2, V> innerMap =
            innerMap(key1, false);

        return innerMap != null && innerMap.containsKey(key2);
    }

    /**
     * Returns the value indexed by a pair of keys.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @return the value indexed by the specified keys, or
     * {@code null} if there is no matching value.
     */
    public V get(K1 key1, K2 key2) {
        Map<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.get(key2);
        else
            return null;
    }

    /**
     * Returns a {@code Set} view of the inner keys in this table for
     * a given outer key.
     *
     * <p>The returned set is backed by the underlying map, so changes
     * to the table are reflected in the set, and vice-versa.
     *
     * @param key1 the outer key.
     *
     * @return a {@code Set} view of the inner keys that share the
     * given outer key (an empty set if this table does not contain
     * the outer key).
     */
    public Set<K2> innerKeySet(K1 key1) {
        Map<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.keySet();
        else
            return Collections.emptySet();
    }

    /**
     * Returns a {@code Set} view of the outer keys in this table.
     *
     * <p>The returned set is backed by the underlying map, so changes
     * to the table are reflected in the set, and vice-versa.
     *
     * @return a {@code Set} view of the outer keys in this table.
     */
    public Set<K1> outerKeySet() {
        return outerMap.keySet();
    }

    /**
     * Indexes a value by a pair of keys.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @param value the value to index.
     *
     * @return the value previously indexed by the specified keys,
     * or {@code null} if there was no previous value.
     */
    public V put(K1 key1, K2 key2, V value) {
        return innerMap(key1, true).put(key2, value);
    }

    /**
     * Removes a value from this table.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @return the value indexed by the specified keys (which is now
     * removed), or {@code null} if there was no previous value.
     */
    public V remove(K1 key1, K2 key2) {
        Map<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.remove(key2);
        else
            return null;
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (K1 key1 : outerKeySet())
            for (K2 key2 : innerKeySet(key1))
                builder.append(String.format("(%s, %s) => %s\n", key1, key2, get(key1, key2)));

        return builder.toString();
    }
}
