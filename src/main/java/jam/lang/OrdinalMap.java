
package jam.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a collection of ordinal objects accessed by index.
 */
public class OrdinalMap<E extends Ordinal> {
    private final Map<Long, E> map;

    /**
     * Creates a new map from an existing indexed map.
     *
     * @param map a mapping from index to ordinal object.
     */
    protected OrdinalMap(Map<Long, E> map) {
        this.map = map;
    }

    /**
     * Creates a new, empty ordinal map backed by a {@code HashMap}.
     *
     * @return a new, empty ordinal map backed by a {@code HashMap}.
     */
    public static <E extends Ordinal> OrdinalMap<E> hash() {
        return new OrdinalMap<E>(new HashMap<Long, E>());
    }

    /**
     * Creates an ordinal map backed by a {@code HashMap} and
     * populates it with a collection of ordinal objects.
     *
     * @param ordinals the ordinal objects to add to the map.
     *
     * @return an ordinal map backed by a {@code HashMap}
     * containing the specified objects.
     */
    public static <E extends Ordinal> OrdinalMap<E> hash(Collection<? extends E> ordinals) {
        OrdinalMap<E> hashMap = hash();
        hashMap.addAll(ordinals);
        return hashMap;
    }

    /**
     * Creates a new, empty ordinal map backed by a {@code TreeMap}.
     *
     * @return a new, empty ordinal map backed by a {@code TreeMap}.
     */
    public static <E extends Ordinal> OrdinalMap<E> tree() {
        return new OrdinalMap<E>(new TreeMap<Long, E>());
    }

    /**
     * Creates an ordinal map backed by a {@code TreeMap} and
     * populates it with a collection of ordinal objects.
     *
     * @param ordinals the ordinal objects to add to the map.
     *
     * @return an ordinal map backed by a {@code TreeMap}
     * containing the specified objects.
     */
    public static <E extends Ordinal> OrdinalMap<E> tree(Collection<? extends E> ordinals) {
        OrdinalMap<E> treeMap = tree();
        treeMap.addAll(ordinals);
        return treeMap;
    }

    /**
     * Adds an ordinal object to this map (unless it is already
     * present).
     *
     * @param ordinal the ordinal object to add.
     *
     * @return {@code true} iff this map was modified by the operation
     * (it did not already contain the object).
     */
    public boolean add(E ordinal) {
        return map.put(ordinal.getIndex(), ordinal) == null;
    }

    /**
     * Adds ordinal objects to this map.
     *
     * @param ordinals the ordinal objects to add.
     */
    public void addAll(Collection<? extends E> ordinals) {
        for (E ordinal : ordinals)
            add(ordinal);
    }

    /**
     * Determines whether this map contains an ordinal object with a
     * specified index.
     *
     * @param index the index of interest.
     *
     * @return {@code true} iff this map contains an ordinal object
     * with the specified index.
     */
    public boolean contains(long index) {
        return map.containsKey(index);
    }

    /**
     * Determines whether this map contains an ordinal object.
     *
     * @param ordinal the ordinal object of interest.
     *
     * @return {@code true} iff this map contains the specified
     * object.
     */
    public boolean contains(E ordinal) {
        return contains(ordinal.getIndex());
    }

    /**
     * Returns the ordinal object with a specified index.
     *
     * @param index the index of interest.
     *
     * @return the ordinal object with the specified index (or
     * {@code null} if there is no matching object).
     */
    public E get(long index) {
        return map.get(index);
    }

    /**
     * Removes an ordinal object from this map.
     *
     * @param index the index of the object to remove.
     *
     * @return {@code true} iff this map contained an object with the
     * specified index and that object was removed.
     */
    public boolean remove(long index) {
        return map.remove(index) != null;
    }

    /**
     * Removes an ordinal object from this map.
     *
     * @param ordinal the object to remove.
     *
     * @return {@code true} iff this map contained the object and it
     * was removed.
     */
    public boolean remove(E ordinal) {
        return remove(ordinal.getIndex());
    }

    /**
     * Removes ordinal objects from this map.
     *
     * @param ordinals the objects to remove.
     */
    public void removeAll(Collection<E> ordinals) {
        for (E ordinal : ordinals)
            remove(ordinal);
    }

    /**
     * Returns the ordinal object with a specified index.
     *
     * @param index the index of interest.
     *
     * @return the ordinal object with the specified index.
     *
     * @throws RuntimeException unless this map contains an object
     * with the specifed index.
     */
    public E require(long index) {
        E ordinal = get(index);

        if (ordinal != null)
            return ordinal;
        else
            throw JamException.runtime("No matching object for index: [%d].", index);
    }

    /**
     * Returns a collection view of the ordinal objects in this map.
     * The collection is backed by the underlying map, so changes to
     * the returned collection will be reflected in the map.
     *
     * @return a collection view of the ordinal objects in this map.
     */
    public Collection<E> values() {
        return map.values();
    }
}
