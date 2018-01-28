
package jam.junit;

import jam.dist.RealDistribution;
import jam.dist.RealDistributionType;

import org.junit.*;
import static org.junit.Assert.*;

public class DiracDeltaDistributionTest extends RealDistributionTestBase {
    private static final double TOLERANCE = 1.0e-12;

    private final double impulse = 3.33;

    private final RealDistribution dist = 
        RealDistributionType.DIRAC_DELTA.create(impulse);

    public DiracDeltaDistributionTest() {
        super(TOLERANCE);
    }

    @Test public void testCDF() {
        assertDouble(0.0, dist.cdf(impulse - TOLERANCE));
        assertDouble(0.5, dist.cdf(impulse));
        assertDouble(1.0, dist.cdf(impulse + TOLERANCE));
    }

    @Test public void testPDF() {
        assertDouble(0.0, dist.pdf(impulse - TOLERANCE));
        assertDouble(0.0, dist.pdf(impulse + TOLERANCE));
    }

    @Test public void testMoments() {
	momentTest(dist, 10000, TOLERANCE, TOLERANCE, TOLERANCE, false);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DiracDeltaDistributionTest");
    }
}
