
package jam.matrix;

import java.util.Collection;

import jam.lang.JamException;
import jam.math.DoubleComparator;
import jam.math.RandomSequence;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Fixed-size real-valued matrix with basic algebraic operations.
 *
 * <p>Although not explicitly indicated by the method comments, most
 * methods throw {@code RuntimeException}s when passed illegal
 * arguments, e.g., non-positive dimensions or out-of-bounds indexes.
 */
public final class JamMatrix extends MatrixView {
    private MatrixImpl impl;

    private JamMatrix(MatrixImpl impl) {
        this.impl = impl;
    }

    /**
     * Creates a matrix of specified dimensions with all elements
     * initialized to zero.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     */
    public JamMatrix(int nrow, int ncol) {
        this(new DenseMatrix(nrow, ncol));
    }

    /**
     * Creates a matrix of specified length with all elements
     * initialized to a specified value.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @param fill the value to assign to all elements.
     */
    public JamMatrix(int nrow, int ncol, double fill) {
        this(nrow, ncol);
        fill(fill);
    }

    /**
     * Creates a matrix from a bare 2D-array.
     *
     * @param elements the elements to initialize the matrix.
     */
    public JamMatrix(double[][] elements) {
        this(new DenseMatrix(elements));
    }

    /**
     * Creates a matrix as a copy of another view.
     *
     * @param view the matrix view to copy.
     */
    public JamMatrix(MatrixView view) {
        this(new DenseMatrix(view.toNumeric()));
    }

    /**
     * Creates a matrix of specified length with all elements
     * initialized to zero.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @return the matrix of zeros.
     */
    public static JamMatrix zeros(int nrow, int ncol) {
        return new JamMatrix(nrow, ncol);
    }

    /**
     * Creates a matrix of specified length with all elements
     * initialized to one.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @return the matrix of ones.
     */
    public static JamMatrix ones(int nrow, int ncol) {
        return new JamMatrix(nrow, ncol, 1.0);
    }

    /**
     * Creates a new diagonal square matrix.
     *
     * @param elements the diagonal elements.
     *
     * @return a new square matrix with {@code elements} assigned
     * along the diagonal.
     */
    public static JamMatrix diagonal(double... elements) {
        return new JamMatrix(new DiagonalMatrix(elements));
    }

    /**
     * Creates a new diagonal square matrix.
     *
     * @param elements the diagonal elements.
     *
     * @return a new square matrix with {@code elements} assigned
     * along the diagonal.
     */
    public static JamMatrix diagonal(VectorView elements) {
        return new JamMatrix(new DiagonalMatrix(elements.toNumeric()));
    }

    /**
     * Creates a new identity matrix.
     *
     * @param dim the desired number of rows and colums.
     *
     * @return a new identity matrix with dimensionality {@code dim}:
     * a matrix with {@code dim} rows and columns with ones along the
     * diagonal, zeros everywhere else.
     */
    public static JamMatrix identity(int dim) {
        return diagonal(JamVector.ones(dim));
    }

    /**
     * Creates a matrix and populates its elements in row-major order.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @param elements the elements in row-major order.
     *
     * @return the new matrix.
     *
     * @throws IllegalArgumentException unless the number of elements
     * supplied is equal to the number required ({@code nrow * ncol}).
     */
    public static JamMatrix byrow(int nrow, int ncol, double... elements) {
	if (elements.length != nrow * ncol)
	    throw new IllegalArgumentException("Inconsistent dimensions.");

	JamMatrix result = new JamMatrix(nrow, ncol);

	for (int i = 0; i < nrow; i++)
	    for (int j = 0; j < ncol; j++)
		result.set(i, j, elements[j + i * ncol]);

	return result;
    }

