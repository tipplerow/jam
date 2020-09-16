
package jam.matrix;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.*;
import static org.junit.Assert.*;

public class MatrixCursorTest {
    @Test public void testIterator() {
        MatrixView matrix =
            MatrixView.wrap(new double[][] {{ 1.0, 2.0, 3.0 },
                                            { 4.0, 5.0, 6.0 }});

        Iterator<MatrixCursor> iter = matrix.iterator();

        assertState(0, 0, iter);
        assertState(0, 1, iter);
        assertState(0, 2, iter);
        assertState(1, 0, iter);
        assertState(1, 1, iter);
        assertState(1, 2, iter);
        assertFalse(iter.hasNext());
    }

    private void assertState(int row, int col, Iterator<MatrixCursor> iter) {
        assertTrue(iter.hasNext());

        MatrixCursor cursor = iter.next();

        assertEquals(row, cursor.row());
        assertEquals(col, cursor.col());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNoNext() {
        MatrixView matrix = MatrixView.wrap(new double[][] {{ 1.0 }});
        Iterator<MatrixCursor> iter = matrix.iterator();

        iter.next();
        iter.next();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.matrix.MatrixCursorTest");
    }
}
