
package jam.lang;

/**
 * Provides a skeletal implementation for classes that are keyed by a
 * single (immutable) object.
 *
 * <p>Keyed objects have a <em>natural ordering</em> imposed by the
 * natural ordering of their keys, but keyed objects may only be
 * compared to others with the same runtime class even if other
 * subclasses have the same key type.
 */
public abstract class KeyedObject<K extends Comparable<K>> implements Comparable<KeyedObject<K>> {
    /**
     * The immutable key. 
     */
    protected final K key;

    /**
     * Creates a new object with a specific key.
     *
     * @param key the (immutable) object key.
     */
    protected KeyedObject(K key) {
        this.key = key;
    }

    /**
     * Returns the key for this object.
     *
     * @return the key for this object.
     */
    public final K getKey() {
        return key;
    }

    /**
     * Compares the <em>key</em> of another keyed object with the key
     * of this object.
     *
     * <p>Keyed objects with different runtime types should not be
     * stored in the same collections, so there should be no need to
     * compare keyed objects of different runtime types.  Therefore,
     * this method throws a {@link ClassCastException} unless the
     * input object has the same runtime type as this object.
     *
     * @param that the keyed object to compare to this object.
     *
     * @return a negative integer, zero, or positive integer according
     * to whether the <em>key</em> of this object is less than, equal
     * to, or greater than the <em>key</em> of the input object.
     *
     * @throws ClassCastException unless the input object has the same
     * runtime time as this object.
     */
    @Override public int compareTo(KeyedObject<K> that) {
        if (!this.getClass().equals(that.getClass()))
            throw new ClassCastException("Invalid comparison.");

        return this.key.compareTo(that.key);
    }

    /**
     * Implements an equality test for this object.
     *
     * <p>Keyed objects are equal if and only if they have the same
     * runtime class and identical keys. Objects with identical keys
     * but different runtime classes are <em>not</em> equal.
     *
     * @param that the object to test for equality.
     *
     * @return {@code true} 
     */
    @Override public boolean equals(Object that) {
        return this.getClass().equals(that.getClass()) && equalsKeyedObject((KeyedObject) that);
    }

    private boolean equalsKeyedObject(KeyedObject that) {
        return this.key.equals(that.key);
    }

    @Override public int hashCode() {
        return key.hashCode();
    }

    @Override public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), getKey().toString());
    }
}
