
package jam.matrix;

import jam.app.JamLogger;
import jam.math.DoubleComparator;
import jam.vector.VectorUtil;

// Package-scope implementation of a diagonal floating-point matrix.
//
final class DiagonalMatrix extends MatrixImpl {
    private final double[] diagonal;
    private static final DoubleComparator COMPARATOR = DoubleComparator.DEFAULT;

    DiagonalMatrix(int nrow) {
        this.diagonal = new double[nrow];
    }

    DiagonalMatrix(double[] diagonal) {
        this.diagonal = VectorUtil.copy(diagonal);
    }

    @Override public MatrixImpl copy() {
        return new DiagonalMatrix(diagonal);
    }

    @Override public MatrixImpl add(int row, int col, double value) {
        return set(row, col, get(row, col) + value);
    }

    @Override public double get(int row, int col) {
        //
        // onDiagonal also validates the indexes...
        //
        if (onDiagonal(row, col))
            return diagonal[row];
        else
            return 0.0;
    }

    @Override public MatrixImpl like(int nrow, int ncol) {
        validateSquare(nrow, ncol);
        return new DiagonalMatrix(nrow);
    }

    @Override public int nrow() {
        return diagonal.length;
    }

    @Override public int ncol() {
        return diagonal.length;
    }

    @Override public MatrixImpl set(int row, int col, double value) {
        //
        // onDiagonal also validates the indexes...
        //
        if (onDiagonal(row, col)) {
            diagonal[row] = value;
            return this;
        }
        else if (COMPARATOR.isNonZero(value)) {
            //
            // No longer a diagonal matrix, must change the
            // implementation.
            // 
            // Should really be sparse...change when we have a sparse
            // implementation.
            //
            JamLogger.debug("Assigning non-zero off-diagonal element; changing representation...");
            MatrixImpl dense = toDense();

            dense.set(row, col, value);
            return dense;
        }
        else {
            //
            // Assigining a zero value to an off-diagonal element
            // maintains the diagonal structure with no change...
            //
            return this;
        }
    }

    private MatrixImpl toDense() {
        MatrixImpl impl = new DenseMatrix(nrow(), ncol());

        for (int index = 0; index < diagonal.length; index++)
            impl = impl.set(index, index, diagonal[index]);

        return impl;
    }

    @Override public MatrixImpl times(double scalar) {
        MatrixImpl result = like();

        for (int index = 0; index < diagonal.length; index++)
            result.set(index, index, scalar * diagonal[index]);

        return result;
    }

    @Override public MatrixImpl transpose() {
        return copy();
    }
}
