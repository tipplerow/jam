
package jam.dist;

import org.junit.*;
import static org.junit.Assert.*;

public class GumbelDistributionTest extends RealDistributionTestBase {

    @Test public void testMoments() {
	GumbelDistribution dist1 = new GumbelDistribution(-10.0, 3.0);
	GumbelDistribution dist2 = new GumbelDistribution(2.0, 0.333);

	momentTest(dist1, 1000000, 0.01, 0.01, 0.05, false);
	momentTest(dist2, 1000000, 0.001, 0.001, 0.001, false);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.dist.GumbelDistributionTest");
    }
}
