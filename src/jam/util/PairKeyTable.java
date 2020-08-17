
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import jam.lang.ObjectFactory;

/**
 * Implements an indexed table of values with two keys.
 */
public abstract class PairKeyTable<K1, K2, V> {
    private final Map<K1, Map<K2, V>> outerMap;
    private final ObjectFactory<Map<K2, V>> innerMapFactory;

    private int size = 0;

    /**
     * Creates a new empty table.
     *
     * @param outerMap the map of inner maps keyed by the outer key.
     *
     * @param innerMapFactory the factory used to create inner maps
     * for each outer key.
     */
    protected PairKeyTable(Map<K1, Map<K2, V>> outerMap, ObjectFactory<Map<K2, V>> innerMapFactory) {
        this.size = 0;
        this.outerMap = outerMap;
        this.innerMapFactory = innerMapFactory;
    }

    private Map<K2, V> innerMap(K1 key1, boolean create) {
        Map<K2, V> innerMap = outerMap.get(key1);

        if (innerMap == null && create) {
            innerMap = innerMapFactory.newInstance();
            outerMap.put(key1, innerMap);
        }

        return innerMap;
    }

    private Set<K1> outerKeySet() {
        return outerMap.keySet();
    }

    private Set<K2> innerKeySet(K1 key1) {
        Map<K2, V> innerMap =
            innerMap(key1, false);

        if (innerMap != null)
            return innerMap.keySet();
        else
            return Collections.emptySet();
    }

    /**
     * Creates a new empty table using {@code HashMap}s for the
     * underlying storage.
     *
     * @param <K1> runtime type for the outer keys.
     *
     * @param <K2> runtime type for the inner keys.
     *
     * @param <V> runtime type for the values.
     *
     * @return a new empty table using {@code HashMap}s for the
     * underlying storage.
     */
    public static <K1, K2, V> PairKeyTable<K1, K2, V> hash() {
        return PairKeyHashTable.create();
    }

    /**
     * Creates a new empty table using {@code TreeMap}s for the
     * underlying storage.
     *
     * @param <K1> runtime type for the outer keys.
     *
     * @param <K2> runtime type for the inner keys.
     *
     * @param <V> runtime type for the values.
     *
     * @return a new empty table using {@code TreeMap}s for the
     * underlying storage.
     */
    public static <K1, K2, V> PairKeyTable<K1, K2, V> tree() {
        return PairKeyTreeTable.create();
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
     * Fills this table with records according to a user-defined
     * indexing scheme.
     *
     * @param values the records to add to this table.
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
     * Identifies empty tables.
     *
     * @return {@code true} iff this table contains no records.
     */
    public boolean isEmpty() {
        return size == 0;
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
        V prev = innerMap(key1, true).put(key2, value);

        if (prev == null)
            ++size;

        return prev;
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
            return remove(innerMap, key2);
        else
            return null;
    }

    private V remove(Map<K2, V> innerMap, K2 key2) {
        V prev = innerMap.remove(key2);

        if (prev != null)
            --size;

        return prev;
    }

    /**
     * Returns the number of entries in this table.
     *
     * @return the number of entries in this table.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the values contained in this table.
     *
     * <p>In the {@code TreeMap} implementation, the values will be
     * ordered by their keys (outer key first, then inner key).  In
     * the {@code HashMap} implementation, the ordering will follow
     * the hash codes of the keys and therefore be difficult to
     * predict.
     *
     * @return the values contained in this table (ordered as
     * described above).
     */
    public List<V> values() {
        List<V> values = new ArrayList<V>();

        for (Map<K2, V> innerMap : outerMap.values())
            values.addAll(innerMap.values());

        return values;
    }

    /**
     * Returns a read-only {@code Set} view of the inner keys in this
     * table for a given outer key.
     *
     * @param key1 the outer key.
     *
     * @return a read-only {@code Set} view of the inner keys that
     * share the given outer key (or an empty set if this table does
     * not contain the outer key).
     */
    public Set<K2> viewInnerKeys(K1 key1) {
        return Collections.unmodifiableSet(innerKeySet(key1));
    }

    /**
     * Returns a read-only {@code Set} view of the outer keys in this
     * table.
     *
     * @return a read-only {@code Set} view of the outer keys in this
     * table.
     */
    public Set<K1> viewOuterKeys() {
        return Collections.unmodifiableSet(outerKeySet());
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (K1 key1 : outerKeySet())
            for (K2 key2 : innerKeySet(key1))
                builder.append(String.format("(%s, %s) => %s\n", key1, key2, get(key1, key2)));

        return builder.toString();
    }
}
