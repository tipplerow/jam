
package jam.junit;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.StatSummary;
import jam.math.JamRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class StatSummaryTest extends NumericTestBase {
    private final JamRandom RANDOM = JamRandom.global(20151202L);

    @Test public void testAttribute() {
        List<String> strings = Arrays.asList("Permanent", "Waves", "Moving", "Pictures", "Signals");
        StatSummary  summary = StatSummary.compute(strings, x -> Double.valueOf(x.length()));

        assertEquals(5.0, summary.getMin(),    1.0E-12);
        assertEquals(7.0, summary.getMedian(), 1.0E-12);
        assertEquals(7.0, summary.getMean(),   1.0E-12);
        assertEquals(9.0, summary.getMax(),    1.0E-12);

        assertEquals(1.581139, summary.getSD(), 1.0E-06);
    }

    @Test public void testGaussian() {
        double mean = 10.0;
        double sdev =  2.0;

        double[] values = new double[1000000];

        for (int k = 0; k < values.length; k++)
            values[k] = RANDOM.nextGaussian(mean, sdev);

        StatSummary summary = StatSummary.compute(values);

        assertTrue(summary.getMin() < mean - 4.0 * sdev);
        assertTrue(summary.getMax() > mean + 4.0 * sdev);

        assertEquals(mean, summary.getMedian(), 0.005);
        assertEquals(mean, summary.getMean(),   0.005);

        assertEquals(sdev, summary.getSD(), 0.01);
        assertEquals(sdev, summary.getMAD(), 0.01);

        double[] zscores = new double[100000];

        for (int k = 0; k < zscores.length; ++k)
            zscores[k] = summary.zScore(RANDOM.nextGaussian(mean, sdev));

        StatSummary zSummary = StatSummary.compute(zscores);

        assertEquals(0.0, zSummary.getMean(), 0.01);
        assertEquals(0.0, zSummary.getMedian(), 0.01);
        assertEquals(1.0, zSummary.getSD(), 0.01);
        assertEquals(1.0, zSummary.getMAD(), 0.01);
    }

    @Test public void testLogNormal() {
        double M = -1.0;
        double S =  1.0;

        double mean   = Math.exp(M + 0.5 * S * S);
        double sdev   = mean * Math.sqrt(Math.exp(S * S) - 1.0);
        double median = Math.exp(M);

        double[] values = new double[1000000];

        for (int k = 0; k < values.length; k++)
            values[k] = Math.exp(RANDOM.nextGaussian(M, S));

        StatSummary summary = StatSummary.compute(values);

        assertEquals(mean,   summary.getMean(),   0.01);
        assertEquals(sdev,   summary.getSD(),     0.02);
        assertEquals(median, summary.getMedian(), 0.002);
    }

    @Test public void testMultiset1() {
        Multiset<Integer> values = HashMultiset.create();

        values.add(1);
        values.add(2);
        values.add(2);
        values.add(3);
        values.add(3);
        values.add(3);
        values.add(3);
        values.add(3);

        StatSummary summary = StatSummary.compute(values);

        assertDouble(2.5, summary.getMean());
        assertDouble(3.0, summary.getMedian());
        assertEquals(0.755929, summary.getSD(), 1.0E-06);
    }

    @Test public void testMultiset2() {
        List<String> universe = List.of("A", "B", "C", "D", "E");
        Multiset<String> counts = HashMultiset.create();

        counts.add("B", 1);
        counts.add("C", 2);
        counts.add("D", 4);
        counts.add("E", 8);

        StatSummary summary = StatSummary.compute(counts, universe);

        assertDouble(2.0, summary.getMedian());
        assertDouble(3.0, summary.getMean());
        assertEquals(3.162278, summary.getSD(), 1.0E-06);
    }
        
    @Test public void testUniform() {
        double[] values = new double[1000000];

        for (int k = 0; k < values.length; k++)
            values[k] = RANDOM.nextDouble();

        StatSummary summary = StatSummary.compute(values);

        assertEquals(0.0, summary.getMin(), 0.0001);
        assertEquals(1.0, summary.getMax(), 0.0001);

        assertEquals(0.5, summary.getMedian(), 0.001);
        assertEquals(0.5, summary.getMean(), 0.001);

        assertEquals(0.25, summary.getQuartile1(), 0.002);
        assertEquals(0.75, summary.getQuartile3(), 0.002);
        assertEquals(0.50, summary.getInterQuartileRange(), 0.002);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StatSummaryTest");
    }
}
