
package jam.matrix;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

// Package-scope implementation of a dense floating-point matrix.
//
final class DenseMatrix extends MatrixImpl {
    private final BlockRealMatrix elements;

    DenseMatrix(int nrow, int ncol) {
        this.elements = new BlockRealMatrix(nrow, ncol);
    }

    DenseMatrix(double[][] elements) {
        this.elements = new BlockRealMatrix(elements);
    }

    DenseMatrix(BlockRealMatrix elements) {
        this.elements = elements;
    }

    @Override public MatrixImpl add(int row, int col, double value) {
        elements.addToEntry(row, col, value);
        return this;
    }

    @Override public MatrixImpl copy() {
        return new DenseMatrix(elements.copy());
    }

    @Override public double get(int row, int col) {
        return elements.getEntry(row, col);
    }

    @Override public MatrixImpl inverse() {
        return new DenseMatrix(MatrixUtils.inverse(elements, 1.0E-12).getData());
    }

    @Override public MatrixImpl like(int nrow, int ncol) {
        return new DenseMatrix(nrow, ncol);
    }

    @Override MatrixImpl multiply(int row, int col, double value) {
        elements.multiplyEntry(row, col, value);
        return this;
    }

    @Override public int nrow() {
        return elements.getRowDimension();
    }

    @Override public int ncol() {
        return elements.getColumnDimension();
    }

    @Override public MatrixImpl set(int row, int col, double value) {
        elements.setEntry(row, col, value);
        return this;
    }
}
