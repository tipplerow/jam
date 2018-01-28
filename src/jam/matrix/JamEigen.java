
package jam.matrix;

import java.util.ArrayList;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import jam.lang.JamException;
import jam.math.DoubleComparator;
import jam.math.IntUtil;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Computes and stores the eigenvalue decomposition of a matrix with
 * real eigenvalues.
 */
public final class JamEigen {
    private final EigenDecomposition decomp;
    private final JamVector values;
    private final JamMatrix vectors;

    /**
     * Computes the eigenvalue decomposition of a square matrix with
     * real eigenvalues.
     *
     * @param matrix the target matrix.
     *
     * @throws IllegalArgumentException unless the matrix is square
     * and has all real eigenvalues.
     */
    public JamEigen(JamMatrix matrix) {
	decomp = new EigenDecomposition(matrix.toRealMatrix());
        
	if (decomp.hasComplexEigenvalues())
	    throw new IllegalArgumentException("Matrix has complex eigenvalues.");

        values  = extractValues(decomp);
        vectors = extractVectors(decomp);

	assert isValid(matrix, values, vectors);
    }

    private static JamVector extractValues(EigenDecomposition decomp) {
	RealMatrix D = decomp.getD();
	JamVector  v = new JamVector(D.getRowDimension());

	for (int k = 0; k < v.length(); k++)
	    v.set(k, D.getEntry(k, k));

	return v;
    }

    private static JamMatrix extractVectors(EigenDecomposition decomp) {
	return new JamMatrix(decomp.getV().getData());
    }

    private static boolean isValid(JamMatrix matrix, JamVector values, JamMatrix vectors) {
        //
        // The fundamental definition must be satisfied for all
        // matrices...
        //
	for (int k = 0; k < values.length(); k++) {
	    double    value  = values.get(k);
	    JamVector vector = vectors.getColumn(k);

	    JamVector actual   = matrix.times(vector);
	    JamVector expected = vector.times(value);

	    if (!actual.equalsVector(expected))
		return false;
	}

	if (matrix.isSymmetric())
            return isDecreasing(values) && isNormalized(vectors); // Additional validation for symmetric matrices...
        else
            return true;
    }

    private static boolean isDecreasing(JamVector values) {
	//
	// Eigenvalues for symmetric matrices should be non-increasing...
	//
	for (int k = 1; k < values.length(); k++)
	    if (DoubleComparator.DEFAULT.LT(values.get(k - 1), values.get(k)))
		return false;

        return true;
    }

    private static boolean isNormalized(JamMatrix vectors) {
	//
	// Eigenvectors for symmetric matrices should have unit norm...
	//
	for (int k = 0; k < vectors.ncol(); k++)
	    if (DoubleComparator.DEFAULT.NE(1.0, VectorAggregator.norm2(vectors.getColumn(k))))
		return false;

        return true;
    }

    /**
     * Computes the determinant of the original matrix as the product
     * of the eigenvalues.
     *
     * <p>Note that the computation is subject to overflow for large
     * matrices; use {@code logdet()} and {@code sgndet()} to ensure
     * finite results.
     *
     * @return the determinant of the original matrix.
     */
    public double det() {
        double result = 1.0;

        for (double value : values)
            result *= value;

        return result;
    }

    /**
     * Computes the logarithm of the absolute value of the determinant
     * (as the sum of the logarithms of the absolute values of the
     * eigenvalues).
     *
     * @return the logarithm of the absolute value of the determinant.
     */
    public double logdet() {
        double result = 0.0;

        for (double value : values)
            result += Math.log(Math.abs(value));

        return result;
    }

    /**
     * Computes the sign of the determinant of the original matrix (as
     * the product of the signs of the eigenvalues).
     *
     * @return the sign of the determinant of the original matrix.
     */
    public double sgndet() {
        double result = 1.0;

        for (double value : values)
            result *= Math.signum(value);

        return result;
    }

