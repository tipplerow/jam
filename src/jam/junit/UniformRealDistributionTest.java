
package jam.junit;

import jam.dist.RealDistribution;
import jam.dist.RealDistributionType;
import jam.dist.UniformRealDistribution;

import org.junit.*;
import static org.junit.Assert.*;

public class UniformRealDistributionTest extends RealDistributionTestBase {
    private static final double TOLERANCE = 1.0e-12;

    private final double lower = -1.0;
    private final double upper =  3.0;

    private final RealDistribution dist = 
        RealDistributionType.UNIFORM.create(lower, upper);

    public UniformRealDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        assertDouble(0.0,  dist.cdf(-2.0));
        assertDouble(0.0,  dist.cdf(-1.0));
        assertDouble(0.25, dist.cdf( 0.0));
        assertDouble(0.50, dist.cdf( 1.0));
        assertDouble(0.75, dist.cdf( 2.0));
        assertDouble(1.0,  dist.cdf( 3.0));
        assertDouble(1.0,  dist.cdf( 4.0));

        assertDouble(0.0,   dist.cdf(-0.5, -0.5));
        assertDouble(0.125, dist.cdf(-0.5,  0.0));
        assertDouble(0.25,  dist.cdf(-0.5,  0.5));
    }

    @Test public void testPDF() {
        assertDouble(0.0, dist.pdf(lower - TOLERANCE));
        assertDouble(0.0, dist.pdf(upper + TOLERANCE));

        assertDouble(0.25, dist.pdf(lower + TOLERANCE));
        assertDouble(0.25, dist.pdf(0.0));
        assertDouble(0.25, dist.pdf(1.0));
        assertDouble(0.25, dist.pdf(upper - TOLERANCE));
    }

    @Test public void testMoments() {
	momentTest(dist, 2000000, 0.0001, 0.002, 0.002, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRange() {
        new UniformRealDistribution(0.0, -1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.UniformRealDistributionTest");
    }
}
