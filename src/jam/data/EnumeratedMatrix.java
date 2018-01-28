
package jam.data;

import jam.util.EnumUtil;

/**
 * Represents a numeric matrix where elements are accessed by
 * enumerated types rather than integer indexes.
 *
 * <p>The row and column keys are fixed at the time of creation
 * (and include all defined enum values), but the element values 
 * may change.
 */
public final class EnumeratedMatrix<ROW extends Enum<ROW>, COL extends Enum<COL>> {
    private final DataMatrix elements;

    private EnumeratedMatrix(Class<ROW> rowType, Class<COL> colType, double fill) {
        this.elements = new DataMatrix(EnumUtil.names(rowType), EnumUtil.names(colType), fill);
    }

    /**
     * Creates a enumerated matrix and assigns each element to zero.
     *
     * @param <ROW> the enumerated row type.
     *
     * @param <COL> the enumerated column type.
     *
     * @param rowType the enum class to serve as the row keys.
     *
     * @param colType the enum class to serve as the column keys.
     *
     * @return the new enumerated matrix.
     */
    public static <ROW extends Enum<ROW>, COL extends Enum<COL>> 
        EnumeratedMatrix<ROW, COL> create(Class<ROW> rowType, Class<COL> colType) {
        return create(rowType, colType, 0.0);
    }

    /**
     * Creates a enumerated matrix and assigns each element to the
     * same value.
     *
     * @param <ROW> the enumerated row type.
     *
     * @param <COL> the enumerated column type.
     *
     * @param rowType the enum class to serve as the row keys.
     *
     * @param colType the enum class to serve as the column keys.
     *
     * @param fill the value to assign each element.
     *
     * @return the new enumerated matrix.
     */
    public static <ROW extends Enum<ROW>, COL extends Enum<COL>> 
        EnumeratedMatrix<ROW, COL> create(Class<ROW> rowType, Class<COL> colType, double fill) {
        return new EnumeratedMatrix<ROW, COL>(rowType, colType, fill);
    }

    /**
     * Creates a square enumerated matrix with the same row and colum
     * type and assigns each element to zero.
     *
     * @param <KEY> the enumerated row and column type.
     *
     * @param keyType the enum class to serve as the row keys.
     *
     * @return the new enumerated matrix.
     */
    public static <KEY extends Enum<KEY>> EnumeratedMatrix<KEY, KEY> square(Class<KEY> keyType) {
        return square(keyType, 0.0);
    }

    /**
     * Creates a square enumerated matrix with the same row and colum
     * type and assigns each element to the same value.
     *
     * @param <KEY> the enumerated row and column type.
     *
     * @param keyType the enum class to serve as the row keys.
     *
     * @param fill the value to assign each element.
     *
     * @return the new enumerated matrix.
     */
    public static <KEY extends Enum<KEY>> EnumeratedMatrix<KEY, KEY> square(Class<KEY> keyType, double fill) {
        return create(keyType, keyType, fill);
    }

    /**
     * Returns the value of an element indexed by row and column enum
     * keys.
     *
     * @param rowKey the row key of the element to return.
     *
     * @param colKey the column key of the element to return.
     *
     * @return the value indexed by the specified enum keys.
     */
    public double get(ROW rowKey, COL colKey) {
        return elements.get(rowKey.name(), colKey.name());
    }

    /**
     * Returns the number of columns in this enumerated matrix.
     *
     * @return the number of columns in this enumerated matrix.
     */
    public int ncol() {
        return elements.ncol();
    }

    /**
     * Returns the number of rows in this enumerated matrix.
     *
     * @return the number of rows in this enumerated matrix.
     */
    public int nrow() {
        return elements.nrow();
    }

    /**
     * Assigns a new value to an element indexed by row and column
     * enum key.
     *
     * @param rowKey the row key of the element to set.
     *
     * @param colKey the column key of the element to set.
     *
     * @param value the value to assign.
     */
    public void set(ROW rowKey, COL colKey, double value) {
        elements.set(rowKey.name(), colKey.name(), value);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof EnumeratedMatrix) && equalsEnumeratedMatrix((EnumeratedMatrix) that);
    }

    private boolean equalsEnumeratedMatrix(EnumeratedMatrix that) {
        return this.elements.equals(that.elements);
    }

    @Override public int hashCode() {
        return elements.hashCode();
    }

    @Override public String toString() {
        return elements.toString();
    }
}
