
package jam.junit;

import org.apache.commons.math3.random.Well44497b;

import jam.dist.DiscreteDistributionType;
import jam.math.Probability;

import org.junit.*;
import static org.junit.Assert.*;

public class BinomialDistributionTest extends DiscreteDistributionTestBase {
    private static final double TOLERANCE = 1.0e-12;
    private static final Well44497b RNG = new Well44497b(20171114);

    private final jam.dist.BinomialDistribution jam0 = jam(0, 0.5);

    private final jam.dist.BinomialDistribution jam_01_20 = jam(1, 0.2);
    private final jam.dist.BinomialDistribution jam_01_60 = jam(1, 0.6);

    private final jam.dist.BinomialDistribution jam_05_10 = jam(5, 0.10);
    private final jam.dist.BinomialDistribution jam_05_50 = jam(5, 0.50);

    private final jam.dist.BinomialDistribution jam_30_50 = jam(30, 0.50);
    private final jam.dist.BinomialDistribution jam_30_90 = jam(30, 0.90);

    private final jam.dist.BinomialDistribution jam_95_25 = jam(95, 0.25);
    private final jam.dist.BinomialDistribution jam_95_60 = jam(95, 0.60);

    private final org.apache.commons.math3.distribution.BinomialDistribution apache0 = apache(0, 0.5);

    private final org.apache.commons.math3.distribution.BinomialDistribution apache_01_20 = apache(1, 0.20);
    private final org.apache.commons.math3.distribution.BinomialDistribution apache_01_60 = apache(1, 0.60);

    private final org.apache.commons.math3.distribution.BinomialDistribution apache_05_10 = apache(5, 0.10);
    private final org.apache.commons.math3.distribution.BinomialDistribution apache_05_50 = apache(5, 0.50);

    private final org.apache.commons.math3.distribution.BinomialDistribution apache_30_50 = apache(30, 0.50);
    private final org.apache.commons.math3.distribution.BinomialDistribution apache_30_90 = apache(30, 0.90);

    private final org.apache.commons.math3.distribution.BinomialDistribution apache_95_25 = apache(95, 0.25);
    private final org.apache.commons.math3.distribution.BinomialDistribution apache_95_60 = apache(95, 0.60);

    private static jam.dist.BinomialDistribution jam(int count, double prob) {
        return jam.dist.BinomialDistribution.create(count, Probability.valueOf(prob));
    }

    private static org.apache.commons.math3.distribution.BinomialDistribution apache(int count, double prob) {
        return new org.apache.commons.math3.distribution.BinomialDistribution(RNG, count, prob);
    }

    public BinomialDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testImplementation() {
        assertEquals("BinomialDistributionExact", jam0.getClass().getSimpleName());

        assertEquals("BinomialDistributionExact", jam_01_20.getClass().getSimpleName());
        assertEquals("BinomialDistributionExact", jam_01_60.getClass().getSimpleName());

        assertEquals("BinomialDistributionExact", jam_05_10.getClass().getSimpleName());
        assertEquals("BinomialDistributionExact", jam_05_50.getClass().getSimpleName());

        assertEquals("BinomialDistributionApprox", jam_30_50.getClass().getSimpleName());
        assertEquals("BinomialDistributionExact",  jam_30_90.getClass().getSimpleName());

        assertEquals("BinomialDistributionApprox", jam_95_25.getClass().getSimpleName());
        assertEquals("BinomialDistributionApprox", jam_95_60.getClass().getSimpleName());
    }

    @Test public void testCDF() {
        compareCDF(jam0, apache0);

        compareCDF(jam_01_20, apache_01_20);
        compareCDF(jam_01_60, apache_01_60);

        compareCDF(jam_05_10, apache_05_10);
        compareCDF(jam_05_50, apache_05_50);

        compareCDF(jam_30_50, apache_30_50);
        compareCDF(jam_30_90, apache_30_90);

        compareCDF(jam_95_25, apache_95_25);
        compareCDF(jam_95_60, apache_95_60);
    }

    private void compareCDF(jam.dist.BinomialDistribution jam,
                            org.apache.commons.math3.distribution.BinomialDistribution apache) {
        for (Integer k : jam.effectiveRange())
            assertEquals(jam.cdf(k), apache.cumulativeProbability(k), 1.0E-06);
    }

