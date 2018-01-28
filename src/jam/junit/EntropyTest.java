
package jam.junit;

import jam.math.DoubleUtil;
import jam.math.Entropy;
import jam.math.Probability;

import org.junit.*;
import static org.junit.Assert.*;

public class EntropyTest extends NumericTestBase {
    private static double natural(double... distribution) {
        return Entropy.natural(Probability.valueOf(distribution));
    }

    private static double shannon(double... distribution) {
        return Entropy.shannon(Probability.valueOf(distribution));
    }

    @Test public void testNatural() {
        assertDouble(0.0, natural(1.0));

        assertDouble(0.0, natural(1.0, 0.0));
        assertDouble(0.0, natural(0.0, 1.0));

        assertDouble(Math.log(2.0), natural(0.5, 0.5));
        assertDouble(Math.log(4.0), natural(0.25, 0.25, 0.25, 0.25));
        assertDouble(Math.log(8.0), natural(0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125));

        assertEquals(0.562335, natural(0.25, 0.75), 1.0E-06);
        assertEquals(0.325083, natural(0.10, 0.90), 1.0E-06);
    }

    @Test public void testShannon() {
        assertDouble(0.0, shannon(1.0));

        assertDouble(0.0, shannon(1.0, 0.0));
        assertDouble(0.0, shannon(0.0, 1.0));

        assertDouble(1.0, shannon(0.5, 0.5));
        assertDouble(2.0, shannon(0.25, 0.25, 0.25, 0.25));
        assertDouble(3.0, shannon(0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125));

        assertEquals(0.811278, shannon(0.25, 0.75), 1.0E-06);
        assertEquals(0.468996, shannon(0.10, 0.90), 1.0E-06);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EntropyTest");
    }
}
