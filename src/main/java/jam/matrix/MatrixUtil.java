
package jam.matrix;

import jam.dist.RealDistribution;
import jam.math.DoubleComparator;
import jam.math.StatUtil;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

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

        for (int k = 0; k < nrow; ++k)
            result[k] = VectorUtil.create(ncol, fill);

        return result;
    }

    /**
     * Creates a new matrix with randomly sampled elements.
     *
     * @param nrow the number of rows in the new matrix.
     *
     * @param ncol the number of colums in the new matrix.
     *
     * @param distrib the probability distribution from which to
     * sample the elements.
     *
     * @return the new random matrix.
     */
    public static double[][] random(int nrow, int ncol, RealDistribution distrib) {
        double[][] result = new double[nrow][];

        for (int k = 0; k < nrow; ++k)
            result[k] = VectorUtil.random(ncol, distrib);

        return result;
    }

    /**
     * Creates a new square matrix and assigns all elements to a
     * single value.
     *
     * @param N the number of rows and columns in the new matrix.
     *
     * @param fill the value to assign to each element.
     *
     * @return the new matrix.
     */
    public static double[][] square(int N, double fill) {
        return create(N, N, fill);
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

        for (int k = 0; k < nrow(matrix); ++k)
            result[k] = VectorUtil.copy(row(matrix, k));

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
        int N = ncol(matrix, 0);

        for (int k = 1; k < nrow(matrix); ++k)
            if (ncol(matrix, k) != N)
                return false;

        return true;
    }

    private static int ncol(double[][] matrix, int k) {
        return row(matrix, k).length;
    }

    /**
     * Identifies square matrices.
     *
     * @param matrix the matrix to examine.
     *
     * @return {@code true} iff the matrix is square.
     */
    public static boolean isSquare(double[][] matrix) {
        return (nrow(matrix) == ncol(matrix)) && isRectangular(matrix);
    }

    /**
     * Identifies symmetric matrices.
     *
     * @param matrix the matrix to examine.
     *
     * @return {@code true} iff the matrix is symmetric within the
     * default floating-point tolerance.
     */
    public static boolean isSymmetric(double[][] matrix) {
        return isSymmetric(matrix, DoubleComparator.DEFAULT);
    }

    /**
     * Identifies symmetric matrices.
     *
     * @param matrix the matrix to examine.
     *
     * @param tolerance the floating-point comparison tolerance.
     *
     * @return {@code true} iff the matrix is symmetric within the
     * specified tolerance.
     */
    public static boolean isSymmetric(double[][] matrix, double tolerance) {
        return isSymmetric(matrix, new DoubleComparator(tolerance));
    }

    /**
     * Identifies symmetric matrices.
     *
     * @param matrix the matrix to examine.
     *
     * @param comparator the floating-point comparator.
     *
     * @return {@code true} iff the matrix is symmetric within the
     * specified tolerance.
     */
    public static boolean isSymmetric(double[][] matrix, DoubleComparator comparator) {
        if (!isSquare(matrix))
            return false;

        for (int i = 0; i < nrow(matrix); ++i)
            for (int j = i + 1; j < ncol(matrix); ++j)
                if (comparator.NE(matrix[i][j], matrix[j][i]))
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
        return row(matrix, 0).length;
    }

    /**
     * Extracts a single row from a bare matrix.
     *
     * @param matrix the matrix of interest.
     *
     * @param k the index of the row to extract.
     *
     * @return the {@code k}th row of the matrix.
     */
    public static double[] row(double[][] matrix, int k) {
        return matrix[k];
    }

    /**
     * Computes the mean of each row in a matrix.
     *
     * @param matrix the matrix of interest.
     *
     * @return a vector {@code v} with {@code v[k]} containing the
     * mean value in row {@code k}.
     */
    public static double[] rowMeans(double[][] matrix) {
        double[] means = new double[nrow(matrix)];

        for (int k = 0; k < nrow(matrix); ++k)
            means[k] = StatUtil.mean(VectorView.wrap(row(matrix, k)));

        return means;
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
