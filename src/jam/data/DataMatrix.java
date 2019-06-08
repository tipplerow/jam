
package jam.data;

import java.util.List;
import java.util.Set;

/**
 * Represents a numeric matrix where elements are accessed by row and
 * column keys (fixed at the time of creation) in addition to integer
 * indexes.
 *
 * @param ROWTYPE the runtime type of the row keys.
 *
 * @param COLTYPE the runtime type of the column keys.
 */
public interface DataMatrix<ROWTYPE, COLTYPE> {
    /**
     * Special index used to indicate that a row or column key is not
     * present in this matrix.
     */
    public static final int KEY_MISSING = -1;

    /**
     * Returns the column index mapped to a given column key.
     *
     * @param colKey the column key to examine.
     *
     * @return the column index mapped to the specified column key, or
     * {@code KEY_MISSING} if this matrix does not contain the column.
     */
    public abstract int colIndex(COLTYPE colKey);

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
    public abstract COLTYPE colKey(int colIndex);

    /**
     * Returns a read-only list view of the columns in this matrix.
     *
     * @return a read-only list view of the columns in this matrix.
     */
    public abstract List<COLTYPE> colKeyList();

    /**
     * Returns a read-only set view of the columns in this matrix.
     *
     * @return a read-only set view of the columns in this matrix.
     */
    public abstract Set<COLTYPE> colKeySet();

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
    public default boolean contains(ROWTYPE rowKey, COLTYPE colKey) {
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
    public default boolean containsCol(COLTYPE colKey) {
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
    public default boolean containsRow(ROWTYPE rowKey) {
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
    public default double get(ROWTYPE rowKey, COLTYPE colKey) {
        return get(rowIndex(rowKey), colIndex(colKey));
    }

    /**
     * Returns the value of an element indexed by row and column
     * position.
     *
     * @param rowIndex the (zero-offset) row position of the element
     * to return.
     *
     * @param colIndex the (zero-offset) column position of the
     * element to return.
     *
     * @return the value indexed by the specified keys.
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
    public default DataMatrix<ROWTYPE, COLTYPE> immutable() {
        return new ImmutableDataMatrix<ROWTYPE, COLTYPE>(this);
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
    public abstract int rowIndex(ROWTYPE rowKey);

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
    public abstract ROWTYPE rowKey(int rowIndex);

    /**
     * Returns a read-only list view of the rows in this matrix.
     *
     * @return a read-only list view of the rows in this matrix.
     */
    public abstract List<ROWTYPE> rowKeyList();

    /**
     * Returns a read-only set view of the rows in this matrix.
     *
     * @return a read-only set view of the rows in this matrix.
     */
    public abstract Set<ROWTYPE> rowKeySet();

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
    public default void set(ROWTYPE rowKey, COLTYPE colKey, double value) {
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
