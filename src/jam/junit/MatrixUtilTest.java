
package jam.junit;

import jam.matrix.MatrixUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class MatrixUtilTest {
    private static final double TOLERANCE = 1.0e-12;

    @Test public void testCreate() {
	int nrow = 5;
	int ncol = 10;
	double fill = 1.234;

	double[][] matrix = MatrixUtil.create(nrow, ncol, fill);
	assertEquals(nrow, matrix.length);

	for (int row = 0; row < nrow; row++) {
	    assertEquals(ncol, matrix[row].length);

	    for (int col = 0; col < ncol; col++)
		assertEquals(fill, matrix[row][col], TOLERANCE);
	}
    }

    @Test public void testCopy() {
        double[][] mat1 = MatrixUtil.create(5, 10, 1.234);
        double[][] mat2 = MatrixUtil.copy(mat1);

        assertEquals( 5, MatrixUtil.nrow(mat2));
        assertEquals(10, MatrixUtil.ncol(mat2));

	for (int row = 0; row < 5; row++)
	    for (int col = 0; col < 10; col++)
		assertEquals(1.234, mat2[row][col], TOLERANCE);

        // Ensure that changes to the original are not reflected in
        // the copy, and vice versa...
        mat1[0][0] = 3.333;
        mat2[1][1] = 5.678;

        assertEquals(1.234, mat2[0][0], TOLERANCE);
        assertEquals(1.234, mat1[1][1], TOLERANCE);
        
    }

    @Test public void testEquals() {
        double[][] x1 = new double[][] {{ 1.0, 2.0, 3.0 },
                                        { 4.0, 5.0, 6.0 }};
        double[][] x2 = new double[][] {{ 1.0, 2.0, 3.0 },
                                        { 4.0, 5.0, 6.0 }};
        double[][] x3 = new double[][] {{ 3.0, 2.0, 1.0 },
                                        { 4.0, 5.0, 6.0 }};
        double[][] x4 = new double[][] {{ 1.0, 2.0 },
                                        { 3.0, 4.0 },
                                        { 5.0, 6.0 }};
        double[][] x5 = new double[][] {{ 1.0, 2.0 },
                                        { 3.0, 4.0, 5.0 }};

        assertTrue(MatrixUtil.equals( x1, x2, TOLERANCE));
        assertFalse(MatrixUtil.equals(x1, x3, TOLERANCE));
        assertFalse(MatrixUtil.equals(x1, x4, TOLERANCE));
        assertFalse(MatrixUtil.equals(x1, x5, TOLERANCE));
    }

    @Test(expected = RuntimeException.class)
    public void testCreateInvalid1() {
	MatrixUtil.create(-1, 10, -1.1);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateInvalid2() {
	MatrixUtil.create(5, -1, -1.1);
    }

    @Test public void testIsSymmetric() {
        double[][] x1 = new double[][] {{ 1.0, 2.0, 3.0 },
                                        { 2.0, 4.0, 6.0 },
                                        { 3.0, 6.0, 9.0 }};

        assertTrue(MatrixUtil.isSymmetric(x1));

        x1[0][1] = 1.1;

        assertFalse(MatrixUtil.isSymmetric(x1));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MatrixUtilTest");
    }
}
