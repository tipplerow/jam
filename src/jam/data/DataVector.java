
package jam.data;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.math.DoubleComparator;
import jam.vector.JamVector;

/**
 * Represents a numeric vector where elements are accessed by key
 * strings rather than integer indexes.
 *
 * <p>The element keys are fixed at the time of creation, but the
 * element values may change.  As with the {@link java.util.Set}
 * class, the order of the keys is immaterial.
 */
public final class DataVector {
    private final JamVector elements;
    private final VectorIndex index;

    /**
     * Creates a new data vector with fixed keys and assigns each
     * element to zero.
     *
     * @param keys the element keys.
     */
    public DataVector(Set<String> keys) {
        this(keys, 0.0);
    }

    /**
     * Creates a new data vector with fixed keys and assigns each
     * element to the same value.
     *
     * @param keys the element keys.
     *
     * @param fill the value to assign each element.
     */
    public DataVector(Set<String> keys, double fill) {
        this.index = new VectorIndex(keys);
        this.elements = new JamVector(keys.size(), fill);
    }

    /**
     * Identifies keys contained in this data vector.
     *
     * @param key the key to examine.
     *
     * @return {@code true} iff this data vector contains an element
     * with the specified key.
     */
    public boolean contains(String key) {
        return index.contains(key);
    }

    /**
     * Returns the value indexed by key.
     *
     * @param key the key of the element to return.
     *
     * @return the value indexed by the specified key.
     *
     * @throws IllegalArgumentException unless this data vector
     * contains the specified element.
     */
    public double get(String key) {
        return elements.get(index.indexOf(key));
    }

    /**
     * Returns the keys for the elements in this data vector.
     *
     * @return the keys for the elements in this data vector.
     */
    public Set<String> keys() {
        return new TreeSet<String>(index.keys());
    }

    /**
     * Returns the number of elements in this data vector.
     *
     * @return the number of elements in this data vector.
     */
    public int length() {
        return elements.length();
    }

    /**
     * Assigns a new value to an element indexed by a key.
     *
     * @param key the key of the element to assign.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless this data vector
     * contains the specified element.
     */
    public void set(String key, double value) {
        elements.set(index.indexOf(key), value);
    }

    /**
     * Ensures that this data vector contains a particular key.
     *
     * @param key the key to validate.
     *
     * @throws IllegalArgumentException unless this data vector
     * contains the specified key.
     */
    public void validateKey(String key) {
        index.validateKey(key);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DataVector) && equalsDataVector((DataVector) that);
    }

    private boolean equalsDataVector(DataVector that) {
        //
        // The element ordering may differ, but all key/value pairs
        // must be identical...
        //
        if (this.length() != that.length())
            return false;

        for (String key : index.keys()) {
            if (!that.contains(key))
                return false;

            if (DoubleComparator.DEFAULT.NE(this.get(key), that.get(key)))
                return false;
        }

        return true;
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("Data vectors should not be used as keys.");
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String key : index.keys())
            builder.append(formatElement(key));

        return builder.toString();
    }

    private String formatElement(String key) {
        return key + " => " + get(key);
    }
}