    /**
     * Creates a matrix and populates its elements in column-major order.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @param elements the elements in column-major order.
     *
     * @return the new matrix.
     *
     * @throws IllegalArgumentException unless the number of elements
     * supplied is equal to the number required ({@code nrow * ncol}).
     */
    public static JamMatrix bycol(int nrow, int ncol, double... elements) {
	if (elements.length != nrow * ncol)
	    throw new IllegalArgumentException("Inconsistent dimensions.");

	JamMatrix result = new JamMatrix(nrow, ncol);

	for (int i = 0; i < nrow; i++)
	    for (int j = 0; j < ncol; j++)
		result.set(i, j, elements[i + j * nrow]);

	return result;
    }

    /**
     * Creates a new matrix by stacking row vectors (of equal positive
     * length) from top to bottom.
     *
     * @param rows the rows to stack.
     *
     * @return a new matrix where row {@code k} is equal to {@code rows[k]}.
     *
     * @throws IllegalArgumentException unless there is at least one
     * row and all rows have the same (positive) length.
     */
    public static JamMatrix rbind(VectorView... rows) {
        if (rows.length < 1)
            throw new IllegalArgumentException("At least one row is required.");

        if (rows[0].length() < 1)
            throw new IllegalArgumentException("Rows must have positive length.");

        int nrow = rows.length;
        int ncol = rows[0].length();

        JamMatrix result = new JamMatrix(nrow, ncol);

        for (int row = 0; row < nrow; row++)
            result.setRow(row, rows[row]);

        return result;
    }

    /**
     * Creates a new matrix by stacking row vectors (of equal positive
     * length) from top to bottom.
     *
     * @param rows the rows to stack.
     *
     * @return a new matrix where row {@code k} is equal to the {@code k}th 
     * vector returned by the collection iterator.
     *
     * @throws IllegalArgumentException unless there is at least one
     * row and all rows have the same (positive) length.
     */
    public static JamMatrix rbind(Collection<VectorView> rows) {
        return rbind(rows.toArray(new VectorView[0]));
    }

    /**
     * Creates a new matrix by stacking column vectors (of equal positive
     * length) from left to right.
     *
     * @param cols the columns to stack.
     *
     * @return a new matrix where column {@code k} is equal to {@code cols[k]}.
     *
     * @throws IllegalArgumentException unless there is at least one
     * column and all columns have the same (positive) length.
     */
    public static JamMatrix cbind(VectorView... cols) {
        if (cols.length < 1)
            throw new IllegalArgumentException("At least one column is required.");

        if (cols[0].length() < 1)
            throw new IllegalArgumentException("Columns must have positive length.");

        int nrow = cols[0].length();
        int ncol = cols.length;

        JamMatrix result = new JamMatrix(nrow, ncol);

        for (int col = 0; col < ncol; col++)
            result.setColumn(col, cols[col]);

        return result;
    }

    /**
     * Creates a new matrix by stacking column vectors (of equal positive
     * length) from left to right.
     *
     * @param cols the columns to stack.
     *
     * @return a new matrix where column {@code k} is equal to the {@code k}th 
     * vector returned by the collection iterator.
     *
     * @throws IllegalArgumentException unless there is at least one
     * column and all columns have the same (positive) length.
     */
    public static JamMatrix cbind(Collection<VectorView> cols) {
        return cbind(cols.toArray(new VectorView[0]));
    }

    /**
     * Creates a matrix filled with pseudo-random values.
     *
     * @param nrow the number of rows.
     *
     * @param ncol the number of columns.
     *
     * @param sequence the random number sequence.
     *
     * @return a matrix of the specified dimensions filled with
     * pseudo-random values.
     */
    public static JamMatrix random(int nrow, int ncol, RandomSequence sequence) {
        JamMatrix matrix = new JamMatrix(nrow, ncol);

        for (MatrixCursor cursor : matrix)
            matrix.set(cursor, sequence.next());

        return matrix;
    }

    /**
     * Creates a (square) positive definite matrix filled with pseudo-random
     * values.
     *
     * @param N the number of rows and columns.
     *
     * @param sequence the random number sequence.
     *
     * @return a (square) positive definite matrix of the specified
     * dimensions filled with pseudo-random values.
     */
    public static JamMatrix randomPositiveDefinite(int N, RandomSequence sequence) {
        JamMatrix m1 = random(N, N, sequence);
        JamMatrix m2 = m1.transpose();

        return m1.times(m2);
    }

