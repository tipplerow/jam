
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class ObjectMatrixTest {
    @Test public void testGetSet() {
        ObjectMatrix<String> matrix = ObjectMatrix.create(2, 3);

        assertEquals(2, matrix.nrow());
        assertEquals(3, matrix.ncol());

        assertNull(matrix.get(0, 0));
        assertNull(matrix.get(0, 1));
        assertNull(matrix.get(0, 2));
        assertNull(matrix.get(1, 0));
        assertNull(matrix.get(1, 1));
        assertNull(matrix.get(1, 2));

        matrix.set(0, 1, "abc");
        matrix.set(0, 1, "def");
        matrix.set(1, 2, "ghi");

        assertNull(matrix.get(0, 0));
        assertNull(matrix.get(0, 2));
        assertNull(matrix.get(1, 0));
        assertNull(matrix.get(1, 1));

        assertEquals("def", matrix.get(0, 1));
        assertEquals("ghi", matrix.get(1, 2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.ObjectMatrixTest");
    }
}
