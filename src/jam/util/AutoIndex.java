
package jam.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Maintains an auto-incrementing ordinal index for a collection of
 * objects.
 */
public final class AutoIndex<V> {
    private int nextIndex = 0;
    private final Map<Integer, V> forward = new TreeMap<Integer, V>();
    private final Map<V, Integer> inverse = new HashMap<V, Integer>();

    private AutoIndex(Collection<V> objects) {
        addAll(objects);
    }

    /**
     * Creates a new auto-index and fills it with a series of objects.
     *
     * @param <V> the runtime object type.
     *
     * @param objects the objects to index.
     *
     * @return the new auto-index.
     */
    @SuppressWarnings("unchecked")
    public static <V> AutoIndex<V> create(V... objects) {
        return create(List.of(objects));
    }

    /**
     * Creates a new auto-index and fills it with a collection of
     * objects.
     *
     * @param <V> the runtime object type.
     *
     * @param objects the objects to index.
     *
     * @return the new auto-index.
     */
    public static <V> AutoIndex<V> create(Collection<V> objects) {
        return new AutoIndex<V>(objects);
    }

    /**
     * Adds an object to this auto-index and assigns the next index.
     *
     * @param object the object to add.
     *
     * @return the index that was assigned to the object.
     */
    public int add(V object) {
        int index = nextIndex;
        ++nextIndex;

        // By construction the index should always be unique...
        assert !containsIndex(index);

        if (containsObject(object))
            throw new IllegalArgumentException("Object is already indexed.");

        forward.put(index, object);
        inverse.put(object, index);

        assert forward.size() == inverse.size();
        return index;
    }

    /**
     * Adds a collection of objects to this index and assigns unique
     * indexes to them.
     *
     * @param objects the objects to add.
     */
    public void addAll(Collection<V> objects) {
        for (V object : objects)
            add(object);
    }

    /**
     * Identifies ordinal indexes that are assigned to objects.
     *
     * @param index the ordinal index to examine.
     *
     * @return {@code true} iff the specified index is assigned to an
     * object.
     */
    public boolean containsIndex(int index) {
        return forward.containsKey(index);
    }

    /**
     * Identifies objects contained in this auto-index.
     *
     * @param object the object to examine.
     *
     * @return {@code true} iff this auto-index contains the specified
     * object.
     */
    public boolean containsObject(V object) {
        return inverse.containsKey(object);
    }

    /**
     * Returns the index assigned to an object.
     *
     * @param object the object of interest.
     *
     * @return the index assigned to the specified object.
     *
     * @throws IllegalStateException unless the object is contained in
     * this index.
     */
    public int indexOf(V object) {
        Integer index = inverse.get(object);

        if (index != null)
            return index.intValue();
        else
            throw new IllegalStateException("Missing object.");
    }

    /**
     * Retrieves an object by its index.
     *
     * @param index the index of the object to retrieve.
     *
     * @return the object assigned to the specified index,
     * {@code null} if there is no object assigned to that
     * index.
     */
    public V lookup(int index) {
        return forward.get(index);
    }

    /**
     * Returns the number of objects in this index.
     *
     * @return the number of objects in this index.
     */
    public int size() {
        return forward.size();
    }

    /**
     * Returns a read-only view of the index-object pairs.
     *
     * @return a read-only view of the index-object pairs.
     */
    public Set<Map.Entry<Integer, V>> viewEntries() {
        return Collections.unmodifiableSet(forward.entrySet());
    }
}
