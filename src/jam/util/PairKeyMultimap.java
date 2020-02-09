
package jam.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import jam.lang.ObjectFactory;

/**
 * Implements an indexed table of values with two keys that allows
 * multiple entries for all key pairs.
 */
public final class PairKeyMultimap<K1, K2, V> {
    private final Map<K1, Multimap<K2, V>> outerMap;
    private final ObjectFactory<Multimap<K2, V>> innerMapFactory;

    private PairKeyMultimap(Map<K1, Multimap<K2, V>> outerMap, ObjectFactory<Multimap<K2, V>> innerMapFactory) {
        this.outerMap = outerMap;
        this.innerMapFactory = innerMapFactory;
    }

    /**
     * Creates a new empty map using hash maps for the underlying
     * storage.
     *
     * @param <K1> runtime type for the outer keys.
     *
     * @param <K2> runtime type for the inner keys.
     *
     * @param <V> runtime type for the values.
     *
     * @return a new empty map using hash maps for the underlying
     * storage.
     */
    public static <K1, K2, V> PairKeyMultimap<K1, K2, V> hash() {
        Map<K1, Multimap<K2, V>> outerMap =
            new HashMap<K1, Multimap<K2, V>>();

        ObjectFactory<Multimap<K2, V>> innerMapFactory =
            new ObjectFactory<Multimap<K2, V>>() {
                @Override public Multimap<K2, V> newInstance() {
                    return HashMultimap.create();
                };
            };

        return new PairKeyMultimap<K1, K2, V>(outerMap, innerMapFactory);
    }

    /**
     * Creates a new empty map using tree maps for the underlying
     * storage.
     *
     * @param <K1> runtime type for the outer keys.
     *
     * @param <K2> runtime type for the inner keys.
     *
     * @param <V> runtime type for the values.
     *
     * @return a new empty map using tree maps for the underlying
     * storage.
     */
    public static <K1, K2 extends Comparable, V extends Comparable> PairKeyMultimap<K1, K2, V> tree() {
        Map<K1, Multimap<K2, V>> outerMap =
            new TreeMap<K1, Multimap<K2, V>>();

        ObjectFactory<Multimap<K2, V>> innerMapFactory =
            new ObjectFactory<Multimap<K2, V>>() {
                @Override public Multimap<K2, V> newInstance() {
                    return TreeMultimap.create();
                };
            };

        return new PairKeyMultimap<K1, K2, V>(outerMap, innerMapFactory);
    }

    private Multimap<K2, V> innerMap(K1 key1, boolean create) {
        Multimap<K2, V> innerMap = outerMap.get(key1);

        if (innerMap == null && create) {
            innerMap = innerMapFactory.newInstance();
            outerMap.put(key1, innerMap);
        }

        return innerMap;
    }

    /**
     * Identifies keys contained in this map.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @return {@code true} iff this map contains a value indexed
     * by the specified keys.
     */
    public boolean contains(K1 key1, K2 key2) {
        Multimap<K2, V> innerMap =
            innerMap(key1, false);

        return innerMap != null && innerMap.containsKey(key2);
    }

    /**
     * Fills this map with records according to a user-defined
     * indexing scheme.
     *
     * @param values the records to add to this map.
     *
     * @param outerKey a function that extracts the outer key from
     * each record.
     *
     * @param innerKey a function that extracts the inner key from
     * each record.
     */
    public void fill(Collection<V> values, Function<V, K1> outerKey, Function<V, K2> innerKey) {
        for (V value : values)
            put(outerKey.apply(value), innerKey.apply(value), value);
    }

    /**
     * Returns the values indexed by a pair of keys.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     *
     * @return the values indexed by the specified keys (an empty
     * collection if there are no matching values).
     */
    public Collection<V> get(K1 key1, K2 key2) {
        Multimap<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.get(key2);
        else
            return Collections.emptyList();
    }

    /**
     * Returns a {@code Set} view of the inner keys in this map for
     * a given outer key.
     *
     * <p>The returned set is backed by the underlying map, so changes
     * to the map are reflected in the set, and vice-versa.
     *
     * @param key1 the outer key.
     *
     * @return a {@code Set} view of the inner keys that share the
     * given outer key (an empty set if this map does not contain
     * the outer key).
     */
    public Set<K2> innerKeySet(K1 key1) {
        Multimap<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.keySet();
        else
            return Collections.emptySet();
    }

    /**
     * Returns a {@code Set} view of the outer keys in this map.
     *
     * <p>The returned set is backed by the underlying map, so changes
     * to the map are reflected in the set, and vice-versa.
     *
     * @return a {@code Set} view of the outer keys in this map.
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
     */
    public void put(K1 key1, K2 key2, V value) {
        innerMap(key1, true).put(key2, value);
    }

    /**
     * Removes values from this map.
     *
     * @param key1 the first (outer) key.
     *
     * @param key2 the second (inner) key.
     */
    public void remove(K1 key1, K2 key2) {
        Multimap<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            innerMap.removeAll(key2);
    }

    /**
     * Returns the number of entries in this table.
     *
     * @return the number of entries in this table.
     */
    public int size() {
        int size = 0;

        for (K1 key1 : outerKeySet())
            size += outerMap.get(key1).size();

        return size;
    }

    /**
     * Returns a read-only {@code Set} view of the inner keys in this
     * map for a given outer key.
     *
     * @param key1 the outer key.
     *
     * @return a read-only {@code Set} view of the inner keys that
     * share the given outer key (or an empty set if this map does
     * not contain the outer key).
     */
    public Set<K2> viewInnerKeys(K1 key1) {
        return Collections.unmodifiableSet(innerKeySet(key1));
    }

    /**
     * Returns a read-only {@code Set} view of the outer keys in this
     * map.
     *
     * @return a read-only {@code Set} view of the outer keys in this
     * map.
     */
    public Set<K1> viewOuterKeys() {
        return Collections.unmodifiableSet(outerKeySet());
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (K1 key1 : outerKeySet())
            for (K2 key2 : innerKeySet(key1))
                for (V value : get(key1, key2))
                    builder.append(String.format("(%s, %s) => %s\n", key1, key2, value));

        return builder.toString();
    }
}