    /**
     * Creates a copy of this matrix.
     *
     * @return a new matrix with the same elements as this matrix.
     */
    public JamMatrix copy() {
	return new JamMatrix(this.impl.copy());
    }

    /**
     * Assigns a single value to all elements.
     *
     * @param value the value to assign.
     */
    public void fill(double value) {
        for (MatrixCursor cursor : this)
            set(cursor, value);
    }

    /**
     * Assigns the value of a specified element.
     *
     * @param row the row index of the element to assign.
     *
     * @param col the column index of the element to assign.
     *
     * @param value the value to assign.
     */
    public void set(int row, int col, double value) {
        //
        // Must update the implementation object, since the underlying
        // storage might change, e.g., from diagonal to sparse...
        // 
        impl = impl.set(row, col, value);
    }

    /**
     * Assigns the value of a specified element.
     *
     * @param cursor a cursor identifying the element location.
     *
     * @param value the value to assign.
     */
    public void set(MatrixCursor cursor, double value) {
        set(cursor.row(), cursor.col(), value);
    }

    /**
     * Assigns values to a row in this matrix.
     *
     * @param rowIndex the index of the row to assign.
     *
     * @param rowValues the row vector to assign.
     *
     * @throws IllegalArgumentException unless the length of the
     * row vector matches the number of columns in this matrix.
     */
    public void setRow(int rowIndex, VectorView rowValues) {
        if (rowValues.length() != ncol())
            throw new IllegalArgumentException("Row vector length mismatch.");

        for (int colIndex = 0; colIndex < ncol(); colIndex++)
            set(rowIndex, colIndex, rowValues.getDouble(colIndex));
    }

    /**
     * Assigns values to a column in this matrix.
     *
     * @param colIndex the index of the column to assign.
     *
     * @param colValues the column vector to assign.
     *
     * @throws IllegalArgumentException unless the length of the
     * column vector matches the number of rows in this matrix.
     */
    public void setColumn(int colIndex, VectorView colValues) {
        if (colValues.length() != nrow())
            throw new IllegalArgumentException("Column vector length mismatch.");

        for (int rowIndex = 0; rowIndex < nrow(); rowIndex++)
            set(rowIndex, colIndex, colValues.getDouble(rowIndex));
    }

    /**
     * Returns the elements along the main diagonal of this matrix.
     *
     * <p>The main diagonal is defined for non-square matrices, in
     * which case its length is {@code min(nrow(), ncol())}.
     *
     * @return a new vector containing the elements along the main
     * diagonal of this matrix.
     */
    public JamVector getDiagonal() {
        return JamVector.copyOf(viewDiagonal());
    }

    /**
     * Extracts a row from this matrix.
     *
     * @param row the index of the row to extract.
     *
     * @return the {@code kth} row of this matrix: a new vector 
     * {@code v} such that {@code v.get(k) == this.get(row, k)}.
     *
     * @throws IllegalArgumentException unless the row index is in
     * bounds.
     */
    public JamVector getRow(int row) {
        return JamVector.copyOf(viewRow(row));
    }

    /**
     * Extracts a column from this matrix.
     *
     * @param col the index of the column to extract.
     *
     * @return the {@code kth} column of this matrix: a new vector
     * {@code v} such that {@code v.get(k) == this.get(k, col)}.
     *
     * @throws IllegalArgumentException unless the column index is in
     * bounds.
     */
    public JamVector getColumn(int col) {
        return JamVector.copyOf(viewColumn(col));
    }

    /**
     * Adds <em>in place</em> a scalar value to each element in this
     * matrix.
     *
     * @param scalar the scalar value to add.
     */
    public void add(double scalar) {
        impl = impl.plus(scalar);
    }

