
package jam.data;

import java.util.List;
import java.util.Set;

/**
 * Represents a numeric matrix where elements are accessed by row and
 * column keys (fixed at the time of creation) in addition to integer
 * indexes.
 *
 * @param <R> the runtime type of the row keys.
 *
 * @param <C> the runtime type of the column keys.
 */
public interface DataMatrix<R,C> {
    /**
     * Special index used to indicate that a row or column key is not
     * present in this matrix.
     */
    public static final int KEY_MISSING = -1;

    /**
     * Returns a new data matrix with dense matrix storage.
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
    public static <R,C> DataMatrix<R,C> dense(List<R> rowKeys, List<C> colKeys) {
        return DenseDataMatrix.create(rowKeys, colKeys);
    }

    /**
     * Returns the column index mapped to a given column key.
     *
     * @param colKey the column key to examine.
     *
     * @return the column index mapped to the specified column key, or
     * {@code KEY_MISSING} if this matrix does not contain the column.
     */
    public abstract int colIndex(C colKey);

    /**
     * Returns the key for a given column.
     *
     * @param colIndex a zero-offset column index.
     *
     * @return the key of the specified column.
     *
     * @throws IllegalArgumentException unless the column index lies
     * within this matrix.
     */
    public abstract C colKey(int colIndex);

    /**
     * Returns a read-only list view of the columns in this matrix.
     *
     * @return a read-only list view of the columns in this matrix.
     */
    public abstract List<C> colKeyList();

    /**
     * Returns a read-only set view of the columns in this matrix.
     *
     * @return a read-only set view of the columns in this matrix.
     */
    public abstract Set<C> colKeySet();

    /**
     * Identifies keys contained in this matrix.
     *
     * @param rowKey the row key to examine.
     *
     * @param colKey the column key to examine.
     *
     * @return {@code true} iff this matrix contains an element with
     * the specified keys.
     */
    public default boolean contains(R rowKey, C colKey) {
        return containsRow(rowKey) && containsCol(colKey);
    }

    /**
     * Identifies column keys contained in this matrix.
     *
     * @param colKey the column key to examine.
     *
     * @return {@code true} iff this matrix contains the specified
     * column key.
     */
    public default boolean containsCol(C colKey) {
        return colIndex(colKey) != KEY_MISSING;
    }

    /**
     * Identifies row keys contained in this matrix.
     *
     * @param rowKey the row key to examine.
     *
     * @return {@code true} iff this matrix contains the specified row
     * key.
     */
    public default boolean containsRow(R rowKey) {
        return rowIndex(rowKey) != KEY_MISSING;
    }

    /**
     * Returns the value of an element indexed by row and column key.
     *
     * @param rowKey the row key of the element to return.
     *
     * @param colKey the column key of the element to return.
     *
     * @return the value indexed by the specified keys.
     *
     * @throws IllegalArgumentException unless this matrix contains
     * the specified element.
     */
    public default double get(R rowKey, C colKey) {
        return get(rowIndex(rowKey), colIndex(colKey));
    }

    /**
     * Returns the value of an element identified by row and column
     * position.
     *
     * @param rowIndex the (zero-offset) row position of the element.
     *
     * @param colIndex the (zero-offset) column position of the
     * element.
     *
     * @return the value at the specified position.
     *
     * @throws IllegalArgumentException unless the row and column
     * indexes lie within this matrix.
     */
    public abstract double get(int rowIndex, int colIndex);

    /**
     * Returns an immutable wrapper around this matrix.
     *
     * @return an immutable wrapper around this matrix.
     */
    public default DataMatrix<R,C> immutable() {
        return new ImmutableDataMatrix<R,C>(this);
    }

    /**
     * Returns the number of columns in this matrix.
     *
     * @return the number of columns in this matrix.
     */
    public default int ncol() {
        return colKeyList().size();
    }

    /**
     * Returns the number of rows in this matrix.
     *
     * @return the number of rows in this matrix.
     */
    public default int nrow() {
        return rowKeyList().size();
    }

    /**
     * Returns the row index mapped to a given row key.
     *
     * @param rowKey the row key to examine.
     *
     * @return the row index mapped to the specified row key, or
     * {@code KEY_MISSING} if this matrix does not contain the row.
     */
    public abstract int rowIndex(R rowKey);

    /**
     * Returns the key for a given row.
     *
     * @param rowIndex a zero-offset row index.
     *
     * @return the key of the specified row.
     *
     * @throws IllegalArgumentException unless the row index lies
     * within this matrix.
     */
    public abstract R rowKey(int rowIndex);

    /**
     * Returns a read-only list view of the rows in this matrix.
     *
     * @return a read-only list view of the rows in this matrix.
     */
    public abstract List<R> rowKeyList();

    /**
     * Returns a read-only set view of the rows in this matrix.
     *
     * @return a read-only set view of the rows in this matrix.
     */
    public abstract Set<R> rowKeySet();

    /**
     * Assigns a new value to an element indexed by row and column
     * key (optional operation).
     *
     * @param rowKey the row key of the element to set.
     *
     * @param colKey the column key of the element to set.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless this matrix contains
     * the specified element.
     *
     * @throws UnsupportedOperationException if this is an
     * unmodifiable matrix.
     */
    public default void set(R rowKey, C colKey, double value) {
        set(rowIndex(rowKey), colIndex(colKey), value);
    }

    /**
     * Assigns a new value to an element indexed by row and column
     * position (optional operation).
     *
     * @param rowIndex the (zero-offset) row position of the element
     * to set.
     *
     * @param colIndex the (zero-offset) column position of the
     * element to set.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless the row and column
     * indexes lie within this matrix.
     *
     * @throws UnsupportedOperationException if this is an
     * unmodifiable matrix.
     */
    public abstract void set(int rowIndex, int colIndex, double value);
}
