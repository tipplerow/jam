
package jam.junit;

import com.google.common.collect.HashMultiset;

import jam.app.JamProperties;
import jam.dist.EmpiricalDiscreteDistribution;
import jam.math.DoubleUtil;
import jam.math.StatUtil;
import jam.math.StatSummary;
import jam.math.JamRandom;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class JamRandomTest {
    private static final JamRandom SOURCE = JamRandom.generator(20071202);

    @Test public void testAccept() {
        assertEquals(0.0, computeAcceptanceRate(SOURCE, 0.0, 10000), 1.0E-15);
        assertEquals(0.1, computeAcceptanceRate(SOURCE, 0.1, 10000), 0.01);
        assertEquals(0.8, computeAcceptanceRate(SOURCE, 0.8, 10000), 0.01);
        assertEquals(1.0, computeAcceptanceRate(SOURCE, 1.0, 10000), 1.0E-15);
    }

    private double computeAcceptanceRate(JamRandom random, double acceptanceProb, int sampleSize) {
        int accepted = 0;

        for (int index = 0; index < sampleSize; index++)
            if (random.accept(acceptanceProb))
                ++accepted;

        return DoubleUtil.ratio(accepted, sampleSize);
    }

    @Test public void testDiscretize() {
        EmpiricalDiscreteDistribution dist;

        dist = discretize(2.25);
        assertEquals(0.75, dist.pdf(2), 0.003);
        assertEquals(0.25, dist.pdf(3), 0.003);

        dist = discretize(-5.4);
        assertEquals(0.40, dist.pdf(-6), 0.002);
        assertEquals(0.60, dist.pdf(-5), 0.002);

        dist = discretize(10.0);
        assertEquals(1.0, dist.pdf(10), 1.0e-12);

        dist = discretize(-2.0);
        assertEquals(1.0, dist.pdf(-2), 1.0e-12);
    }

    private static EmpiricalDiscreteDistribution discretize(double x) {
        HashMultiset<Integer> observations = HashMultiset.create();

        for (int k = 0; k < 100000; ++k)
            observations.add((int) SOURCE.discretize(x));

        return EmpiricalDiscreteDistribution.compute(observations);
    }

    @Test public void testNextDouble() {
        int count = 1000000;
        JamVector values = new JamVector(count);

        double lower = -5.0;
        double upper = 10.0;
        double tol   =  0.01;

        for (int k = 0; k < count; k++)
            values.set(k, SOURCE.nextDouble(lower, upper));

	StatSummary summary = StatSummary.compute(values);

	assertEquals(-5.0, summary.getMin(), tol);
	assertEquals(10.0, summary.getMax(), tol);

	assertEquals(2.50, summary.getMean(), tol);
	assertEquals(2.50, summary.getMedian(), tol);
	assertEquals(4.33, summary.getSD(), tol);

	assertEquals(-1.25, summary.getQuartile1(), tol);
	assertEquals( 6.25, summary.getQuartile3(), tol);
    }

    @Test public void testNextGaussian() {
        int count = 2000000;
        JamVector values = new JamVector(count);

        double mean  = -5.0;
        double stdev = 10.0;
        double tol   =  0.01;

        for (int k = 0; k < count; k++)
            values.set(k, SOURCE.nextGaussian(mean, stdev));

        assertEquals(mean,  StatUtil.mean(values),  tol);
        assertEquals(stdev, StatUtil.stdev(values), tol);
    }

    @Test public void testNextInt1() {
        int RANGE  = 88;
        int count  = 100000;
        int minval = Integer.MAX_VALUE;
        int maxval = Integer.MIN_VALUE;

        for (int k = 0; k < count; k++) {
            int next = SOURCE.nextInt(RANGE);

            minval = Math.min(minval, next);
            maxval = Math.max(maxval, next);
        }

        assertEquals(0, minval);
        assertEquals(RANGE - 1, maxval);
    }

    @Test public void testNextInt2() {
        int LOWER  = -5;
        int UPPER  = 15;
        int count  = 100000;
        int minval = Integer.MAX_VALUE;
        int maxval = Integer.MIN_VALUE;

        for (int k = 0; k < count; k++) {
            int next = SOURCE.nextInt(LOWER, UPPER);

            minval = Math.min(minval, next);
            maxval = Math.max(maxval, next);
        }

        assertEquals(LOWER, minval);
        assertEquals(UPPER - 1, maxval);
    }

    @Test public void testRandomSeed() {
	//
	// Admittedly not a stringent test...
	//
	long seed1 = JamRandom.randomSeed();
	long seed2 = 0;

	for (int k = 0; k < 100; k++)
	    seed2 = JamRandom.randomSeed();

	assertTrue(seed1 != seed2);
    }

    @Test public void testSelectCDF() {
        double[] targetPDF = new double[] { 0.1, 0.2, 0.3, 0.4 };
        double[] targetCDF = new double[] { 0.1, 0.3, 0.6, 1.0 };
        double[] actualPDF = runSelectCDF(SOURCE, targetCDF, 100000);

        for (int index = 0; index < targetPDF.length; index++)
            assertEquals(targetPDF[index], actualPDF[index], 0.01);
    }

    private double[] runSelectCDF(JamRandom random, double[] eventCDF, int sampleSize) {
        int[] eventCount = new int[eventCDF.length];

        for (int sampleIndex = 0; sampleIndex < sampleSize; sampleIndex++)
            ++eventCount[random.selectCDF(eventCDF)];

        double[] result = new double[eventCDF.length];

        for (int eventIndex = 0; eventIndex < eventCDF.length; eventIndex++)
            result[eventIndex] = DoubleUtil.ratio(eventCount[eventIndex], sampleSize);

        return result;
    }

    @Test public void testSelectPDF() {
        double[] targetPDF = new double[] { 0.1, 0.2, 0.3, 0.4 };
        double[] actualPDF = runSelectPDF(SOURCE, targetPDF, 100000);

        for (int index = 0; index < targetPDF.length; index++)
            assertEquals(targetPDF[index], actualPDF[index], 0.01);
    }

    private double[] runSelectPDF(JamRandom random, double[] eventPDF, int sampleSize) {
        int[] eventCount = new int[eventPDF.length];

        for (int sampleIndex = 0; sampleIndex < sampleSize; sampleIndex++)
            ++eventCount[random.selectPDF(eventPDF)];

        double[] result = new double[eventPDF.length];

        for (int eventIndex = 0; eventIndex < eventPDF.length; eventIndex++)
            result[eventIndex] = DoubleUtil.ratio(eventCount[eventIndex], sampleSize);

        return result;
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamRandomTest");
    }
}
