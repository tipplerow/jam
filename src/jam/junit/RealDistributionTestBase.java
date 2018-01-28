
package jam.junit;

import jam.dist.RealDistribution;
import jam.math.StatSummary;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class RealDistributionTestBase extends NumericTestBase {
    protected RealDistributionTestBase() {
        super();
    }

    protected RealDistributionTestBase(double tolerance) {
        super(tolerance);
    }

    public void momentTest(RealDistribution distribution, 
                           int sampleCount, 
                           double meanTolerance,
                           double medianTolerance,
                           double varianceTolerance,
                           boolean verbose) {

	StatSummary summary = StatSummary.compute(distribution.sample(random(), sampleCount));

	double meanError     = Math.abs(distribution.mean()     - summary.getMean());
	double medianError   = Math.abs(distribution.median()   - summary.getMedian());
	double varianceError = Math.abs(distribution.variance() - summary.getVariance());

	if (verbose) {
	    System.out.println(String.format("Mean error:     %12.8f", meanError));
	    System.out.println(String.format("Median error:   %12.8f", medianError));
	    System.out.println(String.format("Variance error: %12.8f", varianceError));
	}

	assertTrue(meanError     <= meanTolerance);
	assertTrue(medianError   <= medianTolerance);
	assertTrue(varianceError <= varianceTolerance);
    }
}
