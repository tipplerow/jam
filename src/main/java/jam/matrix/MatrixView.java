
package jam.matrix;

import java.util.Iterator;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;

import jam.math.DoubleComparator;
import jam.vector.VectorView;

/**
 * Presents a read-only view of a floating-point matrix and provides
 * default implementations of many algebraic operations.
 */
public abstract class MatrixView implements NumericMatrix, Iterable<MatrixCursor> {
    /**
     * Returns the number of rows in this matrix.
     *
     * @return the number of rows in this matrix.
     */
    public abstract int nrow();

    /**
     * Returns the number of columns in this matrix.
     *
     * @return the number of columns in this matrix.
     */
    public abstract int ncol();

    /**
     * Returns the element at a specified location.
     *
     * @param row the row index of the element to return.
     *
     * @param col the column index of the element to return.
     *
     * @return the element at the specified location.
     *
     * @throws RuntimeException unless the specified indexes are valid
     * locations.
     */
    public abstract double get(int row, int col);

    /**
     * Returns the element at a specified location.
     *
     * @param cursor a cursor identifying the element location.
     *
     * @return the element at the specified location.
     *
     * @throws RuntimeException unless the specified location is in
     * bounds.
     */
    public double get(MatrixCursor cursor) {
        return get(cursor.row(), cursor.col());
    }

    /**
     * Compares this matrix element-by-element to another, allowing
     * the default tolerance for floating-point precision.
     *
     * @param that matrix against which to compare.
     *
     * @return {@code true} iff this matrix and the input matrix have
     * identical dimensions and all elements are equal within the
     * default floating-point tolerance.
     */
    public boolean equalsMatrix(MatrixView that) {
        return equalsMatrix(that, DoubleComparator.DEFAULT);
    }

    /**
     * Compares this matrix element-by-element to another, allowing a
     * tolerance for floating-point precision.
     *
     * @param that matrix against which to compare.
     *
     * @param tolerance the tolerance to allow.
     *
     * @return {@code true} iff this matrix and the input matrix have
     * identical dimensions and all elements are equal within the
     * given floating-point tolerance.
     */
    public boolean equalsMatrix(MatrixView that, double tolerance) {
        return equalsMatrix(that, new DoubleComparator(tolerance));
    }

    /**
     * Compares this matrix element-by-element to another, allowing a
     * tolerance for floating-point precision.
     *
     * @param that matrix against which to compare.
     *
     * @param comparator the comparator to execute the element
     * equality tests.
     *
     * @return {@code true} iff this matrix and the input matrix have
     * identical dimensions and all elements are equal within the
     * given floating-point tolerance.
     */
    public boolean equalsMatrix(MatrixView that, DoubleComparator comparator) {
        if (this.nrow() != that.nrow())
            return false;

        if (this.ncol() != that.ncol())
            return false;

        for (MatrixCursor cursor : this)
            if (comparator.NE(this.get(cursor), that.get(cursor)))
                return false;

        return true;
    }

    /**
     * Returns the length of the main diagonal.
     *
     * <p>The main diagonal is defined for non-square matrices, in
     * which case its length is {@code min(nrow(), ncol())}.
     *
     * @return the length of the main diagonal.
     */
    public int diagonalLength() {
        return Math.min(nrow(), ncol());
    }

    /**
     * Returns a shallow read-only view of the elements along the main
     * diagonal of this matrix.
     *
     * <p>The main diagonal is defined for non-square matrices, in
     * which case its length is {@code min(nrow(), ncol())}.
     *
     * @return a shallow read-only view of the elements along the main
     * diagonal of this matrix.
     */
    public VectorView viewDiagonal() {
        return new DiagonalView(this);
    }

    /**
     * Returns a shallow read-only view of a row in this matrix.
     *
     * @param row the index of the row to view.
     *
     * @return the view of the {@code kth} row of this matrix: a vector 
     * {@code v} such that {@code v.get(k) == this.get(row, k)}.
     *
     * @throws IllegalArgumentException unless the row index is in
     * bounds.
     */
    public VectorView viewRow(int row) {
        return new RowView(this, row);
    }

