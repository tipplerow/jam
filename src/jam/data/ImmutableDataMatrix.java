
package jam.data;

import java.util.List;
import java.util.Set;

final class ImmutableDataMatrix<ROWTYPE, COLTYPE> implements DataMatrix<ROWTYPE, COLTYPE> {
    private final DataMatrix<ROWTYPE, COLTYPE> matrix;

    ImmutableDataMatrix(DataMatrix<ROWTYPE, COLTYPE> matrix) {
        this.matrix = matrix;
    }

    @Override public int colIndex(COLTYPE colKey) {
        return matrix.colIndex(colKey);
    }

    @Override public COLTYPE colKey(int colIndex) {
        return matrix.colKey(colIndex);
    }

    @Override public List<COLTYPE> colKeyList() {
        return matrix.colKeyList();
    }

    @Override public Set<COLTYPE> colKeySet() {
        return matrix.colKeySet();
    }

    @Override public boolean contains(ROWTYPE rowKey, COLTYPE colKey) {
        return matrix.contains(rowKey, colKey);
    }

    @Override public boolean containsCol(COLTYPE colKey) {
        return matrix.containsCol(colKey);
    }

    @Override public boolean containsRow(ROWTYPE rowKey) {
        return matrix.containsRow(rowKey);
    }

    @Override public double get(ROWTYPE rowKey, COLTYPE colKey) {
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

    @Override public int rowIndex(ROWTYPE rowKey) {
        return matrix.rowIndex(rowKey);
    }

    @Override public ROWTYPE rowKey(int rowIndex) {
        return matrix.rowKey(rowIndex);
    }

    @Override public List<ROWTYPE> rowKeyList() {
        return matrix.rowKeyList();
    }

    @Override public Set<ROWTYPE> rowKeySet() {
        return matrix.rowKeySet();
    }

    @Override public void set(ROWTYPE rowKey, COLTYPE colKey, double value) {
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
