
package jam.junit;

import jam.hugo.HugoSymbol;
import jam.rna.Expression;
import jam.rna.TumorExpressionMatrix;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorExpressionMatrixTest extends NumericTestBase {
    private static final TumorBarcode tumor1 = TumorBarcode.instance("OR-A5J1");
    private static final TumorBarcode tumor2 = TumorBarcode.instance("OR-A5J2");
    private static final TumorBarcode tumor3 = TumorBarcode.instance("OR-A5J3");

    private static final HugoSymbol gene1 = HugoSymbol.instance("A1BG");
    private static final HugoSymbol gene2 = HugoSymbol.instance("A1CF");
    private static final HugoSymbol gene3 = HugoSymbol.instance("A2BP1");
    private static final HugoSymbol gene4 = HugoSymbol.instance("A2LD1");
    private static final HugoSymbol gene5 = HugoSymbol.instance("A2M");

    private static final TumorBarcode bad_tumor = TumorBarcode.instance("bad_tumor");
    private static final HugoSymbol   bad_gene  = HugoSymbol.instance("bad_gene");

    private static final TumorExpressionMatrix matrix =
        TumorExpressionMatrix.load("data/test/tumor_expression.csv");

    @Test public void testAll() {
        assertTrue(matrix.contains(tumor1));
        assertTrue(matrix.contains(tumor2));
        assertTrue(matrix.contains(tumor3));

        assertTrue(matrix.contains(gene1));
        assertTrue(matrix.contains(gene2));
        assertTrue(matrix.contains(gene3));
        assertTrue(matrix.contains(gene4));
        assertTrue(matrix.contains(gene5));

        assertExpression(  16.3305, matrix.get(tumor1, gene1));
        assertExpression(   5.6368, matrix.get(tumor2, gene3));
        assertExpression(7201.84,   matrix.get(tumor3, gene5));

        assertFalse(matrix.contains(bad_tumor));
        assertFalse(matrix.contains(bad_gene));

        assertNull(matrix.get(tumor1, bad_gene));
        assertNull(matrix.get(bad_tumor, gene1));
        assertNull(matrix.get(bad_tumor, bad_gene));
    }

    private void assertExpression(double expected, Expression actual) {
        assertEquals(expected, actual.doubleValue(), 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TumorExpressionMatrixTest");
    }
}
