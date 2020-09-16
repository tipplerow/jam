
package jam.math;

import org.junit.*;
import static org.junit.Assert.*;

public class QuantileTest {
    @Test public void testSmall() {
        Quantile quantile = new Quantile();
        assertTrue(Double.isNaN(quantile.evaluate(0.5)));

        quantile = new Quantile(3.33);
        assertEquals(3.33, quantile.evaluate(0.0001), 1.0e-12);
        assertEquals(3.33, quantile.evaluate(0.5), 1.0e-12);
        assertEquals(3.33, quantile.evaluate(1.0), 1.0e-12);

        quantile = new Quantile(0.0, 1.0);
        assertEquals(0.0, quantile.evaluate(0.0001), 1.0e-12);
        assertEquals(0.5, quantile.evaluate(0.5), 1.0e-12);
        assertEquals(1.0, quantile.evaluate(1.0), 1.0e-12);

        quantile = new Quantile(0.0, 1.0, 2.0);
        assertEquals(0.0, quantile.evaluate(0.0001), 1.0e-12);
        assertEquals(1.0, quantile.evaluate(0.5), 1.0e-12);
        assertEquals(2.0, quantile.evaluate(1.0), 1.0e-12);

        quantile = new Quantile(0.0, 1.0, 2.0, 3.0);
        assertEquals(0.0, quantile.evaluate(0.0001), 1.0e-12);
        assertEquals(1.5, quantile.evaluate(0.5), 1.0e-12);
        assertEquals(3.0, quantile.evaluate(1.0), 1.0e-12);
    }

    @Test public void testLarge() {
        double[]  values = new double[1000000];
        JamRandom random = JamRandom.global(20150831L);

        for (int k = 0; k < values.length; k++)
            values[k] = random.nextDouble();

        Quantile quantile = new Quantile(values);

        for (double p = 0.1; p <= 1.0; p += 0.1)
            assertEquals(p, quantile.evaluate(p), 0.001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.QuantileTest");
    }
}
