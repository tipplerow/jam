
package jam.data;

import java.util.Set;
import java.util.TreeSet;

import jam.math.DoubleComparator;
import jam.matrix.JamMatrix;

/**
 * Represents a numeric matrix where elements are accessed by row and
 * column key strings rather than integer indexes.
 *
 * <p>The row and column keys are fixed at the time of creation, but
 * the element values may change.  As with the {@link java.util.Set}
 * class, the order of the keys is immaterial.
 */
public final class DataMatrix {
    private final JamMatrix   elements;
    private final VectorIndex rowIndex;
    private final VectorIndex colIndex;

    /**
     * Creates a new data matrix with fixed keys and assigns each
     * element to zero.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     */
    public DataMatrix(Set<String> rowKeys, Set<String> colKeys) {
        this(rowKeys, colKeys, 0.0);
    }

    /**
     * Creates a new data matrix with fixed keys and assigns each
     * element to the same value.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @param fill the value to assign each element.
     */
    public DataMatrix(Set<String> rowKeys, Set<String> colKeys, double fill) {
        this.rowIndex = new VectorIndex(rowKeys);
        this.colIndex = new VectorIndex(colKeys);
        this.elements = new JamMatrix(rowKeys.size(), colKeys.size(), fill);
    }

    /**
     * Returns the keys of the columns in this data matrix.
     *
     * @return the keys of the columns in this data matrix.
     */
    public Set<String> columnKeys() {
        return new TreeSet<String>(colIndex.keys());
    }

    /**
     * Identifies keys contained in this data matrix.
     *
     * @param rowKey the row key to examine.
     *
     * @param colKey the column key to examine.
     *
     * @return {@code true} iff this data matrix contains an element
     * with the specified keys.
     */
    public boolean contains(String rowKey, String colKey) {
        return containsRow(rowKey) && containsColumn(colKey);
    }

    /**
     * Identifies rows contained in this data matrix.
     *
     * @param rowKey the row key to examine.
     *
     * @return {@code true} iff this data matrix contains a row with
     * the specified key.
     */
    public boolean containsRow(String rowKey) {
        return rowIndex.contains(rowKey);
    }

    /**
     * Identifies columns contained in this data matrix.
     *
     * @param colKey the column key to examine.
     *
     * @return {@code true} iff this data matrix contains a column
     * with the specified key.
     */
    public boolean containsColumn(String colKey) {
        return colIndex.contains(colKey);
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
     * @throws IllegalArgumentException unless this data matrix
     * contains the specified element.
     */
    public double get(String rowKey, String colKey) {
        return elements.get(rowIndex.indexOf(rowKey), colIndex.indexOf(colKey));
    }

    /**
     * Returns the number of columns in this data matrix.
     *
     * @return the number of columns in this data matrix.
     */
    public int ncol() {
        return elements.ncol();
    }

    /**
     * Returns the number of rows in this data matrix.
     *
     * @return the number of rows in this data matrix.
     */
    public int nrow() {
        return elements.nrow();
    }

    /**
     * Returns the keys of the rows in this data matrix.
     *
     * @return the keys of the rows in this data matrix.
     */
    public Set<String> rowKeys() {
        return new TreeSet<String>(rowIndex.keys());
    }

    /**
     * Assigns a new value to an element indexed by row and column
     * key.
     *
     * @param rowKey the row key of the element to set.
     *
     * @param colKey the column key of the element to set.
     *
     * @param value the value to assign.
     *
     * @throws IllegalArgumentException unless this data matrix
     * contains the specified element.
     */
    public void set(String rowKey, String colKey, double value) {
        elements.set(rowIndex.indexOf(rowKey), colIndex.indexOf(colKey), value);
    }

    /**
     * Ensures that this data matrix contains a particular element.
     *
     * @param rowKey the row key to validate.
     *
     * @param colKey the column key to validate.
     *
     * @throws IllegalArgumentException unless this data matrix
     * contains the specified element.
     */
    public void validateKeys(String rowKey, String colKey) {
        rowIndex.validateKey(rowKey);
        colIndex.validateKey(colKey);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DataMatrix) && equalsDataMatrix((DataMatrix) that);
    }

    private boolean equalsDataMatrix(DataMatrix that) {
        //
        // The element ordering may differ, but all key/value pairs
        // must be identical...
        //
        if (this.nrow() != that.nrow())
            return false;

        if (this.ncol() != that.ncol())
            return false;

        for (String rowKey : rowIndex.keys()) {
            if (!that.containsRow(rowKey))
                return false;

            for (String colKey : colIndex.keys()) {
                if (!that.containsColumn(colKey))
                    return false;

                if (DoubleComparator.DEFAULT.NE(this.get(rowKey, colKey), that.get(rowKey, colKey)))
                    return false;
            }
        }

        return true;
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("Data matrices should not be used as keys.");
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String rowKey : rowIndex.keys())
            for (String colKey : colIndex.keys())
                builder.append(formatElement(rowKey, colKey));

        return builder.toString();
    }

    private String formatElement(String rowKey, String colKey) {
        return "(" + rowKey + ", " + colKey + ") => " + get(rowKey, colKey) + System.lineSeparator();
    }
}
