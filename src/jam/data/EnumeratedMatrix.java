
package jam.data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jam.matrix.JamMatrix;
import jam.util.EnumUtil;

/**
 * Represents a numeric matrix where elements are accessed by
 * enumerated types rather than integer indexes.
 *
 * <p>The row and column keys are fixed at the time of creation
 * (and include all defined enum values), but the element values 
 * may change.
 */
public final class EnumeratedMatrix<ROWTYPE extends Enum<ROWTYPE>,
                                    COLTYPE extends Enum<COLTYPE>> implements DataMatrix<ROWTYPE, COLTYPE> {
    private final ROWTYPE[] rowKeys;
    private final COLTYPE[] colKeys;
    private final JamMatrix elements;

    private EnumeratedMatrix(Class<ROWTYPE> rowType, Class<COLTYPE> colType, double fill) {
        this.rowKeys  = EnumUtil.values(rowType);
        this.colKeys  = EnumUtil.values(colType);
        this.elements = new JamMatrix(rowKeys.length, colKeys.length, fill);
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

    @Override public int colIndex(COLTYPE colKey) {
        return colKey.ordinal();
    }

    @Override public COLTYPE colKey(int colIndex) {
        return colKeys[colIndex];
    }

    @Override public List<COLTYPE> colKeyList() {
        return List.of(colKeys);
    }

    @Override public Set<COLTYPE> colKeySet() {
        return Set.of(colKeys);
    }

    @Override public double get(int rowIndex, int colIndex) {
        return elements.get(rowIndex, colIndex);
    }

    @Override public int ncol() {
        return elements.ncol();
    }

    @Override public int nrow() {
        return elements.nrow();
    }

    @Override public int rowIndex(ROWTYPE rowKey) {
        return rowKey.ordinal();
    }

    @Override public ROWTYPE rowKey(int rowIndex) {
        return rowKeys[rowIndex];
    }

    @Override public List<ROWTYPE> rowKeyList() {
        return List.of(rowKeys);
    }

    @Override public Set<ROWTYPE> rowKeySet() {
        return Set.of(rowKeys);
    }

    @Override public void set(int rowIndex, int colIndex, double value) {
        elements.set(rowIndex, colIndex, value);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof EnumeratedMatrix) && equalsEnumeratedMatrix((EnumeratedMatrix) that);
    }

    private boolean equalsEnumeratedMatrix(EnumeratedMatrix that) {
        return Arrays.equals(this.rowKeys, that.rowKeys)
            && Arrays.equals(this.colKeys, that.colKeys)
            && this.elements.equals(that.elements);
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("Enumerated matrices should not be used as hash keys.");
    }
}
