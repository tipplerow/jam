
package jam.junit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.dist.DiscreteDistribution;
import jam.dist.DiscreteDistributionType;
import jam.dist.UniformDiscreteDistribution;
import jam.util.MultisetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class UniformDiscreteDistributionTest extends DiscreteDistributionTestBase {
    private static final double TOLERANCE = 1.0e-12;

    private final int lower = -1;
    private final int upper =  3;

    private final DiscreteDistribution dist =
        DiscreteDistributionType.parse("UNIFORM; -1, 3");

    public UniformDiscreteDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        assertDouble(0.0,  dist.cdf(-3));
        assertDouble(0.0,  dist.cdf(-2));
        assertDouble(0.25, dist.cdf(-1));
        assertDouble(0.50, dist.cdf( 0));
        assertDouble(0.75, dist.cdf( 1));
        assertDouble(1.0,  dist.cdf( 2));
        assertDouble(1.0,  dist.cdf( 3));
        assertDouble(1.0,  dist.cdf( 4));
    }

    @Test public void testMean() {
        assertDouble(0.5, dist.mean());
    }

    @Test public void testMedian() {
        assertDouble(0.5, dist.median());
    }

    @Test public void testPDF() {
        assertDouble(0.0,  dist.pdf(-3));
        assertDouble(0.0,  dist.pdf(-2));
        assertDouble(0.25, dist.pdf(-1));
        assertDouble(0.25, dist.pdf( 0));
        assertDouble(0.25, dist.pdf( 1));
        assertDouble(0.25, dist.pdf( 2));
        assertDouble(0.0,  dist.pdf( 3));
        assertDouble(0.0,  dist.pdf( 4));
    }

    @Test public void testSample() {
        Multiset<Integer> samples = HashMultiset.create();

        for (int trial = 0; trial < 100000; ++trial)
            samples.add(dist.sample());

        assertEquals(0.25, MultisetUtil.frequency(samples, -1), 0.01);
        assertEquals(0.25, MultisetUtil.frequency(samples,  0), 0.01);
        assertEquals(0.25, MultisetUtil.frequency(samples,  1), 0.01);
        assertEquals(0.25, MultisetUtil.frequency(samples,  2), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRange1() {
        new UniformDiscreteDistribution(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRange2() {
        new UniformDiscreteDistribution(1, 1);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.UniformDiscreteDistributionTest");
    }
}