    /**
     * Applies an aggregator to each row in this matrix.
     *
     * @param aggregator the aggregator to apply.
     *
     * @return a vector of length {@code nrow()} with element {@code k}
     * containing the aggregate value from row {@code k}.
     */
    public JamVector aggregateRows(VectorAggregator aggregator) {
        JamVector result = new JamVector(nrow());

        for (int k = 0; k < nrow(); k++)
            result.set(k, aggregator.compute(viewRow(k)));

        return result;
    }

    /**
     * Applies an aggregator to each column in this matrix.
     *
     * @param aggregator the aggregator to apply.
     *
     * @return a vector of length {@code ncol()} with element {@code k}
     * containing the aggregate value from column {@code k}.
     */
    public JamVector aggregateColumns(VectorAggregator aggregator) {
        JamVector result = new JamVector(ncol());

        for (int k = 0; k < ncol(); k++)
            result.set(k, aggregator.compute(viewColumn(k)));

        return result;
    }

    /**
     * Adds another matrix to this matrix <em>in place</em>.
     *
     * @param that the matrix to add.
     *
     * @throws IllegalArgumentException unless the input matrix and
     * this matrix have identical dimensions.
     */
    public void add(MatrixView that) {
        impl = impl.daxpy(1.0, that);
    }

    /**
     * Computes the sum of this matrix and a scalar multiple of
     * another matrix {@code (this + scalar * that)} and returns the
     * result in a new {@code JamMatrix}; this matrix is unchanged.
     *
     * <p>The method name comes from the BLAS library function which
     * performs the same operation.
     *
     * @param scalar the scalar factor to multiply the input matrix.
     *
     * @param that the matrix operand to add to this matrix.
     *
     * @return a new matrix {@code M} with {@code M.get(i, j) == this.get(i, j) + scalar * that.get(i, j)}.
     */
    public JamMatrix daxpy(double scalar, MatrixView that) {
        return new JamMatrix(impl.daxpy(scalar, that));
    }

    /**
     * Divide <em>in place</em> all elements in this matrix by a
     * scalar value.
     *
     * @param scalar the scalar value to divide each element.
     */
    public void divide(double scalar) {
        multiply(1.0 / scalar);
    }

    /**
     * Creates the dyadic (outer or tensor) product of a vector with
     * itself.
     *
     * @param vector any vector.
     *
     * @return the dyadic (outer or tensor) product of the input
     * vector with itself.
     */
    public static JamMatrix dyad(VectorView vector) {
        return dyad(vector, vector);
    }

    /**
     * Creates the dyadic (outer or tensor) product of two vectors.
     *
     * @param x the first vector in the dyadic product.
     *
     * @param y the second vector in the dyadic product.
     *
     * @return the dyadic (outer or tensor) product of the input
     * vectors: a matrix {@code A} with {@code N} rows and columns
     * (where {@code N} is the length of the input vectors) having
     * {@code A.get(i, j) == x.get(i) * y.get(j)}.
     *
     * @throws IllegalArgumentException unless the input vectors have
     * the same length.
     */
    public static JamMatrix dyad(VectorView x, VectorView y) {
        int D = x.length();

        if (y.length() != D)
            throw new IllegalArgumentException("Vector length mismatch.");

        JamMatrix result = new JamMatrix(D, D);

        for (int row = 0; row < D; row++)
            for (int col = 0; col < D; col++)
                result.set(row, col, x.getDouble(row) * y.getDouble(col));

        return result;
    }

    /**
     * Computes the difference between this matrix and a scalar value
     * and returns the result in a new {@code JamMatrix}; this matrix
     * is unchanged.
     *
     * @param scalar the scalar value to subtract.
     *
     * @return the difference between this matrix and the input value.
     */
    public JamMatrix minus(double scalar) {
        return plus(-scalar);
    }

    /**
     * Computes the difference between this matrix and a read-only
     * matrix view and returns the result in a new {@code JamMatrix};
     * this matrix is unchanged.
     *
     * @param that a read-only view of the matrix to subtract.
     *
     * @return the difference between this matrix and the input view.
     *
     * @throws IllegalArgumentException unless the view and this
     * matrix have identical dimensions.
     */
    public JamMatrix minus(MatrixView that) {
        return daxpy(-1.0, that);
    }

