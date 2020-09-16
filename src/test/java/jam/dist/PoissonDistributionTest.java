
package jam.dist;

import org.apache.commons.math3.random.Well44497b;

import org.junit.*;
import static org.junit.Assert.*;

public class PoissonDistributionTest extends DiscreteDistributionTestBase {
    private static final double TOLERANCE = 1.0e-12;
    private static final Well44497b RNG = new Well44497b(20171114);

    private final jam.dist.PoissonDistribution jam1E2 = jam(1.0E-02);
    private final jam.dist.PoissonDistribution jam1E3 = jam(1.0E-03);
    private final jam.dist.PoissonDistribution jam1E4 = jam(1.0E-04);

    private final jam.dist.PoissonDistribution jam005 = jam(0.5);
    private final jam.dist.PoissonDistribution jam025 = jam(2.5);
    private final jam.dist.PoissonDistribution jam095 = jam(9.5);
    private final jam.dist.PoissonDistribution jam999 = jam(99.9);

    private final org.apache.commons.math3.distribution.PoissonDistribution apache005 = apache(0.5);
    private final org.apache.commons.math3.distribution.PoissonDistribution apache025 = apache(2.5);
    private final org.apache.commons.math3.distribution.PoissonDistribution apache095 = apache(9.5);
    private final org.apache.commons.math3.distribution.PoissonDistribution apache999 = apache(99.9);

    private static jam.dist.PoissonDistribution jam(double mean) {
        return jam.dist.PoissonDistribution.create(mean);
    }

    private static org.apache.commons.math3.distribution.PoissonDistribution apache(double mean) {
        int    maxIter = org.apache.commons.math3.distribution.PoissonDistribution.DEFAULT_MAX_ITERATIONS;
        double epsilon = org.apache.commons.math3.distribution.PoissonDistribution.DEFAULT_EPSILON;

        return new org.apache.commons.math3.distribution.PoissonDistribution(RNG, mean, epsilon, maxIter);
    }

    public PoissonDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        compareCDF(jam005, apache005);
        compareCDF(jam025, apache025);
        compareCDF(jam095, apache095);
        compareCDF(jam999, apache999);
    }

    private void compareCDF(jam.dist.PoissonDistribution jam,
                            org.apache.commons.math3.distribution.PoissonDistribution apache) {
        for (Integer k : jam.effectiveRange())
            assertEquals(jam.cdf(k), apache.cumulativeProbability(k), TOLERANCE);
    }

    @Test public void testPDF() {
        comparePDF(jam005, apache005);
        comparePDF(jam025, apache025);
        comparePDF(jam095, apache095);
        comparePDF(jam999, apache999);
    }

    private void comparePDF(jam.dist.PoissonDistribution jam,
                            org.apache.commons.math3.distribution.PoissonDistribution apache) {
        for (Integer k : jam.effectiveRange())
            assertEquals(jam.pdf(k), apache.probability(k), TOLERANCE);
    }

    @Test public void testCache() {
        cacheTest(jam005, 1.0e-12, false);
        cacheTest(jam025, 1.0e-09, false);
        cacheTest(jam095, 1.0e-09, false);
    }

    @Test public void testEffectiveRange() {
        checkEffectiveRange(0.001);
        checkEffectiveRange(0.002);
        checkEffectiveRange(0.005);
        checkEffectiveRange(0.01);
        checkEffectiveRange(0.02);
        checkEffectiveRange(0.05);
        checkEffectiveRange(0.1);
        checkEffectiveRange(0.2);
        checkEffectiveRange(0.5);
        checkEffectiveRange(1.0);
        checkEffectiveRange(2.0);
        checkEffectiveRange(5.0);
        checkEffectiveRange(10.0);
        checkEffectiveRange(20.0);
        checkEffectiveRange(50.0);
        checkEffectiveRange(100.0);
    }

    private void checkEffectiveRange(double mean) {
        effectiveRangeTest(jam(mean), 1.0e-09, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMean() {
        jam.dist.PoissonDistribution.create(0.0);
    }

    @Test public void testMoments() {
	momentTest(jam1E4, 100000, 0.00005, 0.00005, false);
	momentTest(jam1E3, 100000, 0.0001,  0.0001,  false);
	momentTest(jam1E2, 100000, 0.001,   0.001,   false);

	momentTest(jam005, 100000, 0.005, 0.005, false);
	momentTest(jam025, 100000, 0.02,  0.02,  false);
	momentTest(jam095, 100000, 0.05,  0.05,  false);
	momentTest(jam999, 100000, 0.1,   1.0,   false);
    }

    @Test public void testParse() {
        DiscreteDistribution dist = 
            DiscreteDistributionType.parse("POISSON; 2.5");

        assertTrue(dist instanceof jam.dist.PoissonDistribution);
        assertEquals(2.5, dist.mean(), TOLERANCE);
    }

    @Test public void testSamples() {
        sampleTest(100000, 0.01, 0.01, jam005, apache005);
        sampleTest(100000, 0.05, 0.05, jam025, apache025);
        sampleTest(100000, 0.1,  0.1,  jam095, apache095);
        sampleTest(100000, 1.0,  5.0,  jam999, apache999);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.dist.PoissonDistributionTest");
    }
}
