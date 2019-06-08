
package jam.data;

import java.util.List;

import jam.matrix.JamMatrix;

/**
 * Implements the {@code DataMatrix} interface with dense matrix
 * storage.
 */
public final class DenseDataMatrix<ROWTYPE, COLTYPE> extends AbstractDataMatrix<ROWTYPE, COLTYPE> {
    private final JamMatrix elements;

    DenseDataMatrix(List<ROWTYPE> rowKeys,
                    List<COLTYPE> colKeys,
                    JamMatrix     elements,
                    boolean       copyKeys,
                    boolean       copyElements) {
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
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @throws IllegalArgumentException if either key list is empty or
     * contains duplicates.
     */
    public DenseDataMatrix(List<ROWTYPE> rowKeys, List<COLTYPE> colKeys) {
        this(rowKeys, colKeys, new JamMatrix(rowKeys.size(), colKeys.size()), true, false);
    }

    /**
     * Creates a new data matrix with dense matrix storage.
     *
     * @param <ROWTYPE> the runtime type for the row keys.
     *
     * @param <COLTYPE> the runtime type for the column keys.
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
    public static <ROWTYPE, COLTYPE> DenseDataMatrix<ROWTYPE, COLTYPE> create(List<ROWTYPE> rowKeys, List<COLTYPE> colKeys) {
        return new DenseDataMatrix<ROWTYPE, COLTYPE>(rowKeys, colKeys);
    }

    /**
     * Creates a new square data matrix with dense matrix storage.
     *
     * @param <KEYTYPE> the runtime type for the row and column keys.
     *
     * @param keys the row and column keys.
     *
     * @return the new data matrix.
     *
     * @throws IllegalArgumentException if the key list is empty or
     * contains duplicates.
     */
    public static <KEYTYPE> DenseDataMatrix<KEYTYPE, KEYTYPE> square(List<KEYTYPE> keys) {
        return new DenseDataMatrix<KEYTYPE, KEYTYPE>(keys, keys);
    }

    @Override public double get(int rowIndex, int colIndex) {
        return elements.get(rowIndex, colIndex);
    }

    @Override public void set(int rowIndex, int colIndex, double value) {
        elements.set(rowIndex, colIndex, value);
    }
}