    @Test public void testPDF() {
        comparePDF(jam0, apache0);

        comparePDF(jam_01_20, apache_01_20);
        comparePDF(jam_01_60, apache_01_60);

        comparePDF(jam_05_10, apache_05_10);
        comparePDF(jam_05_50, apache_05_50);

        comparePDF(jam_30_50, apache_30_50);
        comparePDF(jam_30_90, apache_30_90);

        comparePDF(jam_95_25, apache_95_25);
        comparePDF(jam_95_60, apache_95_60);
    }

    private void comparePDF(jam.dist.BinomialDistribution jam,
                            org.apache.commons.math3.distribution.BinomialDistribution apache) {
        for (Integer k : jam.effectiveRange())
            assertEquals(jam.pdf(k), apache.probability(k), 1.0E-06);
    }

    @Test public void testCache() {
        cacheTest(jam_01_20, 1.0e-12, false);
        cacheTest(jam_01_60, 1.0e-12, false);
        cacheTest(jam_05_10, 1.0e-12, false);
        cacheTest(jam_05_50, 1.0e-12, false);
        cacheTest(jam_30_50, 1.0e-12, false);
        cacheTest(jam_30_90, 1.0e-12, false);
    }

    @Test public void testEffectiveRange() {
        checkEffectiveRange(0);
        checkEffectiveRange(1);
        checkEffectiveRange(10);
        checkEffectiveRange(100);
        checkEffectiveRange(1000);
    }

    private void checkEffectiveRange(int trialCount) {
        effectiveRangeTest(jam(trialCount, 0.02), 1.0e-09, false);
        effectiveRangeTest(jam(trialCount, 0.20), 1.0e-09, false);
        effectiveRangeTest(jam(trialCount, 0.50), 1.0e-09, false);
        effectiveRangeTest(jam(trialCount, 0.80), 1.0e-09, false);
        effectiveRangeTest(jam(trialCount, 0.98), 1.0e-09, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTrialCount() {
        jam.dist.BinomialDistribution.create(-1, Probability.valueOf(0.5));
    }

    @Test public void testMoments() {
	momentTest(jam0, 100, 1.0E-12, 1.0E-12, false);

        momentTest(jam_01_20, 100000, 0.001, 0.001, false);
        momentTest(jam_01_60, 100000, 0.005, 0.001, false);

        momentTest(jam_05_10, 100000, 0.001, 0.002, false);
        momentTest(jam_05_50, 100000, 0.05,  0.002, false);

        momentTest(jam_30_50, 100000, 0.01,  0.2,   false);
        momentTest(jam_30_90, 100000, 0.005, 0.005, false);

        momentTest(jam_95_25, 100000, 0.01, 0.1,  false);
        momentTest(jam_95_60, 100000, 0.01, 0.05, false);
    }

    @Test public void testParse() {
        jam.dist.BinomialDistribution dist = 
            (jam.dist.BinomialDistribution) DiscreteDistributionType.parse("BINOMIAL; 5, 0.2");

        assertTrue(dist instanceof jam.dist.BinomialDistribution);
        assertEquals(5, dist.getTrialCount());
        assertDouble(0.2, dist.getSuccessProb().doubleValue());
    }

    @Test public void testSamples() {
        sampleTest(1000, 0.0001, 0.0001, jam0, apache0);

        sampleTest(100000, 0.005, 0.005, jam_01_20, apache_01_20);
        sampleTest(100000, 0.005, 0.005, jam_01_60, apache_01_60);

        sampleTest(100000, 0.05, 0.05, jam_05_10, apache_05_10);
        sampleTest(100000, 0.05, 0.05, jam_05_50, apache_05_50);

        sampleTest(100000, 0.2, 0.2, jam_30_50, apache_30_50);
        sampleTest(100000, 0.2, 0.2, jam_30_90, apache_30_90);

        sampleTest(100000, 0.5, 0.5, jam_95_25, apache_95_25);
        sampleTest(100000, 0.5, 0.5, jam_95_60, apache_95_60);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BinomialDistributionTest");
    }
}
