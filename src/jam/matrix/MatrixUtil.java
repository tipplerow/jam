
package jam.matrix;

import jam.vector.VectorUtil;

/**
 * Provides utility methods operating on bare floating-point matrices
 * ({@code double[][]} arrays).
 */
public final class MatrixUtil {
    /**
     * Creates a new matrix and assigns all elements to a single
     * value.
     *
     * @param nrow the number of rows in the new matrix.
     *
     * @param ncol the number of colums in the new matrix.
     *
     * @param fill the value to assign to each element.
     *
     * @return the new matrix.
     */
    public static double[][] create(int nrow, int ncol, double fill) {
        double[][] result = new double[nrow][];

        for (int row = 0; row < nrow; row++)
            result[row] = VectorUtil.create(ncol, fill);

        return result;
    }

    /**
     * Copies a matrix.
     *
     * @param matrix the matrix to copy.
     *
     * @return a deep copy of the input matrix.
     */
    public static double[][] copy(double[][] matrix) {
        double[][] result = new double[nrow(matrix)][];

        for (int row = 0; row < nrow(matrix); row++)
            result[row] = VectorUtil.copy(matrix[row]);

        return result;
    }

    /**
     * Compares two bare matrices for equality, allowing for a given
     * floating-point tolerance.
     *
     * @param matrix1 the first matrix to compare.
     *
     * @param matrix2 the second matrix to compare.
     *
     * @param tolerance the maximum difference to allow and still
     * compare elements as equal.
     *
     * @return {@code true} iff the input matrices have the same length
     * and all corresponding elements are equal within the specified
     * tolerance.
     */
    public static boolean equals(double[][] matrix1, double[][] matrix2, double tolerance) {
        if (nrow(matrix1) != nrow(matrix2))
            return false;

        for (int row = 0; row < nrow(matrix1); row++)
            if (!VectorUtil.equals(matrix1[row], matrix2[row], tolerance))
                return false;

        return true;
    }

    /**
     * Identifies rectangular (non-ragged) matrices.
     *
     * @param matrix the matrix to examine.
     *
     * @return {@code true} iff all rows have the same length.
     */
    public static boolean isRectangular(double[][] matrix) {
        for (int row = 1; row < nrow(matrix); row++)
            if (matrix[row].length != matrix[0].length)
                return false;

        return true;
    }

    /**
     * Returns the number of rows in a bare matrix.
     *
     * @param matrix the matrix to examine.
     *
     * @return the number of rows in {@code matrix}.
     */
    public static int nrow(double[][] matrix) {
        return matrix.length;
    }

    /**
     * Returns the number of columns in a rectangular bare matrix.
     *
     * <p>This method <em>does not check</em> whether the input matrix
     * is rectangular; the results for non-rectangular matrices have
     * no meaning.
     *
     * @param matrix the matrix to examine.
     *
     * @return the number of columns in {@code matrix}.
     *
     * @throws RuntimeException if the matrix is empty.
     */
    public static int ncol(double[][] matrix) {
        return matrix[0].length;
    }

    /**
     * Verifies that a bare matrix is rectangular.
     *
     * @param matrix the matrix to examine.
     *
     * @throws IllegalArgumentException unless the input matrix is
     * rectangular.
     */
    public static void validate(double[][] matrix) {
        if (!isRectangular(matrix))
            throw new IllegalArgumentException("Ragged matrix.");
    }
}