    /**
     * Returns a shallow read-only view of the rows in this matrix.
     *
     * @return an array of vector views with element {@code k}
     * containing a view of row {@code k} in this matrix.
     */
    public VectorView[] viewRows() {
        VectorView[] rows = new VectorView[nrow()];

        for (int row = 0; row < nrow(); row++)
            rows[row] = viewRow(row);

        return rows;
    }

    /**
     * Returns a shallow read-only view of a column in this matrix.
     *
     * @param col the index of the column to view.
     *
     * @return the view of the {@code kth} column of this matrix: a vector 
     * {@code v} such that {@code v.get(k) == this.get(col, k)}.
     *
     * @throws IllegalArgumentException unless the column index is in
     * bounds.
     */
    public VectorView viewColumn(int col) {
        return new ColumnView(this, col);
    }

    /**
     * Returns a shallow read-only view of the columns in this matrix.
     *
     * @return an array of vector views with element {@code k}
     * containing a view of column {@code k} in this matrix.
     */
    public VectorView[] viewColumns() {
        VectorView[] cols = new VectorView[ncol()];

        for (int col = 0; col < ncol(); col++)
            cols[col] = viewColumn(col);

        return cols;
    }

    /**
     * Identifies square matrices.
     *
     * @return {@code true} iff this is a square matrix.
     */
    public boolean isSquare() {
        return isSquare(nrow(), ncol());
    }

    /**
     * Identifies square matrix dimensions.
     *
     * @param nrow the number of rows in question.
     *
     * @param ncol the number of columns in question.
     *
     * @return {@code true} iff the row and column dimensions define a
     * square matrix.
     */
    public static boolean isSquare(int nrow, int ncol) {
        return nrow == ncol;
    }

    /**
     * Identifies (square) symmetric matrices.
     *
     * @return {@code true} iff this is a (square) symmetric matrix
     * within the default floating-point tolerance.
     */
    public boolean isSymmetric() {
        if (!isSquare())
            return false;

        for (int row = 0; row < nrow() - 1; ++row)
            for (int col = row + 1; col < ncol(); ++col)
                if (DoubleComparator.DEFAULT.NE(get(row, col), get(col, row)))
                    return false;

        return true;
    }

    /**
     * Identifies elements on the main diagonal of a matrix.
     *
     * @param row the row index of the element.
     *
     * @param col the column index of the element.
     *
     * @return {@code true} iff the row and column indexes are
     * identical and fall within the dimensions of this matrix.
     *
     * @throws IndexOutOfBoundsException unless the row index is in
     * the valid range {@code [0, nrow())} and the column index is in
     * the valid range {@code [0, ncol())}.
     */
    public boolean onDiagonal(int row, int col) {
        validateIndex(row, col);
        return row == col;
    }

    /**
     * Validates a matrix element index.
     *
     * @param row the row index to validate.
     *
     * @param col the column index to validate.
     *
     * @throws IndexOutOfBoundsException unless the row index is in
     * the valid range {@code [0, nrow())} and the column index is in
     * the valid range {@code [0, ncol())}.
     */
    public void validateIndex(int row, int col) {
        validateRow(row);
        validateColumn(col);
    }

    /**
     * Validates a matrix row index.
     *
     * @param row the row index to validate.
     *
     * @throws IndexOutOfBoundsException unless the row index is in
     * the valid range {@code [0, nrow())}.
     */
    public void validateRow(int row) {
        if (row < 0 || row >= nrow())
            throw new IndexOutOfBoundsException(String.format("Row index [%d] out of bounds: [0, %d).", row, nrow()));
    }

    /**
     * Validates a matrix column index.
     *
     * @param col the column index to validate.
     *
     * @throws IndexOutOfBoundsException unless the column index is in
     * the valid range {@code [0, ncol())}.
     */
    public void validateColumn(int col) {
        if (col < 0 || col >= ncol())
            throw new IndexOutOfBoundsException(String.format("Column index [%d] out of bounds: [0, %d).", col, ncol()));
    }