    /**
     * Returns the number of <em>unit</em> (exactly equal to 1.0)
     * eigenvalues in this decomposition.
     *
     * @return the number of unit eigenvalues in this decomposition.
     */
    public int countUnitEigenvalues() {
        int result = 0;

        for (double value : values)
            if (isUnitEigenvalue(value))
                ++result;

        return result;
    }

    private boolean isUnitEigenvalue(double value) {
        return DoubleComparator.DEFAULT.EQ(value, 1.0);
    }

    /**
     * Returns the eigenvector corresponding to the unique <em>unit</em>
     * (exactly equal to 1.0) eigenvalue in this decomposition.
     *
     * @param normalize whether to scale the eigenvector so that its
     * elements have unit sum.
     *
     * @return the eigenvector corresponding to the unique unit
     * eigenvalue in this decomposition.
     *
     * @throws IllegalStateException unless this decomposition has a
     * unique unit eigenvalue.
     */
    public JamVector findUniqueUnitEigenvector(boolean normalize) {
        int[] indexes = getUnitEigenvalues();

        if (indexes.length != 1)
            throw new IllegalStateException("Eigenvector decomposition does not have a unique unit eigenvalue.");

        JamVector result = new JamVector(viewVector(indexes[0]));

        if (normalize)
            result.normalize();

        return result;
    }

    /**
     * Returns the indexes of all <em>unit</em> (exactly equal to 1.0)
     * eigenvalues in this decomposition (an empty array if there are
     * none).
     *
     * @return the indexes of all unit eigenvalues in this decomposition.
     */
    public int[] getUnitEigenvalues() {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int index = 0; index < values.length(); index++)
            if (isUnitEigenvalueIndex(index))
                result.add(index);

        return IntUtil.toArray(result);
    }

    private boolean isUnitEigenvalueIndex(int index) {
        return isUnitEigenvalue(values.get(index));
    }

    /**
     * Returns a (real) eigenvalue.
     *
     * <p>Eigenvalues are returned in descending order <em>for
     * symmetric matrices only</em>; the order is arbitrary for
     * non-symmetric matrices.
     *
     * @param index the eigenvalue index (which idenfies the
     * corresponding eigenvector).
     *
     * @return the (real) eigenvalue corresponding to the given index.
     *
     * @throws IllegalArgumentException unless the eigenvalue index is
     * valid.
     */
    public double getValue(int index) {
        return values.getDouble(index);
    }

    /**
     * Identifies decompositions with a single <em>unit</em> (exactly
     * equal to 1.0) eigenvalue.
     *
     * @return {@code true} iff this decomposition has exactly one
     * unit eigenvalue.
     */
    public boolean hasUniqueUnitEigenvalue() {
        return countUnitEigenvalues() == 1;
    }

    /**
     * Returns the (real) eigenvalues.
     *
     * <p>Eigenvalues are returned in descending order <em>for
     * symmetric matrices only</em>; the order is arbitrary for
     * non-symmetric matrices.
     *
     * @return the (real) eigenvalues.
     */
    public VectorView viewValues() {
        return values;
    }

    /**
     * Returns a specific eignvector.
     *
     * @param index the eigenvector index (which idenfies the
     * corresponding eigenvalue).
     *
     * @return the specified eignvector, whose eigenvalue is equal to
     * {@code viewValues.getDouble(k)}.
     *
     * @throws IllegalArgumentException unless the eigenvector index
     * is valid.
     */
    public VectorView viewVector(int index) {
	return vectors.viewColumn(index);
    }

    /**
     * Returns the eignvectors as matrix columns, ordered from left to
     * right by eigenvalue.
     *
     * @return the eignvectors as matrix columns, ordered from left to
     * right by eigenvalue: {@code viewVectors.getColumn(k)} has the
     * corresponding eigenvalue {@code viewValues.getDouble(k)}.
     */
    public MatrixView viewVectors() {
	return vectors;
    }
}
