
package jam.vector;

import jam.junit.NumericTestBase;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorAggregatorTest extends NumericTestBase {
    private static final JamVector vector =
	JamVector.valueOf(-2.0, -1.0, Double.NaN, 10.0, 20.0);

    @Test public void testMin() {
        assertDouble(-2.0, VectorAggregator.min(vector));
    }

    @Test public void testMax() {
        assertDouble(20.0, VectorAggregator.max(vector));
    }

    @Test public void testMean() {
        assertDouble(6.75, VectorAggregator.mean(vector));
    }

    @Test public void testNorm1() {
        assertDouble(33.0, VectorAggregator.norm1(vector));
    }

    @Test public void testNorm2() {
        assertEquals(22.47221, VectorAggregator.norm2(vector), 0.00001);
    }

    @Test public void testNormInf() {
        assertDouble(20.0, VectorAggregator.normInf(vector));
    }

    @Test public void testSum() {
	assertDouble(27.0, VectorAggregator.sum(vector));
    }

    @Test public void testSd() {
        assertEquals(10.37224, VectorAggregator.sd(vector), 0.00001);
    }

    @Test public void testVariance() {
        assertEquals(107.5833, VectorAggregator.variance(vector), 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.vector.VectorAggregatorTest");
    }
}
