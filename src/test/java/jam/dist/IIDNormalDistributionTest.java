
package jam.dist;

import jam.math.VectorMoment;
import jam.matrix.JamMatrix;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class IIDNormalDistributionTest extends MultivariateDistributionTestBase {
    private static final int    NVAR  =  3;
    private static final double MEAN  = -1.0;
    private static final double STDEV =  0.5;

    private final JamVector expectedMean = 
        JamVector.valueOf(MEAN, MEAN, MEAN);

    private final JamMatrix expectedCovar =
        JamMatrix.byrow(3, 3,
                        STDEV * STDEV, 0.0, 0.0,
                        0.0, STDEV * STDEV, 0.0,
                        0.0, 0.0, STDEV * STDEV);

    public IIDNormalDistributionTest() {
        super(new IIDNormalDistribution(NVAR, MEAN, STDEV));
    }

    @Test public void testDim() {
        assertEquals(NVAR, dist.dim());
    }

    @Test public void testMean() {
        assertEquals(expectedMean, dist.mean());
    }

    @Test public void testCovar() {
        assertEquals(expectedCovar, dist.covar());
    }

    @Test public void testMoments() {
        momentTest(0.001, 0.01, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDim() {
        new IIDNormalDistribution(0, 1.0, 10.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStDev() {
        new IIDNormalDistribution(3, 0.0, 0.0);
    }

    public static void main(String[] args) {
    }
}
