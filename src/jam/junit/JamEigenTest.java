
package jam.junit;

import jam.math.DoubleComparator;
import jam.matrix.JamEigen;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class JamEigenTest extends NumericTestBase {
    @Test public void testSmallSymmetric() {
	double[][] x = new double[][] {{  2.0, -1.0,  1.0 },
				       { -1.0,  3.0,  0.0 },
				       {  1.0,  0.0,  1.0 }};

	JamEigen eigen = new JamEigen(new JamMatrix(x));

	assertEquals(3, eigen.viewValues().length());
	assertEquals(3, eigen.viewVectors().nrow());
	assertEquals(3, eigen.viewVectors().ncol());

	assertEquals(3.732051, eigen.getValue(0), 1.0e-06);
	assertEquals(2.0,      eigen.getValue(1), 1.0e-12);
	assertEquals(0.267949, eigen.getValue(2), 1.0e-06);

        assertEquals(0, eigen.countUnitEigenvalues());
        assertFalse(eigen.hasUniqueUnitEigenvalue());

        assertDouble(2.0, eigen.det());
        assertDouble(1.0, eigen.sgndet());
        assertDouble(Math.log(2.0), eigen.logdet());
    }

    @Test public void testSmallNonSymmetric() {
	//
	// The transpose of the transition matrix from Section 6.C.1
	// of L. E. Reichl, "A Modern Course in Statistical Physics".
	//
	double[][] x = new double[][] {{ 0.0, 1.0, 0.0 },
				       { 6.0, 3.0, 4.0 },
				       { 0.0, 2.0, 2.0 }};

	JamMatrix matrix = new JamMatrix(x);
        matrix.divide(6.0);

	JamEigen eigen = new JamEigen(matrix);

	assertEquals(3, eigen.viewValues().length());
	assertEquals(3, eigen.viewVectors().nrow());
	assertEquals(3, eigen.viewVectors().ncol());

        assertEquals(1, eigen.countUnitEigenvalues());
        assertTrue(eigen.hasUniqueUnitEigenvalue());

	JamVector vector = eigen.findUniqueUnitEigenvector(true);

	assertDouble(0.1, vector.get(0));
	assertDouble(0.6, vector.get(1));
	assertDouble(0.3, vector.get(2));

        assertDouble(-1.0 / 18.0, eigen.det());
        assertDouble(-1.0, eigen.sgndet());
        assertDouble(Math.log(1.0 / 18.0), eigen.logdet());
    }

    @Test(expected = RuntimeException.class)
    public void testNonSquare() {
	double[][] x = new double[][] {{  2.0, -1.0,  1.0 },
				       { -1.0,  3.0,  0.0 }};

	JamEigen eigen = new JamEigen(new JamMatrix(x));
    }

    @Test(expected = RuntimeException.class)
    public void testComplexEigenvalues() {
	double[][] x = new double[][] {{  2.0,  3.0, 4.0 },
				       { -5.0, -8.0, 0.0 },
				       { -9.0,  1.0, 7.0 }};

	JamEigen eigen = new JamEigen(new JamMatrix(x));
    }

    @Test public void testRandom() {
	int N = 10;

	JamMatrix matrix = JamMatrix.randomPositiveDefinite(N, uniform());
	JamEigen  eigen  = new JamEigen(matrix);

	// All eigenvalues must be positive...
	for (int k = 0; k < N; k++)
	    assertTrue(eigen.getValue(k) > 0.0);

	// All eigenvectors must be normalized...
	for (int k = 0; k < N; k++)
	    assertDouble(1.0, VectorAggregator.norm2(eigen.viewVector(k)));

	// Eigenvectors must be orthogonal...
	for (int i = 0; i < N - 1; i++)
	    for (int j = i + 1; j < N; j++)
		assertDouble(0.0, JamVector.dot(eigen.viewVector(i), eigen.viewVector(j)));

	// Basic eigenvalue definition...
	for (int k = 0; k < N; k++) {
	    double    value  = eigen.getValue(k);
	    JamVector vector = JamVector.copyOf(eigen.viewVector(k));

	    JamVector actual   = matrix.times(vector);
	    JamVector expected = vector.times(value);

	    assertTrue(actual.equalsVector(expected));
	}
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamEigenTest");
    }
}
