
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents the Frechet distribution, the extreme value distribution
 * of type II.
 */
public final class FrechetDistribution extends GEVDistribution {
    /**
     * Creates a Frechet distribution
     *
     * @param location the location parameter.
     *
     * @param scale the scale parameter.
     *
     * @param shape the (positive) shape parameter.
     *
     * @throws IllegalArgumentException unless the scale and shape
     * parameters are positive.
     */
    public FrechetDistribution(double location, double scale, double shape) {
        super(location, scale, shape, computeRange(location, scale, shape));

        if (shape <= 0.0)
            throw new IllegalArgumentException("Frechet shape parameter must be positive.");
    }

    private static DoubleRange computeRange(double location, double scale, double shape) {
        double lower = location - scale / shape;
        double upper = Double.POSITIVE_INFINITY;

        return DoubleRange.leftClosed(lower, upper);
    }
}
