
package jam.matrix;

import jam.junit.NumericTestBase;
import jam.math.RandomSequence;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;

import org.junit.*;
import static org.junit.Assert.*;

public class JamMatrixTest extends NumericTestBase {
    private static double TOLERANCE = 1.0e-15;

    private static final JamMatrix FIXED1 =
        JamMatrix.byrow(4, 3, 
                        0.0, 0.1, 0.2, 
                        1.0, 1.1, 1.2,
                        2.0, 2.1, 2.2,
                        3.0, 3.1, 3.2);

    private static final JamMatrix FIXED2 =
        JamMatrix.byrow(4, 3, 
                        5.0, 7.3, 9.6, 
                        5.1, 7.4, 9.7,
                        5.2, 7.5, 9.8,
                        5.3, 7.6, 9.9);

    private static final JamMatrix FIXED3 =
        JamMatrix.byrow(3, 2, 
                        10.0, 10.1,
                        11.0, 11.1,
                        12.0, 12.1);

    @Test public void testAddScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5);

        m1.add(0.5);
        assertEquals(m2, m1);
    }

    @Test public void testAddMatrix() {
        JamMatrix actual = FIXED1.copy();
        actual.add(FIXED2);

        JamMatrix expected = 
            JamMatrix.byrow(4, 3,
                            5.0,  7.4,  9.8,
                            6.1,  8.5, 10.9,
                            7.2,  9.6, 12.0,
                            8.3, 10.7, 13.1);

        assertEquals(expected, actual);
        assertFixedUnchanged();
    }

    private void assertFixedUnchanged() {
        JamMatrix expected1 =
            JamMatrix.byrow(4, 3, 
                            0.0, 0.1, 0.2, 
                            1.0, 1.1, 1.2,
                            2.0, 2.1, 2.2,
                            3.0, 3.1, 3.2);

        JamMatrix expected2 =
            JamMatrix.byrow(4, 3, 
                            5.0, 7.3, 9.6, 
                            5.1, 7.4, 9.7,
                            5.2, 7.5, 9.8,
                            5.3, 7.6, 9.9);

        JamMatrix expected3 =
            JamMatrix.byrow(3, 2, 
                            10.0, 10.1,
                            11.0, 11.1,
                            12.0, 12.1);

        assertEquals(expected1, FIXED1);
        assertEquals(expected2, FIXED2);
        assertEquals(expected3, FIXED3);
    }

    @Test(expected = RuntimeException.class)
    public void testAddMatrixInvalid() {
        FIXED1.add(FIXED3);
    }

    @Test public void testAggregate() {
        JamVector v1 = FIXED1.aggregateRows(VectorAggregator.SUM);
        JamVector v2 = JamVector.valueOf(0.3, 3.3, 6.3, 9.3);

        JamVector v3 = FIXED1.aggregateColumns(VectorAggregator.SUM);
        JamVector v4 = JamVector.valueOf(6.0, 6.4, 6.8);

        assertEquals(v1, v2);
        assertEquals(v3, v4);
    }

    @Test public void testBasic() {
        int nrow = 10;
        int ncol = 20;
        JamMatrix matrix = new JamMatrix(nrow, ncol);

        assertEquals(nrow, matrix.nrow());
        assertEquals(ncol, matrix.ncol());

        matrix.set(2, 5, 25.0);
        assertDouble(25.0, matrix.get(2, 5));
    }

    @Test public void testByRow() {
	JamMatrix mat = JamMatrix.byrow(2, 3, 
                                        1.0, 2.0, 3.0,
                                        4.0, 5.0, 6.0);

        assertEquals(2, mat.nrow());
        assertEquals(3, mat.ncol());

        assertDouble(1.0, mat.get(0, 0));
        assertDouble(2.0, mat.get(0, 1));
        assertDouble(3.0, mat.get(0, 2));
        assertDouble(4.0, mat.get(1, 0));
        assertDouble(5.0, mat.get(1, 1));
        assertDouble(6.0, mat.get(1, 2));
    }

    @Test public void testByCol() {
	JamMatrix mat = JamMatrix.bycol(2, 3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);

        assertEquals(2, mat.nrow());
        assertEquals(3, mat.ncol());

        assertDouble(1.0, mat.get(0, 0));
        assertDouble(2.0, mat.get(1, 0));
        assertDouble(3.0, mat.get(0, 1));
        assertDouble(4.0, mat.get(1, 1));
        assertDouble(5.0, mat.get(0, 2));
        assertDouble(6.0, mat.get(1, 2));
    }

    @Test public void testConstructorBasic() {
        JamMatrix mat = new JamMatrix(2, 3);

        assertEquals(2, mat.nrow());
        assertEquals(3, mat.ncol());
        assertAll(0.0, mat);
    }

    private void assertAll(double expected, JamMatrix matrix) {
        for (int row = 0; row < matrix.nrow(); row++)
            for (int col = 0; col < matrix.ncol(); col++)
                assertDouble(expected, matrix.get(row, col));
    }

    @Test public void testCbind() {
        JamVector col0 = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector col1 = JamVector.valueOf(4.0, 5.0, 6.0);

        JamMatrix expected = 
            JamMatrix.byrow(3, 2, 
                            1.0, 4.0,
                            2.0, 5.0, 
                            3.0, 6.0);

        assertEquals(expected, JamMatrix.cbind(col0, col1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCbindInvalid1() {
        JamMatrix.cbind();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCbindInvalid2() {
        JamMatrix.cbind(JamVector.valueOf(1.0, 2.0), JamVector.valueOf(1.0, 2.0, 3.0));
    }

    @Test public void testConstructorFill() {
        JamMatrix mat = new JamMatrix(10, 5, 3.33);

        assertEquals(10, mat.nrow());
        assertEquals(5, mat.ncol());
        assertAll(3.33, mat);
    }

    @Test public void testConstructorBareMatrix() {
        double[][] bare = new double[][] {{ 1.0, 2.0, 3.0 },
                                          { 4.0, 5.0, 6.0 }};
        JamMatrix jam = new JamMatrix(bare);

        assertEquals(2, jam.nrow());
        assertEquals(3, jam.ncol());

        assertDouble(1.0, jam.get(0, 0));
        assertDouble(2.0, jam.get(0, 1));
        assertDouble(3.0, jam.get(0, 2));
        assertDouble(4.0, jam.get(1, 0));
        assertDouble(5.0, jam.get(1, 1));
        assertDouble(6.0, jam.get(1, 2));

        // Assert changes to the bare matrix are not reflected in the
        // JamMatrix and vice-versa...
        bare[0][0] = 3.45;
        jam.set(1, 1, 7.89);
        
        assertDouble(1.0, jam.get(0, 0));
        assertDouble(5.0, bare[1][1]);
    }

    @Test public void testConstructorView() {
	JamMatrix mat1 = JamMatrix.byrow(2, 3, 
                                         1.0, 2.0, 3.0,
                                         4.0, 5.0, 6.0);

        JamMatrix mat2 = new JamMatrix(mat1);

        assertEquals(2, mat2.nrow());
        assertEquals(3, mat2.ncol());

        assertDouble(1.0, mat2.get(0, 0));
        assertDouble(2.0, mat2.get(0, 1));
        assertDouble(3.0, mat2.get(0, 2));
        assertDouble(4.0, mat2.get(1, 0));
        assertDouble(5.0, mat2.get(1, 1));
        assertDouble(6.0, mat2.get(1, 2));

        // Assert changes to the first matrix are not reflected in the
        // second matrix and vice-versa...
        mat1.set(0, 0, 3.45);
        mat2.set(1, 1, 7.89);
        
        assertDouble(1.0, mat2.get(0, 0));
        assertDouble(5.0, mat1.get(1, 1));
    }

    @Test public void testCopy() {
	JamMatrix mat1 = JamMatrix.byrow(2, 3, 
                                         1.0, 2.0, 3.0,
                                         4.0, 5.0, 6.0);

        JamMatrix mat2 = mat1.copy();

        assertEquals(2, mat2.nrow());
        assertEquals(3, mat2.ncol());

        assertDouble(1.0, mat2.get(0, 0));
        assertDouble(2.0, mat2.get(0, 1));
        assertDouble(3.0, mat2.get(0, 2));
        assertDouble(4.0, mat2.get(1, 0));
        assertDouble(5.0, mat2.get(1, 1));
        assertDouble(6.0, mat2.get(1, 2));

        // Assert changes to the original matrix are not reflected in
        // the copy and vice-versa...
        mat1.set(0, 0, 3.45);
        mat2.set(1, 1, 7.89);
        
        assertDouble(1.0, mat2.get(0, 0));
        assertDouble(5.0, mat1.get(1, 1));
    }

    @Test public void testDiagonalInstance() {
        JamMatrix m1 = JamMatrix.byrow(3, 3, 
                                       1.0, 0.0, 0.0,
                                       0.0, 2.0, 0.0, 
                                       0.0, 0.0, 3.0);
        JamMatrix m2 = JamMatrix.diagonal(1.0, 2.0, 3.0);
        assertEquals(m1, m2);

        JamMatrix m3 = JamMatrix.byrow(3, 3, 
                                       1.5, 0.0, 0.0,
                                       0.0, 3.0, 0.0, 
                                       0.0, 0.0, 4.5);
        assertEquals(m3, m2.times(1.5));

        // Ensure off-diagonal assignment is honored...
        m2.set(0, 1, 3.33);
        assertDouble(3.33, m2.get(0, 1));
    }

    @Test public void testDivideScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0);

        m1.divide(2.0);
        assertEquals(m2, m1);
    }

    @Test public void testDyad() {
        JamVector x = JamVector.valueOf( 1.0, 2.0, 3.0);
        JamVector y = JamVector.valueOf(-1.0, 0.4, 2.1);

        JamMatrix expected = 
            JamMatrix.byrow(3, 3, 
                            -1.0, 0.4, 2.1,
                            -2.0, 0.8, 4.2,
                            -3.0, 1.2, 6.3);

        assertEquals(expected, JamMatrix.dyad(x, y));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDyadInvalid() {
        JamMatrix.dyad(new JamVector(3), new JamVector(4));
    }

    @Test public void testGetDiagonal() {
        JamVector actual   = FIXED1.getDiagonal();
        JamVector expected = JamVector.valueOf(0.0, 1.1, 2.2);

        assertEquals(expected, actual);
    }

    @Test public void testGetRow() {
        JamVector actual   = FIXED1.getRow(2);
        JamVector expected = JamVector.valueOf(2.0, 2.1, 2.2);

        assertEquals(expected, actual);
    }

    @Test public void testGetColumn() {
        JamVector actual   = FIXED1.getColumn(2);
        JamVector expected = JamVector.valueOf(0.2, 1.2, 2.2, 3.2);

        assertEquals(expected, actual);
    }

    @Test public void testIdentity() {
        JamMatrix m1 = JamMatrix.byrow(3, 3, 
                                       1.0, 0.0, 0.0,
                                       0.0, 1.0, 0.0, 
                                       0.0, 0.0, 1.0);
        JamMatrix m2 = JamMatrix.identity(3);
        assertEquals(m1, m2);
    }

    @Test public void testInverseDense() {
        JamMatrix m1 = JamMatrix.random(10, 10, RandomSequence.uniform());
        JamMatrix m2 = m1.inverse();

        assertEquals(JamMatrix.identity(10), m1.times(m2));
        assertEquals(JamMatrix.identity(10), m2.times(m1));
    }

    @Test public void testInverseDiagonal() {
        JamMatrix m1 = JamMatrix.diagonal(1.0, 2.0, 5.0);

        JamMatrix actual   = m1.inverse();
        JamMatrix expected = JamMatrix.byrow(3, 3,
                                             1.0, 0.0, 0.0,
                                             0.0, 0.5, 0.0,
                                             0.0, 0.0, 0.2);

        assertEquals(expected, actual);
    }

    @Test public void testIsSymmetric() {
        JamMatrix m1 = JamMatrix.diagonal(1.0, 2.0, 3.0);
        JamMatrix m2 = JamMatrix.byrow(3, 3,
                                       1.0, 1.2, 1.3,
                                       1.2, 2.0, 2.3,
                                       1.3, 2.3, 3.0);

        assertTrue(m1.isSymmetric());
        assertTrue(m2.isSymmetric());

        m2.set(2, 2, 2.222);
        assertTrue(m2.isSymmetric());

        m2.set(1, 2, 2.222);
        assertFalse(m2.isSymmetric());
    }

    @Test public void testMinusScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3,  5.0, 15.0, 25.0, 35.0, 45.0, 55.0);
        JamMatrix m3 = m1.minus(5.0);

        assertEquals(m2, m3);
    }

    @Test public void testMinusMatrix() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3,  1.0,  2.0,  3.0,  4.0,  5.0,  6.0);
        JamMatrix m3 = JamMatrix.byrow(2, 3,  9.0, 18.0, 27.0, 36.0, 45.0, 54.0);

        assertEquals(m3, m1.minus(m2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinusMatrixInvalid() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(3, 2,  1.0,  2.0,  3.0,  4.0,  5.0,  6.0);

        m1.minus(m2);
    }

    @Test public void testMultiplyScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 1.5, 3.0, 4.5, 6.0, 7.5, 9.0);

        m1.multiply(1.5);
        assertEquals(m2, m1);
    }

    @Test public void testPlusScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 15.0, 25.0, 35.0, 45.0, 55.0, 65.0);
        JamMatrix m3 = m1.plus(5.0);

        assertEquals(m2, m3);
    }

    @Test public void testPlusMatrix() {
        JamMatrix m1 = JamMatrix.byrow(2, 3,  1.0,  2.0,  3.0,  4.0,  5.0,  6.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m3 = JamMatrix.byrow(2, 3, 11.0, 22.0, 33.0, 44.0, 55.0, 66.0);

        assertEquals(m3, m1.plus(m2));
        assertEquals(m3, m2.plus(m1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlusMatrixInvalid() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(3, 2,  1.0,  2.0,  3.0,  4.0,  5.0,  6.0);

        m1.plus(m2);
    }

    @Test public void testRbind() {
        JamVector row0 = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector row1 = JamVector.valueOf(4.0, 5.0, 6.0);

        JamMatrix expected = 
            JamMatrix.byrow(2, 3, 
                            1.0, 2.0, 3.0, 
                            4.0, 5.0, 6.0);

        assertEquals(expected, JamMatrix.rbind(row0, row1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRbindInvalid1() {
        JamMatrix.rbind();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRbindInvalid2() {
        JamMatrix.rbind(JamVector.valueOf(1.0, 2.0), JamVector.valueOf(1.0, 2.0, 3.0));
    }

    @Test public void testSetRow() {
        JamVector row0 = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector row1 = JamVector.valueOf(4.0, 5.0, 6.0);

        JamMatrix expected = 
            JamMatrix.byrow(2, 3, 
                            1.0, 2.0, 3.0, 
                            4.0, 5.0, 6.0);

        JamMatrix actual = new JamMatrix(2, 3);
        actual.setRow(0, row0);
        actual.setRow(1, row1);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowInvalid1() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setRow(-1, new JamVector(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowInvalid2() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setRow(3, new JamVector(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowInvalid3() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setRow(0, new JamVector(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowInvalid4() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setRow(0, new JamVector(4));
    }

    @Test public void testSetColumn() {
        JamVector col0 = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector col1 = JamVector.valueOf(4.0, 5.0, 6.0);

        JamMatrix expected = 
            JamMatrix.byrow(3, 2, 
                            1.0, 4.0,
                            2.0, 5.0, 
                            3.0, 6.0);

        JamMatrix actual = new JamMatrix(3, 2);
        actual.setColumn(0, col0);
        actual.setColumn(1, col1);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetColumnInvalid1() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setColumn(-1, new JamVector(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetColumnInvalid2() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setColumn(10, new JamVector(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetColumnInvalid3() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setColumn(0, new JamVector(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetColumnInvalid4() {
        JamMatrix mat = new JamMatrix(2, 3);
        mat.setColumn(0, new JamVector(3));
    }

    @Test public void testSubtractScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 0.5, 1.5, 2.5, 3.5, 4.5, 5.5);

        m1.subtract(0.5);
        assertEquals(m2, m1);
    }

    @Test public void testSubtractMatrix() {
        JamMatrix actual = FIXED1.copy();
        actual.subtract(FIXED2);

        JamMatrix expected = 
            JamMatrix.byrow(4, 3,
                            -5.0, -7.2, -9.4,
                            -4.1, -6.3, -8.5,
                            -3.2, -5.4, -7.6,
                            -2.3, -4.5, -6.7);

        assertEquals(expected, actual);
        assertFixedUnchanged();
    }

    @Test(expected = RuntimeException.class)
    public void testSubtractMatrixInvalid() {
        FIXED1.subtract(FIXED3);
    }

    @Test public void testTimesScalar() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
        JamMatrix m2 = JamMatrix.byrow(2, 3, 15.0, 30.0, 45.0, 60.0, 75.0, 90.0);
        JamMatrix m3 = m1.times(1.5);

        assertEquals(m2, m3);
    }

    @Test public void getTimesVector() {
        JamVector factor   = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector actual   = FIXED1.times(factor);
        JamVector expected = JamVector.valueOf(0.8, 6.8, 12.8, 18.8);

        assertEquals(expected, actual);
    }

    @Test public void testTimesLeftVector() {
        JamVector vector = JamVector.valueOf(1.0, 2.0, 3.0);
        JamMatrix matrix = JamMatrix.byrow(3, 2,
                                           10.0, 100.0,
                                           20.0, 200.0,
                                           30.0, 300.0);

        JamVector result = JamMatrix.times(vector, matrix);
        assertEquals(2, result.length());

        assertDouble( 140.0, result.get(0));
        assertDouble(1400.0, result.get(1));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTimesLeftVectorInvalid() {
        JamVector vector = JamVector.valueOf(1.0, 2.0, 3.0);
        JamMatrix matrix = JamMatrix.byrow(2, 3, 
                                           1.0, 2.0, 3.0,
                                           4.0, 5.0, 6.0);

        JamMatrix.times(vector, matrix);
    }

    @Test public void getTimesMatrix() {
        JamMatrix actual   = FIXED1.times(FIXED3);
        JamMatrix expected = 
            JamMatrix.byrow(4, 2,
                              3.5,   3.53,
                             36.5,  36.83,
                             69.5,  70.13,
                            102.5, 103.43);

        assertEquals(expected, actual);
    }

    @Test public void testTranspose() {
        JamMatrix m1 = JamMatrix.byrow(2, 3, 
                                       1.0, 2.0, 3.0, 
                                       4.0, 5.0, 6.0);

        JamMatrix m2 = JamMatrix.byrow(3, 2,
                                       1.0, 4.0,
                                       2.0, 5.0,
                                       3.0, 6.0);

        assertEquals(m2, m1.transpose());

        JamMatrix m3 = JamMatrix.diagonal(1.0, 2.0, 3.0);
        JamMatrix m4 = JamMatrix.diagonal(1.0, 2.0, 3.0);

        assertEquals(m4, m3.transpose());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.matrix.JamMatrixTest");
    }
}
