
package jam.matrix;

import org.apache.commons.math3.linear.BlockRealMatrix;

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

    @Override public MatrixImpl copy() {
        return new DenseMatrix(elements.copy());
    }

    @Override public MatrixImpl like(int nrow, int ncol) {
        return new DenseMatrix(nrow, ncol);
    }

    @Override public int nrow() {
        return elements.getRowDimension();
    }

    @Override public int ncol() {
        return elements.getColumnDimension();
    }

    @Override public double get(int row, int col) {
        return elements.getEntry(row, col);
    }
    
    @Override public MatrixImpl set(int row, int col, double value) {
        elements.setEntry(row, col, value);
        return this;
    }
}
