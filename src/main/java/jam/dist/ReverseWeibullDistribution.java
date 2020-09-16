
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents the reverse Weibull distribution, the extreme value
 * distribution of type III.
 */
public final class ReverseWeibullDistribution extends GEVDistribution {
    /**
     * Creates a reverse Weibull distribution
     *
     * @param location the location parameter.
     *
     * @param scale the scale parameter.
     *
     * @param shape the (negative) shape parameter.
     *
     * @throws IllegalArgumentException unless the scale parameter is
     * positive and the shape parameter is negative.
     */
    public ReverseWeibullDistribution(double location, double scale, double shape) {
        super(location, scale, shape, computeRange(location, scale, shape));

        if (shape >= 0.0)
            throw new IllegalArgumentException("Reverse Weibull shape parameter must be negative.");
    }

    private static DoubleRange computeRange(double location, double scale, double shape) {
        double lower = Double.NEGATIVE_INFINITY;
        double upper = location - scale / shape;

        return DoubleRange.leftOpen(lower, upper);
    }
}
