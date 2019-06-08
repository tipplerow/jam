
package jam.junit;

import java.util.List;
import java.util.Set;

import jam.data.DataMatrix;
import jam.lang.KeyedObject;

import org.junit.*;
import static org.junit.Assert.*;

public class DataMatrixTest extends NumericTestBase {

    public static final class Asset extends KeyedObject<String> {
        public Asset(String key) {
            super(key);
        }
    }

    public static final class Factor extends KeyedObject<String> {
        public Factor(String key) {
            super(key);
        }
    }

    private static Asset AMAT = new Asset("AMAT");
    private static Asset INTC = new Asset("INTC");
    private static Asset KLAC = new Asset("KLAC");
    private static Asset LRCX = new Asset("LRCX");

    private static Factor MOMENTUM = new Factor("MOMENTUM");
    private static Factor QUALITY  = new Factor("QUALITY");
    private static Factor VALUE    = new Factor("VALUE");

    private static DataMatrix<Asset, Factor> createMatrix() {
        return DataMatrix.dense(List.of(AMAT, KLAC, LRCX),
                                List.of(MOMENTUM, VALUE));
    }

    @Test public void testIndexing() {
        DataMatrix<Asset, Factor> matrix = createMatrix();

        matrix.set(AMAT, MOMENTUM, 1.23);
        matrix.set(2, 1, 2.1);
        
        runIndexingTest(matrix);
        runIndexingTest(matrix.immutable());
    }

    private void runIndexingTest(DataMatrix<Asset, Factor> matrix) {
        assertEquals(-1, matrix.colIndex(QUALITY));
        assertEquals( 0, matrix.colIndex(MOMENTUM));
        assertEquals( 1, matrix.colIndex(VALUE));

        assertEquals(MOMENTUM, matrix.colKey(0));
        assertEquals(VALUE,    matrix.colKey(1));

        assertEquals(List.of(MOMENTUM, VALUE), matrix.colKeyList());
        assertEquals(Set.of( MOMENTUM, VALUE), matrix.colKeySet());

        assertTrue(matrix.contains( AMAT, VALUE));
        assertFalse(matrix.contains(AMAT, QUALITY));
        assertFalse(matrix.contains(INTC, VALUE));
        assertFalse(matrix.contains(INTC, QUALITY));

        assertTrue(matrix.containsCol(VALUE));
        assertFalse(matrix.containsCol(QUALITY));

        assertTrue(matrix.containsRow(AMAT));
        assertFalse(matrix.containsRow(INTC));

        assertEquals(3, matrix.nrow());
        assertEquals(2, matrix.ncol());

        assertEquals(-1, matrix.rowIndex(INTC));
        assertEquals( 0, matrix.rowIndex(AMAT));
        assertEquals( 1, matrix.rowIndex(KLAC));
        assertEquals( 2, matrix.rowIndex(LRCX));

        assertEquals(AMAT, matrix.rowKey(0));
        assertEquals(KLAC, matrix.rowKey(1));
        assertEquals(LRCX, matrix.rowKey(2));

        assertEquals(List.of(AMAT, KLAC, LRCX), matrix.rowKeyList());
        assertEquals(Set.of( AMAT, KLAC, LRCX), matrix.rowKeySet());

        assertDouble(1.23, matrix.get(AMAT, MOMENTUM));
        assertDouble(2.1,  matrix.get(2, 1));
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalidColumnIndex() {
        createMatrix().get(0, 5);
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalidColumnKey() {
        createMatrix().get(AMAT, QUALITY);
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalidRowIndex() {
        createMatrix().get(3, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalidRowKey() {
        createMatrix().get(INTC, VALUE);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalidColumnIndex() {
        createMatrix().set(0, 5, 1.1);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalidColumnKey() {
        createMatrix().set(AMAT, QUALITY, 1.1);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalidRowIndex() {
        createMatrix().set(3, 0, 1.1);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalidRowKey() {
        createMatrix().set(INTC, VALUE, 1.1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutable() {
        DataMatrix<Asset, Factor> matrix = createMatrix();
        matrix.immutable().set(0, 0, 1.23);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DataMatrixTest");
    }
}
