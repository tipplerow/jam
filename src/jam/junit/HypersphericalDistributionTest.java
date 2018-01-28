
package jam.junit;

import jam.dist.HypersphericalDistribution;
import jam.math.StatUtil;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class HypersphericalDistributionTest extends MultivariateDistributionTestBase {
    private static final double RADIUS = 2.0;
    private static final JamVector CENTER = JamVector.valueOf(-1.0, 0.0, 2.0);

    public HypersphericalDistributionTest() {
        super(new HypersphericalDistribution(RADIUS, CENTER));
    }

    @Test public void testMoments() {
        momentTest(0.01, 0.01, false);
    }

    @Test public void testRadius() {
        for (int sample = 0; sample < samples.length; sample++)
            assertDouble(RADIUS, StatUtil.norm2(samples[sample].minus(CENTER)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDimensionality() {
        new HypersphericalDistribution(1, 10.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRadius() {
        new HypersphericalDistribution(3, 0.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HypersphericalDistributionTest");
    }
}
