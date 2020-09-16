
package jam.matrix;

import jam.junit.NumericTestBase;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class MatrixViewTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0e-12;

    private static final MatrixView FIXED =
        MatrixView.wrap(new double[][] {{ 0.0, 0.1, 0.2 }, 
                                        { 1.0, 1.1, 1.2 },
                                        { 2.0, 2.1, 2.2 },
                                        { 3.0, 3.1, 3.2 }});

    public MatrixViewTest() {
        super(TOLERANCE);
    }

    @Test public void testCursorIterator() {
        int index = 0;
        double[] elements = new double[6];
        MatrixView matrix = 
            MatrixView.wrap(new double[][] {{ 1.0, 2.0, 3.0 },
                                            { 4.0, 5.0, 6.0 }});

        for (MatrixCursor cursor : matrix)
            elements[index++] = matrix.get(cursor);

        assertDouble(1.0, elements[0]);
        assertDouble(2.0, elements[1]);
        assertDouble(3.0, elements[2]);
        assertDouble(4.0, elements[3]);
        assertDouble(5.0, elements[4]);
        assertDouble(6.0, elements[5]);
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

        MatrixView m1 = MatrixView.wrap(x1);
        MatrixView m2 = MatrixView.wrap(x2);
        MatrixView m3 = MatrixView.wrap(x3);
        MatrixView m4 = MatrixView.wrap(x4);

        assertTrue(m1.equals(m1));
        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
        assertFalse(m1.equals(m4));
    }

    @Test public void testEqualsTolerance() {
        MatrixView m1 = MatrixView.wrap(new double[][] {{ 1.0,  2.0,  3.0  }, { 4.0,  5.0,  6.0  }});
        MatrixView m2 = MatrixView.wrap(new double[][] {{ 1.01, 2.02, 2.99 }, { 4.01, 4.99, 6.01 }});

        assertTrue(m1.equalsMatrix(m2, 0.03));
        assertFalse(m1.equalsMatrix(m2, 0.001));
        assertFalse(m1.equalsMatrix(m2));
    }

    @Test public void testViewDiagonal() {
        VectorView actual   = FIXED.viewDiagonal();
        VectorView expected = VectorView.wrap(0.0, 1.1, 2.2);

        assertEquals(expected, actual);
    }

    @Test public void testViewRow() {
        VectorView actual   = FIXED.viewRow(2);
        VectorView expected = VectorView.wrap(2.0, 2.1, 2.2);

        assertEquals(expected, actual);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testViewRowInvalid1() {
        FIXED.viewRow(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testViewRowInvalid2() {
        FIXED.viewRow(10);
    }

    @Test public void testViewRows() {
        VectorView[] rows = FIXED.viewRows();

        assertEquals(4, rows.length);
        assertEquals(VectorView.wrap(0.0, 0.1, 0.2), rows[0]);
        assertEquals(VectorView.wrap(1.0, 1.1, 1.2), rows[1]);
        assertEquals(VectorView.wrap(2.0, 2.1, 2.2), rows[2]);
        assertEquals(VectorView.wrap(3.0, 3.1, 3.2), rows[3]);
    }

    @Test public void testViewColumn() {
        VectorView actual   = FIXED.viewColumn(2);
        VectorView expected = VectorView.wrap(0.2, 1.2, 2.2, 3.2);

        assertEquals(expected, actual);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testViewColumnInvalid1() {
        FIXED.viewColumn(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testViewColumnInvalid2() {
        FIXED.viewColumn(10);
    }

    @Test public void testViewColumns() {
        VectorView[] cols = FIXED.viewColumns();

        assertEquals(3, cols.length);
        assertEquals(VectorView.wrap(0.0, 1.0, 2.0, 3.0), cols[0]);
        assertEquals(VectorView.wrap(0.1, 1.1, 2.1, 3.1), cols[1]);
        assertEquals(VectorView.wrap(0.2, 1.2, 2.2, 3.2), cols[2]);
    }

    @Test public void testToNumeric() {
        double[][] mat1 = new double[][] {{ 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }};
        double[][] mat2 = new double[][] {{ 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }};

        MatrixView view = MatrixView.wrap(mat1);

        double[][] mat3 = view.toNumeric();
        assertTrue(MatrixUtil.equals(mat1, mat3, TOLERANCE));

        // Verify that the original array and view are unchanged...
        mat3[1][2] = -1.234;
        assertTrue(MatrixUtil.equals(mat1, mat2, TOLERANCE));
        assertTrue(MatrixUtil.equals(mat1, view.toNumeric(), TOLERANCE));
    }

    @Test public void testWrap() {
        double[][] matrix = new double[][] {{ 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }};
        MatrixView view   = MatrixView.wrap(matrix);

        assertEquals(2, view.nrow());
        assertEquals(3, view.ncol());

        assertDouble(1.0, view.get(0, 0));
        assertDouble(2.0, view.get(0, 1));
        assertDouble(3.0, view.get(0, 2));

	// Verify that changes to the underlying bare array are
	// reflected in the view...
        matrix[0][0] = 5.5;
        matrix[1][2] = 4.4;

        assertDouble(5.5, view.get(0, 0));
        assertDouble(4.4, view.get(1, 2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.matrix.MatrixViewTest");
    }
}
