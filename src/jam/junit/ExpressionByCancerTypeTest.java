
package jam.junit;

import jam.data.DataMatrix;
import jam.peptide.HugoSymbol;
import jam.rna.ExpressionByCancerType;
import jam.tcga.CancerType;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionByCancerTypeTest extends NumericTestBase {
    private static CancerType ACC  = CancerType.ACC;
    private static CancerType BLCA = CancerType.BLCA;
    private static CancerType BRCA = CancerType.BRCA;

    private static HugoSymbol A1BG   = HugoSymbol.instance("A1BG");
    private static HugoSymbol A2LD1  = HugoSymbol.instance("A2LD1");
    private static HugoSymbol A2M    = HugoSymbol.instance("A2M");
    private static HugoSymbol A2ML1  = HugoSymbol.instance("A2ML1");
    private static HugoSymbol A4GALT = HugoSymbol.instance("A4GALT");

    @Test public void testLoad() {
        DataMatrix<HugoSymbol, CancerType> matrix =
            ExpressionByCancerType.load("data/test/rna_by_cancer_type.csv");

        assertEquals(5, matrix.nrow());
        assertEquals(3, matrix.ncol());

        assertEquals(A1BG,   matrix.rowKey(0));
        assertEquals(A2LD1,  matrix.rowKey(1));
        assertEquals(A2M,    matrix.rowKey(2));
        assertEquals(A2ML1,  matrix.rowKey(3));
        assertEquals(A4GALT, matrix.rowKey(4));

        assertEquals(ACC,  matrix.colKey(0));
        assertEquals(BLCA, matrix.colKey(1));
        assertEquals(BRCA, matrix.colKey(2));

        assertDouble( 48.41, matrix.get(A1BG, ACC));
        assertDouble( 35.51, matrix.get(A1BG, BLCA));
        assertDouble(131.58, matrix.get(A1BG, BRCA));

        assertDouble(258.35, matrix.get(A4GALT, ACC));
        assertDouble(845.06, matrix.get(A4GALT, BLCA));
        assertDouble(280.51, matrix.get(A4GALT, BRCA));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ExpressionByCancerTypeTest");
    }
}
