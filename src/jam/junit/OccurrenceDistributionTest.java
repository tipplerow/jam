
package jam.junit;

import jam.dist.DiscreteDistributionType;
import jam.dist.OccurrenceDistribution;
import jam.math.Factorial;
import jam.math.Probability;

import org.junit.*;
import static org.junit.Assert.*;

public class OccurrenceDistributionTest extends DiscreteDistributionTestBase {
    @Test public void test0() {
        OccurrenceDistribution dist = new OccurrenceDistribution(Probability.valueOf(0.95), 0);

        assertDouble(1.0, dist.exactly(0));

        assertDouble(1.0, dist.atLeast(-1));
        assertDouble(1.0, dist.atLeast(0));
        assertDouble(0.0, dist.atLeast(1));

        assertDouble(0.0, dist.atMost(-1));
        assertDouble(1.0, dist.atMost(0));
        assertDouble(1.0, dist.atMost(1));
    }

    @Test public void test1() {
        OccurrenceDistribution dist = new OccurrenceDistribution(Probability.valueOf(0.25), 1);

        assertDouble(0.75, dist.exactly(0));
        assertDouble(0.25, dist.exactly(1));

        assertDouble(1.0,  dist.atLeast(-1));
        assertDouble(1.0,  dist.atLeast(0));
        assertDouble(0.25, dist.atLeast(1));
        assertDouble(0.0,  dist.atLeast(2));

        assertDouble(0.0,  dist.atMost(-1));
        assertDouble(0.75, dist.atMost(0));
        assertDouble(1.0,  dist.atMost(1));
        assertDouble(1.0,  dist.atMost(2));
    }

    @Test public void test2() {
        OccurrenceDistribution dist = new OccurrenceDistribution(Probability.valueOf(0.7), 2);

        assertDouble(0.09, dist.exactly(0));
        assertDouble(0.42, dist.exactly(1));
        assertDouble(0.49, dist.exactly(2));

        assertDouble(1.0,  dist.atLeast(-1));
        assertDouble(1.0,  dist.atLeast(0));
        assertDouble(0.91, dist.atLeast(1));
        assertDouble(0.49, dist.atLeast(2));
        assertDouble(0.0,  dist.atLeast(3));

        assertDouble(0.0,  dist.atMost(-1));
        assertDouble(0.09, dist.atMost(0));
        assertDouble(0.51, dist.atMost(1));
        assertDouble(1.0,  dist.atMost(2));
        assertDouble(1.0,  dist.atMost(3));
    }

    @Test public void test6() {
        double tol = 1.0e-10;
        OccurrenceDistribution dist = new OccurrenceDistribution(Probability.valueOf(0.25), 6);

        assertEquals(0.1779785156, dist.exactly(0), tol);
        assertEquals(0.3559570312, dist.exactly(1), tol);
        assertEquals(0.2966308594, dist.exactly(2), tol);
        assertEquals(0.1318359375, dist.exactly(3), tol);
        assertEquals(0.0329589844, dist.exactly(4), tol);
        assertEquals(0.0043945312, dist.exactly(5), tol);
        assertEquals(0.0002441406, dist.exactly(6), tol);
    }

    @Test public void test20() {
        int    count = 20;
        double prob  = 0.9;
        double tol   = 1.0e-12;

        OccurrenceDistribution dist = new OccurrenceDistribution(Probability.valueOf(prob), count);

        for (int k = 0; k <= count; ++k)
            assertEquals(Math.pow(prob, k) * Math.pow(1.0 - prob, count - k) * Factorial.choose(count, k), dist.exactly(k), tol);
    }

    @Test public void testType() {
        OccurrenceDistribution dist = 
            (OccurrenceDistribution) DiscreteDistributionType.parse("OCCURRENCE; 0.2, 4");

        assertDouble(0.2, dist.getEventProb().doubleValue());
        assertEquals(4, dist.getTrialCount());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.OccurrenceDistributionTest");
    }
}
