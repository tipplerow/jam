
package jam.junit;

import jam.pepmhc.PredictorKey;
import jam.pepmhc.PredictionMethod;
import jam.pepmhc.smm.StabilizedMatrix;
import jam.peptide.Peptide;
import jam.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class StabilizedMatrixTest extends NumericTestBase {
    PredictorKey smmKey =
        new PredictorKey(PredictionMethod.SMM, "HLA-A*01:01", 9);

    @Test public void testSMM() {
        StabilizedMatrix mat = StabilizedMatrix.instance(smmKey);

        assertEquals(9, mat.getPeptideLength());
        assertEquals(5.07755, mat.getIntercept(), 0.00001);

        assertEquals( 0.169, mat.getElement(Residue.valueOfCode1('A'), 0), 0.001);
        assertEquals(-1.007, mat.getElement(Residue.valueOfCode1('T'), 1), 0.001);
        assertEquals(-0.985, mat.getElement(Residue.valueOfCode1('D'), 2), 0.001);
        assertEquals( 0.033, mat.getElement(Residue.valueOfCode1('Y'), 3), 0.001);

        assertEquals(167.70654039, mat.computeIC50(Peptide.parse("YWDRNTQIY")), 1.0E-08);
        assertEquals(1873.0527138, mat.computeIC50(Peptide.parse("QTSYQYLII")), 1.0E-07);
        assertEquals(11656.012379, mat.computeIC50(Peptide.parse("TSAFNKKTF")), 1.0E-06);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StabilizedMatrixTest");
    }
}
