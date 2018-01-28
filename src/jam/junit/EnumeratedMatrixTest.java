
package jam.junit;

import jam.data.EnumeratedMatrix;

import org.junit.*;
import static org.junit.Assert.*;

public class EnumeratedMatrixTest extends NumericTestBase {
    public enum Enum1 { A, B, C };
    public enum Enum2 { ONE, TWO };

    @Test public void testBasic() {
        EnumeratedMatrix<Enum1, Enum2> matrix =
            EnumeratedMatrix.create(Enum1.class, Enum2.class, 1.0);

        assertEquals(3, matrix.nrow());
        assertEquals(2, matrix.ncol());

        for (Enum1 row : Enum1.values())
            for (Enum2 col : Enum2.values())
                assertDouble(1.0, matrix.get(row, col));

        matrix.set(Enum1.B, Enum2.ONE, 3.33);
        assertDouble(3.33, matrix.get(Enum1.B, Enum2.ONE));
    }

    @Test public void testSquare() {
        EnumeratedMatrix<Enum1, Enum1> matrix = 
            EnumeratedMatrix.square(Enum1.class);

        assertEquals(3, matrix.nrow());
        assertEquals(3, matrix.ncol());

        for (Enum1 row : Enum1.values())
            for (Enum1 col : Enum1.values())
                assertDouble(0.0, matrix.get(row, col));

        matrix.set(Enum1.B, Enum1.A, 3.33);
        assertDouble(3.33, matrix.get(Enum1.B, Enum1.A));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnumeratedMatrixTest");
    }
}