    /**
     * Multiplies <em>in place</em> all elements in this matrix by a
     * scalar value.
     *
     * @param scalar the scalar value to multiply each element.
     */
    public void multiply(double scalar) {
        impl = impl.times(scalar);
    }

    /**
     * Computes the sum of this matrix and a scalar value and returns
     * the result in a new {@code JamMatrix}; this matrix is unchanged.
     *
     * @param scalar the scalar value to add.
     *
     * @return the sum of this matrix and the input value.
     */
    public JamMatrix plus(double scalar) {
        return new JamMatrix(impl.plus(scalar));
    }

    /**
     * Computes the sum of this matrix and a read-only matrix view and
     * returns the result in a new {@code JamMatrix}; this matrix is
     * unchanged.
     *
     * @param that a read-only view of the matrix to add.
     *
     * @return the sum of this matrix and the input view.
     *
     * @throws IllegalArgumentException unless the view and this
     * matrix have identical dimensions.
     */
    public JamMatrix plus(MatrixView that) {
        return daxpy(1.0, that);
    }

    /**
     * Subtracts <em>in place</em> a scalar value from each element in
     * this matrix.
     *
     * @param scalar the scalar value to subtract.
     */
    public void subtract(double scalar) {
        add(-scalar);
    }

    /**
     * Subtracts another matrix from this matrix <em>in place</em>.
     *
     * @param that the matrix to subtract.
     *
     * @throws IllegalArgumentException unless the input matrix and
     * this matrix have identical dimensions.
     */
    public void subtract(MatrixView that) {
        impl = impl.daxpy(-1.0, that);
    }

    /**
     * Computes the product of this matrix and a scalar value and returns
     * the result in a new {@code JamMatrix}; this matrix is unchanged.
     *
     * @param scalar the scalar value to add.
     *
     * @return the product of this matrix and the input value.
     */
    public JamMatrix times(double scalar) {
        return new JamMatrix(impl.times(scalar));
    }

    /**
     * Computes the vector product of this matrix and a vector factor.
     *
     * @param factor the vector factor.
     *
     * @return the product of this matrix and the input factor.
     *
     * @throws IllegalArgumentException unless the length of the
     * factor matches the number of columns in this matrix.
     */
    public JamVector times(VectorView factor) {
        return impl.times(factor);
    }

    /**
     * Computes the product of a vector and a matrix and returns the
     * result in a new {@code JamVector}.
     *
     * @param vector the left vector factor.
     *
     * @param matrix the right matrix factor.
     *
     * @return the vector product.
     *
     * @throws IllegalArgumentException unless the number of rows in
     * the input matrix matches the length of the vector.
     */
    public static JamVector times(VectorView vector, MatrixView matrix) {
        if (matrix.nrow() != vector.length())
            throw new IllegalArgumentException("Incongruent matrix factor.");

        JamVector result = new JamVector(matrix.ncol());

        for (int j = 0; j < matrix.ncol(); ++j)
            result.set(j, JamVector.dot(vector, matrix.viewColumn(j)));

        return result;
    }

    /**
     * Computes the matrix product of this matrix and a matrix factor.
     *
     * @param factor the matrix factor.
     *
     * @return the product of this matrix and the input factor.
     *
     * @throws IllegalArgumentException unless the number of rows in
     * the factor matches the number of columns in this matrix.
     */
    public JamMatrix times(MatrixView factor) {
        return new JamMatrix(impl.times(factor));
    }

    /**
     * Returns the transpose of this matrix; this matrix is unchanged.
     *
     * @return the transpose of this matrix; this matrix is unchanged.
     */
    public JamMatrix transpose() {
        return new JamMatrix(impl.transpose());
    }

    @Override public double get(int row, int col) {
        return impl.get(row, col);
    }
    
    @Override public int nrow() {
        return impl.nrow();
    }

    @Override public int ncol() {
        return impl.ncol();
    }
}
