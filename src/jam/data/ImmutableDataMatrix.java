
package jam.data;

import java.util.List;
import java.util.Set;

final class ImmutableDataMatrix<R, C> implements DataMatrix<R, C> {
    private final DataMatrix<R, C> matrix;

    ImmutableDataMatrix(DataMatrix<R, C> matrix) {
        this.matrix = matrix;
    }

    @Override public int colIndex(C colKey) {
        return matrix.colIndex(colKey);
    }

    @Override public C colKey(int colIndex) {
        return matrix.colKey(colIndex);
    }

    @Override public List<C> colKeyList() {
        return matrix.colKeyList();
    }

    @Override public Set<C> colKeySet() {
        return matrix.colKeySet();
    }

    @Override public boolean contains(R rowKey, C colKey) {
        return matrix.contains(rowKey, colKey);
    }

    @Override public boolean containsCol(C colKey) {
        return matrix.containsCol(colKey);
    }

    @Override public boolean containsRow(R rowKey) {
        return matrix.containsRow(rowKey);
    }

    @Override public double get(R rowKey, C colKey) {
        return matrix.get(rowKey, colKey);
    }

    @Override public double get(int rowIndex, int colIndex) {
        return matrix.get(rowIndex, colIndex);
    }

    @Override public int ncol() {
        return matrix.ncol();
    }

    @Override public int nrow() {
        return matrix.nrow();
    }

    @Override public int rowIndex(R rowKey) {
        return matrix.rowIndex(rowKey);
    }

    @Override public R rowKey(int rowIndex) {
        return matrix.rowKey(rowIndex);
    }

    @Override public List<R> rowKeyList() {
        return matrix.rowKeyList();
    }

    @Override public Set<R> rowKeySet() {
        return matrix.rowKeySet();
    }

    @Override public void set(R rowKey, C colKey, double value) {
        throw new UnsupportedOperationException("Value assignment is not supported.");
    }

    @Override public void set(int rowIndex, int colIndex, double value) {
        throw new UnsupportedOperationException("Value assignment is not supported.");
    }

    @Override public boolean equals(Object obj) {
        return matrix.equals(obj);
    }

    @Override public int hashCode() {
        return matrix.hashCode();
    }

    @Override public String toString() {
        return matrix.toString();
    }
}
