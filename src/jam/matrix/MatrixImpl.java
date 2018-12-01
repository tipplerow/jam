
package jam.matrix;

import jam.vector.JamVector;
import jam.vector.VectorView;

// Package-scope base class for concrete implementations of matrix
// storage and arithmetic operations.
//
abstract class MatrixImpl extends MatrixView {
    // Creates a deep copy of this matrix.
    abstract MatrixImpl copy();

    // Creates a new implementation instance with the same run-time
    // type and dimensions as this, with all elements initialized to
    // zero.
    MatrixImpl like() {
        return like(nrow(), ncol());
    }

    // Creates a new implementation instance with the same run-time
    // type as this, with all elements initialized to zero.
    abstract MatrixImpl like(int nrow, int ncol);

    // Adds a value to an element.
    //
    // Returns the implementation, usually this same matrix, but
    // potentially a different implementation if the underlying
    // storage scheme must change, e.g., when adding a non-zero
    // value to an off-diagonal element in a diagonal matrix.
    //
    abstract MatrixImpl add(int row, int col, double value);

    // Assigns a value to the element at location [row, col].
    //
    // Returns the implementation, usually this same matrix, but
    // potentially a different implementation if the underlying
    // storage scheme must change, e.g., when assigning a non-zero
    // value to an off-diagonal element in a diagonal matrix.
    //
    abstract MatrixImpl set(int row, int col, double value);

    MatrixImpl set(MatrixCursor cursor, double value) {
        return set(cursor.row(), cursor.col(), value);
    }

    // Computes the sum of this matrix and a scalar multiple of
    // another (this + scalar * that) and returns the result in
    // a new matrix; this matrix is unchanged.
    //
    // The method name comes from the BLAS library function which
    // performs the same operation.
    //
    MatrixImpl daxpy(double scalar, MatrixView that) {
        validateEBE(that);
        MatrixImpl result = like();

        for (MatrixCursor cursor : this)
            result = result.set(cursor, this.get(cursor) + scalar * that.get(cursor));

        return result;
    }

    // Computes the inner product between a row of this matrix and a
    // vector factor.
    //
    // As this is designed to be a helper function, no validation is
    // performed. If the row index is invalid or the vector factor is
    // not congruent, exceptions will still be thrown but the error
    // messages will not explicitly mention a vector multiplication.
    //
    double dot(int row, VectorView factor) {
        double result = 0.0;

        for (int col = 0; col < ncol(); col++)
            result += get(row, col) * factor.getDouble(col);

        return result;
    }

    // Computes the inner product between a row of this matrix and a
    // column of a matrix factor.
    //
    // As this is designed to be a helper function, no validation is
    // performed. If the row index is invalid or the vector factor is
    // not congruent, exceptions will still be thrown but the error
    // messages will not explicitly mention a vector multiplication.
    //
    double dot(int row, int col, MatrixView factor) {
        return dot(row, factor.viewColumn(col));
    }

    MatrixImpl multiply(int row, int col, double value) {
        return set(row, col, get(row, col) * value);
    }

    MatrixImpl plus(double scalar) {
        MatrixImpl result = like();

        for (MatrixCursor cursor : this)
            result = result.set(cursor, scalar + this.get(cursor));

        return result;
    }

    MatrixImpl times(double scalar) {
        MatrixImpl result = like();

        for (MatrixCursor cursor : this)
            result = result.set(cursor, scalar * this.get(cursor));

        return result;
    }

    JamVector times(VectorView factor) {
        validateFactor(factor);
        JamVector result = new JamVector(nrow());

        for (int row = 0; row < nrow(); row++)
            result.set(row, dot(row, factor));

        return result;
    }

    MatrixImpl times(MatrixView factor) {
        validateFactor(factor);
        MatrixImpl result = like(this.nrow(), factor.ncol());

        for (int row = 0; row < result.nrow(); row++)
            for (int col = 0; col < result.ncol(); col++)
                result = result.set(row, col, dot(row, col, factor));

        return result;
    }

    MatrixImpl transpose() {
        MatrixImpl result = like(ncol(), nrow());

        for (MatrixCursor cursor : this)
            result = result.set(cursor.transpose(), this.get(cursor));

        return result;
    }
}
