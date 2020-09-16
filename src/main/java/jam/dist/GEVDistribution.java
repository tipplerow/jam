
package jam.dist;

import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents the generalized extreme value distribution.
 */
public abstract class GEVDistribution extends AbstractRealDistribution {
    protected final double location;
    protected final double scale;
    protected final double shape;
    protected final DoubleRange range;

    /**
     * Creates a generalized extreme value distribution.
     *
     * @param location the location parameter.
     *
     * @param scale the scale parameter.
     *
     * @param shape the shape parameter.
     *
     * @param range the range of support.
     *
     * @throws IllegalArgumentException unless the scale parameter is
     * positive.
     */
    protected GEVDistribution(double location, double scale, double shape, DoubleRange range) {
        this.location = location;
        this.scale    = scale;
        this.shape    = shape;
        this.range    = range;

        if (scale <= 0.0)
            throw new IllegalArgumentException("Non-positive scale.");
    }

    /**
     * Computes the kernel function {@code t(x)} from which the CDF
     * and PDF are derived:
     *
     * <pre>
     *     CDF(x) = exp(-t(x))
     *
     *     PDF(x) = [t(x) ^ (shape + 1)] * exp(-t(x)) / scale
     * </pre>
     *
     * @param x the point at which the kernel is evaluated.
     *
     * @return the kernel evaluated at {@code x}.
     */
    protected double kernel(double x) {
        if (DoubleComparator.DEFAULT.isZero(shape))
            throw new IllegalStateException("Gumbel kernel function requires special implementation.");

        return Math.pow(1.0 + shape * (x - location) / scale, -1.0 / shape);
    }

    /**
     * Creates a generalized extreme value (GEV) distribution.
     *
     * @param location the location parameter.
     *
     * @param scale the scale parameter.
     *
     * @param shape the shape parameter.
     *
     * @return the generalized extreme value (GEV) distribution with
     * the specified parameters.
     *
     * @throws IllegalArgumentException unless the scale parameter is
     * positive.
     */
    public static GEVDistribution instance(double location, double scale, double shape) {
        if (DoubleComparator.DEFAULT.isNegative(shape))
            return new ReverseWeibullDistribution(location, scale, shape);

        if (DoubleComparator.DEFAULT.isZero(shape))
            return new GumbelDistribution(location, scale);


        return new FrechetDistribution(location, scale, shape);
    }

    /**
     * Returns the location parameter.
     *
     * @return the location parameter.
     */
    public double getLocation() {
        return location;
    }

    /**
     * Returns the scale parameter.
     *
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Returns the shape parameter.
     *
     * @return the shape parameter.
     */
    public double getShape() {
        return shape;
    }

    @Override public double cdf(double x) {
        DoubleRange range = support();

        if (x <= range.getLowerBound())
            return 0.0;

        if (x >= range.getUpperBound())
            return 1.0;

        return Math.exp(-kernel(x));
    }

    @Override public double pdf(double x) {
        if (!support().contains(x))
            return 0.0;

        double tx = kernel(x);
        return Math.pow(tx, shape + 1.0) * Math.exp(-tx) / scale;
    }

    @Override public double quantile(double F) {
        //
        // Valid for Frechet and reverse Weibull only...
        //
        if (DoubleComparator.DEFAULT.isZero(shape))
            throw new IllegalStateException("Gumbel quantile function requires special implementation.");

        return location - scale * (1.0 - Math.pow(-Math.log(F), -shape)) / shape;
    }

    @Override public DoubleRange support() {
        return range;
    }
}
