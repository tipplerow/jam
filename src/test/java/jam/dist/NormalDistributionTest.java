
package jam.dist;

import org.junit.*;
import static org.junit.Assert.*;

public class NormalDistributionTest extends RealDistributionTestBase {
    private static final double TOLERANCE = 1.0e-06;

    private final double mean  = -1.0;
    private final double stdev =  3.0;

    private final RealDistribution dist = 
        RealDistributionType.NORMAL.create(mean, stdev);

    public NormalDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        assertDouble(0.1586553, dist.cdf(-4.0));
        assertDouble(0.5,       dist.cdf(-1.0));
        assertDouble(0.9772499, dist.cdf( 5.0));
    }

    @Test public void testPDF() {
        assertDouble(0.08065691, dist.pdf(-4.0));
        assertDouble(0.13298076, dist.pdf(-1.0));
        assertDouble(0.01799699, dist.pdf( 5.0));
    }

    @Test public void testMoments() {
	momentTest(dist, 1000000, 0.001, 0.005, 0.005, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStDev() {
        new NormalDistribution(0.0, -1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.dist.NormalDistributionTest");
    }
}
