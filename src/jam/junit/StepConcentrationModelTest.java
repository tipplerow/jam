
package jam.junit;

import jam.chem.Concentration;
import jam.hugo.HugoSymbol;
import jam.rna.Expression;
import jam.rna.ConcentrationModel;
import jam.rna.StepConcentrationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class StepConcentrationModelTest {
    static {
        System.setProperty(ConcentrationModel.CONCENTRATION_MODEL_TYPE_PROPERTY, "STEP");
        System.setProperty(StepConcentrationModel.EXPRESSION_THRESHOLD_PROPERTY, "10.0");
    }

    @Test public void testAll() {
        Concentration zero = Concentration.valueOf(0.0);
        Concentration unit = Concentration.valueOf(1.0);

        ConcentrationModel model = ConcentrationModel.global();

        assertEquals(zero, model.translate(Expression.valueOf(0.0)));
        assertEquals(zero, model.translate(Expression.valueOf(9.999)));
        assertEquals(unit, model.translate(Expression.valueOf(10.0)));
        assertEquals(unit, model.translate(Expression.valueOf(9999.0)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StepConcentrationModelTest");
    }
}
