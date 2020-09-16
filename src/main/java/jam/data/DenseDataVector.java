
package jam.data;

import java.util.List;

import jam.vector.JamVector;

/**
 * Implements the {@code DataVector} interface with dense vector
 * storage.
 */
public final class DenseDataVector<K> extends AbstractDataVector<K> {
    private final JamVector elements;

    DenseDataVector(List<K> keys, JamVector elements, boolean copyKeys, boolean copyElements) {
        super(keys, copyKeys);

        if (copyElements)
            this.elements = elements.copy();
        else
            this.elements = elements;

        if (elements.length() != keys.size())
            throw new IllegalArgumentException("Key and element dimensions are incompatible.");
    }

    /**
     * Creates a new data vector with dense vector storage.
     *
     * @param keys the element keys.
     *
     * @throws IllegalArgumentException if the key list is empty or
     * contains duplicates.
     */
    public DenseDataVector(List<K> keys) {
        this(keys, new JamVector(keys.size()), true, false);
    }

    /**
     * Creates a new data vector with dense vector storage.
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
    public static <K> DenseDataVector<K> create(List<K> keys) {
        return new DenseDataVector<K>(keys);
    }

    @Override public double get(int index) {
        return elements.get(index);
    }

    @Override public void set(int index, double value) {
        elements.set(index, value);
    }
}
