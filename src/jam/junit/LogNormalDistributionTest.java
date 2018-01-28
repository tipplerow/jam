
package jam.junit;

import jam.dist.LogNormalDistribution;
import jam.dist.RealDistribution;
import jam.dist.RealDistributionType;

import org.junit.*;
import static org.junit.Assert.*;

public class LogNormalDistributionTest extends RealDistributionTestBase {
    private static final double TOLERANCE = 1.0e-06;

    private final double meanLog  = -1.0;
    private final double stdevLog =  0.5;

    private final RealDistribution dist = 
        RealDistributionType.LOG_NORMAL.create(meanLog, stdevLog);

    public LogNormalDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        assertDouble(0.004591432, dist.cdf(0.1));
        assertDouble(0.730295069, dist.cdf(0.5));
        assertDouble(0.977249868, dist.cdf(1.0));
    }

    @Test public void testPDF() {
        assertDouble(0.2680285, dist.pdf(0.1));
        assertDouble(1.3218583, dist.pdf(0.5));
        assertDouble(0.1079819, dist.pdf(1.0));
    }

    @Test public void testMoments() {
	momentTest(dist, 1000000, 0.0002, 0.0002, 0.04, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStDev() {
        new LogNormalDistribution(0.0, -1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LogNormalDistributionTest");
    }
}
