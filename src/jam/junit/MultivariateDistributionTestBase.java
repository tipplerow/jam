
package jam.junit;

import jam.dist.MultivariateDistribution;
import jam.math.VectorMoment;
import jam.matrix.JamMatrix;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class MultivariateDistributionTestBase extends NumericTestBase {
    protected final MultivariateDistribution dist;
    protected final JamVector[] samples;

    public final static int SAMPLE_COUNT = 1000000;

    protected MultivariateDistributionTestBase(MultivariateDistribution dist) {
        this(dist, DEFAULT_TOLERANCE);
    }

    protected MultivariateDistributionTestBase(MultivariateDistribution dist, double tolerance) {
        super(tolerance);
        this.dist = dist;
        this.samples = dist.sample(random(), SAMPLE_COUNT);
    }

    public void momentTest(double tolCM, double tolRG, boolean verbose) {
        VectorMoment moment = VectorMoment.compute(samples);

        if (verbose) {
            //System.out.println(JamVector.minus(moment.getCM(), dist.mean()));
            //System.out.println(JamMatrix.minus(moment.getRG(), dist.covar()));
            System.out.println(moment.getCM());
            System.out.println(dist.mean());
            System.out.println(moment.getRG());
            System.out.println(dist.covar());
        }

        assertTrue(moment.getCM().equalsVector(dist.mean(),  tolCM));
        assertTrue(moment.getRG().equalsMatrix(dist.covar(), tolRG));
    }
}
