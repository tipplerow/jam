
package jam.data;

import java.util.List;

import jam.matrix.JamMatrix;

/**
 * Implements the {@code DataMatrix} interface with dense matrix
 * storage.
 */
public class DenseDataMatrix<R, C> extends AbstractDataMatrix<R, C> {
    private final JamMatrix elements;

    /**
     * Creates a new data matrix with dense matrix storage initialized
     * to zero.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @throws IllegalArgumentException if either key list is empty or
     * contains duplicates.
     */
    public DenseDataMatrix(List<R> rowKeys, List<C> colKeys) {
        this(rowKeys, colKeys, 0.0);
    }

    /**
     * Creates a new data matrix with dense matrix storage initialized
     * to a common value.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @param fill the value to assign to each element.
     *
     * @throws IllegalArgumentException if either key list is empty or
     * contains duplicates.
     */
    public DenseDataMatrix(List<R> rowKeys, List<C> colKeys, double fill) {
        this(rowKeys, colKeys, new JamMatrix(rowKeys.size(), colKeys.size(), fill), true, false);
    }

    /**
     * Creates a new dense data matrix with fixed row and column keys
     * and mutable (by default) values.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @param elements the element values.
     *
     * @param copyKeys whether or not to create a deep private copy of
     * the input keys.
     *
     * @param copyElements whether or not to create a deep private
     * copy of the input elements.
     */
    public DenseDataMatrix(List<R>   rowKeys,
                           List<C>   colKeys,
                           JamMatrix elements,
                           boolean   copyKeys,
                           boolean   copyElements) {
        super(rowKeys, colKeys, copyKeys);

        if (copyElements)
            this.elements = elements.copy();
        else
            this.elements = elements;

        if (elements.nrow() != rowKeys.size())
            throw new IllegalArgumentException("Row dimensions are incompatible.");

        if (elements.ncol() != colKeys.size())
            throw new IllegalArgumentException("Column dimensions are incompatible.");
    }

    /**
     * Creates a new data matrix with dense matrix storage.
     *
     * @param <R> the runtime type for the row keys.
     *
     * @param <C> the runtime type for the column keys.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @return the new data matrix.
     *
     * @throws IllegalArgumentException if either key list is empty or
     * contains duplicates.
     */
    public static <R, C> DenseDataMatrix<R, C> create(List<R> rowKeys, List<C> colKeys) {
        return new DenseDataMatrix<R, C>(rowKeys, colKeys);
    }

    /**
     * Creates a new square data matrix with dense matrix storage.
     *
     * @param <K> the runtime type for the row and column keys.
     *
     * @param keys the row and column keys.
     *
     * @return the new data matrix.
     *
     * @throws IllegalArgumentException if the key list is empty or
     * contains duplicates.
     */
    public static <K> DenseDataMatrix<K, K> square(List<K> keys) {
        return new DenseDataMatrix<K, K>(keys, keys);
    }

    @Override public double get(int rowIndex, int colIndex) {
        return elements.get(rowIndex, colIndex);
    }

    @Override public void set(int rowIndex, int colIndex, double value) {
        elements.set(rowIndex, colIndex, value);
    }
}
