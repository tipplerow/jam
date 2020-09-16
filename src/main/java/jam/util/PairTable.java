
package jam.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a <em>many-to-many</em> relationship between two
 * collections of objects.
 *
 * @param <K1> the runtime type of the first key.
 *
 * @param <K2> the runtime type of the second key.
 */
public final class PairTable<K1, K2> {
    private final Multimap<K1, K2> map1 = HashMultimap.create();
    private final Multimap<K2, K1> map2 = HashMultimap.create();

    private PairTable() {
    }

    /**
     * Creates a new, empty pair table.
     *
     * @param <K1> runtime type for the first key.
     *
     * @param <K2> runtime type for the second key.
     *
     * @return a new, empty pair table.
     */
    public static <K1, K2> PairTable<K1, K2> create() {
        return new PairTable<K1, K2>();
    }

    /**
     * Adds a many-to-many relationship to this table (if it is not
     * already present).
     *
     * @param first the first key to add.
     *
     * @param second the second key to add.
     *
     * @return {@code true} iff the relationship was not already
     * present.
     */
    public boolean add(K1 first, K2 second) {
        if (contains(first, second))
            return false;

        map1.put(first, second);
        map2.put(second, first);
        
        return true;
    }

    /**
     * Identifies many-to-many relationships contained in this table.
     *
     * @param first the first key to query.
     *
     * @param second the second key to query.
     *
     * @return {@code true} iff this table contains the specified key
     * pair.
     */
    public boolean contains(K1 first, K2 second) {
        return map1.get(first).contains(second);
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param first the first key to query.
     *
     * @return {@code true} iff this table contains the specified
     * value as the first key in a many-to-many relationship.
     */
    public boolean containsFirst(K1 first) {
        return map1.containsKey(first);
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param second the second key to query.
     *
     * @return {@code true} iff this table contains the specified
     * value as the second key in a many-to-many relationship.
     */
    public boolean containsSecond(K2 second) {
        return map2.containsKey(second);
    }

    /**
     * Retrieves the second keys in the many-to-many relationships
     * involving a given first key.
     *
     * @param first the first key to query.
     *
     * @return an unmodifiable collection containing the second keys
     * mapped to the first.
     */
    public Collection<K2> forFirst(K1 first) {
        return Collections.unmodifiableCollection(map1.get(first));
    }

    /**
     * Retrieves the first keys in the many-to-many relationships
     * involving a given second key.
     *
     * @param second the second key to query.
     *
     * @return an unmodifiable collection containing the second keys
     * mapped to the second.
     */
    public Collection<K1> forSecond(K2 second) {
        return Collections.unmodifiableCollection(map2.get(second));
    }

    /**
     * Identifies empty tables.
     *
     * @return the number of many-to-many relationships in this table.
     */
    public boolean isEmpty() {
        assert map1.isEmpty() == map2.isEmpty();
        return map1.isEmpty();
    }

    /**
     * Removes a many-to-many relationship from this table.
     *
     * @param first the first key to remove.
     *
     * @param second the second  key to remove.
     *
     * @return {@code true} iff the relationship was previously stored
     * in this table.
     */
    public boolean remove(K1 first, K2 second) {
        return map1.remove(first, second) && map2.remove(second, first);
    }

    /**
     * Removes all many-to-many relationships involving a first key.
     *
     * @param first the first key to remove.
     *
     * @return the second keys that were mapped to the first.
     */
    public Collection<K2> removeFirst(K1 first) {
        Collection<K2> secondKeys = map1.removeAll(first);

        for (K2 second : secondKeys)
            map2.remove(second, first);

        assert map1.size() == map2.size();
        return secondKeys;
    }

    /**
     * Removes all many-to-many relationships involving a second key.
     *
     * @param second the second key to remove.
     *
     * @return the first keys that were mapped to the second.
     */
    public Collection<K1> removeSecond(K2 second) {
        Collection<K1> firstKeys = map2.removeAll(second);

        for (K1 first : firstKeys)
            map1.remove(first, second);

        assert map1.size() == map2.size();
        return firstKeys;
    }

    /**
     * Returns the number of many-to-many relationships in this table.
     *
     * @return the number of many-to-many relationships in this table.
     */
    public int size() {
        assert map1.size() == map2.size();
        return map1.size();
    }

    /**
     * Returns a read-only view of the distinct first keys in this
     * table.
     *
     * @return a read-only view of the distinct first keys in this
     * table.
     */
    public Set<K1> viewFirstKeys() {
        return Collections.unmodifiableSet(map1.keySet());
    }

    /**
     * Returns a read-only view of the distinct second keys in this
     * table.
     *
     * @return a read-only view of the distinct second keys in this
     * table.
     */
    public Set<K2> viewSecondKeys() {
        return Collections.unmodifiableSet(map2.keySet());
    }

    @Override public String toString() {
        return map1.toString();
    }
}
