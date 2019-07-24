
package jam.junit;

import jam.rna.Expression;
import jam.rna.ConcentrationModel;
import jam.rna.LogConcentrationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class LogConcentrationModelTest {
    static {
        System.setProperty(ConcentrationModel.CONCENTRATION_MODEL_TYPE_PROPERTY, "LOG");
        System.setProperty(LogConcentrationModel.ALPHA_PROPERTY, "2.0");
        System.setProperty(LogConcentrationModel.MAX_CONC_PROPERTY, "10.0");
    }

    @Test public void testAll() {
        assertConcentration(0.0, 0.0);
        assertConcentration(0.0, 0.01);
        assertConcentration(0.4055, 1.0);
        assertConcentration(3.9318, 100.0);
        assertConcentration(8.5174, 10000.0);
        assertConcentration(10.0, 1000000.0);
    }

    private void assertConcentration(double expected, double expression) {
        double actual = ConcentrationModel.global().translate(Expression.valueOf(expression)).doubleValue();
        assertEquals(expected, actual, 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LogConcentrationModelTest");
    }
}
