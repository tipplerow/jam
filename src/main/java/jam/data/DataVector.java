
package jam.data;

import java.util.List;
import java.util.Set;

/**
 * Represents a numeric vector where elements are accessed by keys
 * (fixed at the time of creation) in addition to integer indexes.
 *
 * @param K the runtime time of the element keys.
 */
public interface DataVector<K> {
    /**
     * Special index used to indicate that an element key is not
     * present in this vector.
     */
    public static final int KEY_MISSING = -1;

    /**
     * Returns a new data vector with dense vector storage.
     *
     * @param <K> the runtime type for the row keys.
     *
     * @param keys the element keys.
     *
     * @return the new data vector.
     *
     * @throws IllegalArgumentException if the key list is empty or
     * contains duplicates.
     */
    public static <K> DataVector<K> dense(List<K> keys) {
        return DenseDataVector.create(keys);
    }

    /**
     * Returns the element index mapped to a given key.
     *
     * @param key the element key to examine.
     *
     * @return the element index mapped to the specified key, or
     * {@code KEY_MISSING} if this vector does not contain that
     * element.
     */
    public abstract int indexOf(K key);

    /**
     * Returns the key for a given element.
     *
     * @param index a zero-offset element index.
     *
     * @return the key of the specified element.
     *
     * @throws IllegalArgumentException unless the element index lies
     * within this vector.
     */
    public abstract K keyAt(int index);

    /**
     * Returns a read-only list view of the keys in this vector.
     *
     * @return a read-only list view of the keys in this vector.
     */
    public abstract List<K> keyList();

    /**
     * Returns a read-only set view of the key in this vector.
     *
     * @return a read-only set view of the key in this vector.
     */
    public abstract Set<K> keySet();

    /**
     * Identifies keys contained in this vector.
     *
     * @param key the element key to examine.
     *
     * @return {@code true} iff this vector contains an element with
     * the specified key.
     */
    public default boolean contains(K key) {
        return indexOf(key) != KEY_MISSING;
    }

    /**
     * Returns the value of an element indexed by key.
     *
     * @param key the key of the element to return.
     *
     * @return the value indexed by the specified key.
     *
     * @throws IllegalArgumentException unless this vector contains
     * the specified element.
     */
    public default double get(K key) {
        return get(indexOf(key));
    }

    /**
     * Returns the value of an element indexed by position.
     *
     * @param index the (zero-offset) position of the element.
     *
     * @return the value indexed by the specified keys.
     *
     * @throws IllegalArgumentException unless the index lies within
     * this vector.
     */
    public abstract double get(int index);

    /**
     * Returns an immutable wrapper around this vector.
     *
     * @return an immutable wrapper around this vector.
     */
    public default DataVector<K> immutable() {
        return new ImmutableDataVector<K>(this);
    }

    /**
     * Returns the number of elements in this vector.
     *
     * @return the number of elements in this vector.
     */
    public default int length() {
        return keyList().size();
    }

    /**
     * Assigns a new value to an element indexed by key
     * (optional operation).
     *
     * @param key the key of the element to set.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless this vector
     * contains the specified element.
     *
     * @throws UnsupportedOperationException if this is an
     * unmodifiable vector.
     */
    public default void set(K key, double value) {
        set(indexOf(key), value);
    }

    /**
     * Assigns a new value to an element indexed by position
     * (optional operation).
     *
     * @param index the (zero-offset) position of the element.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless the index lies
     * within this vector.
     *
     * @throws UnsupportedOperationException if this is an
     * unmodifiable vector.
     */
    public abstract void set(int index, double value);
}