    /**
     * Validates a matrix operand in element-by-element algebraic
     * operations (e.g., matrix addition and subtraction).
     *
     * @param operand the operand to validate.
     *
     * @throws IllegalArgumentException unless the operand and this
     * matrix have identical dimensions.
     */
    public void validateEBE(MatrixView operand) {
        if (this.nrow() != operand.nrow())
            throw new IllegalArgumentException("Row length mismatch.");

        if (this.ncol() != operand.ncol())
            throw new IllegalArgumentException("Column length mismatch.");
    }

    /**
     * Validates a (right) vector factor in a matrix multiplication.
     *
     * @param factor the factor to validate.
     *
     * @throws IllegalArgumentException unless the factor length
     * matches the row length (number of columns) in this matrix.
     */
    public void validateFactor(VectorView factor) {
        if (this.ncol() != factor.length())
            throw new IllegalArgumentException("Factor dimension mismatch.");
    }

    /**
     * Validates a (right) matrix factor in a matrix multiplication.
     *
     * @param factor the factor to validate.
     *
     * @throws IllegalArgumentException unless the number of rows in
     * the factor matches the number of columns in this matrix.
     */
    public void validateFactor(MatrixView factor) {
        if (this.ncol() != factor.nrow())
            throw new IllegalArgumentException("Factor dimension mismatch.");
    }

    /**
     * Ensures that this is a square matrix.
     *
     * @throws IllegalArgumentException unless this is a square matrix.
     */
    public void validateSquare() {
        validateSquare(nrow(), ncol());
    }

    /**
     * Ensures that matrix dimensions define a square matrix.
     *
     * @param nrow the number of rows in question.
     *
     * @param ncol the number of columns in question.
     *
     * @throws IllegalArgumentException unless the input dimensions
     * define a square matrix.
     */
    public static void validateSquare(int nrow, int ncol) {
        if (!isSquare(nrow, ncol))
            throw new IllegalArgumentException("Non-square matrix.");
    }

    /**
     * Wraps a bare matrix in a read-only matrix view.
     *
     * <p>The view is a shallow wrapper around the underlying matrix,
     * so changes to the bare matrix will be reflected in the view.
     * Therefore, this construction is most appropriate for matrix
     * views returned from functions that have created new matrices.
     *
     * @param matrix the matrix to wrap.
     *
     * @return a matrix view of the input matrix.
     *
     * @throws IllegalArgumentException if the input matrix is ragged
     * (not rectangular).
     */
    public static MatrixView wrap(double[][] matrix) {
        return new MatrixWrapper(matrix);
    }

    /**
     * Returns an Apache Commons {@code RealMatrix} representation of
     * this matrix.
     *
     * @return an Apache Commons {@code RealMatrix} representation of
     * this matrix.
     */
    public RealMatrix toRealMatrix() {
        return new BlockRealMatrix(toNumeric());
    }

    @Override public double[][] toNumeric() {
        double[][] result = new double[nrow()][];

        for (int irow = 0; irow < nrow(); irow++) {
            double[] rowi = new double[ncol()];
            result[irow]  = rowi;

            for (int jcol = 0; jcol < ncol(); jcol++)
                rowi[jcol] = get(irow, jcol);
        }

        return result;
    }

    /**
     * Returns an {@code Iterator} that will traverse all elements in
     * this matrix in row-major order.
     *
     * <p>For example, the following code will compute the sum of all
     * elements in a matrix:
     * <pre>
         double total = 0.0;
     
         for (MatrixCursor cursor : view)
             total += view.get(cursor);
     * </pre>
     *
     * @return an {@code Iterator} that will traverse all elements in
     * this matrix in row-major order.
     */
    @Override public Iterator<MatrixCursor> iterator() {
        return new MatrixCursorIterator(this);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof MatrixView) && equalsMatrix((MatrixView) that);
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder("[");

        if (nrow() > 0)
            builder.append(viewRow(0));

        for (int row = 1; row < nrow(); row++) {
            builder.append(",");
            builder.append(System.lineSeparator());
            builder.append(viewRow(row));
        }

        builder.append("]");
        return builder.toString();
    }
}
