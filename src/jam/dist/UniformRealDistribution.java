
package jam.dist;

import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

/**
 * Represents a uniform probability distribution over a fixed range.
 */
public final class UniformRealDistribution extends AbstractRealDistribution {
    private final double lower;
    private final double upper;
    private final double density;
    private final DoubleRange support;

    /**
     * Creates a uniform probability distribution for a fixed range.
     *
     * @param lower the lower bound of the range.
     *
     * @param upper the upper bound of the range.
     *
     * @throws IllegalArgumentException if {@code lower >= upper}.
     */
    public UniformRealDistribution(double lower, double upper) {
        validateRange(lower, upper);

        this.lower = lower;
        this.upper = upper;

        this.density = computeDensity(lower, upper);
        this.support = DoubleRange.closed(lower, upper);
    }

    private static void validateRange(double lower, double upper) {
        if (lower >= upper)
            throw new IllegalArgumentException("Invalid range.");
    }

    private static double computeDensity(double lower, double upper) {
        return 1.0 / (upper - lower);
    }

    /**
     * Returns the lower bound of the fixed range.
     *
     * @return the lower bound of the fixed range.
     */
    public double getLower() {
        return lower;
    }

    /**
     * Returns the upper bound of the fixed range.
     *
     * @return the upper bound of the fixed range.
     */
    public double getUpper() {
        return upper;
    }

    @Override public double cdf(double x) {
        if (x <= lower)
            return 0.0;

        if (x >= upper)
            return 1.0;

        return density * (x - lower);
    }

    @Override public double pdf(double x) {
        if (x < lower)
            return 0.0;

        if (x > upper)
            return 0.0;

        return density;
    }

    @Override public double quantile(double F) {
        return lower + (upper - lower) * F;
    }

    @Override public double mean() {
	return 0.5 * (lower + upper);
    }

    @Override public double median() {
	return mean();
    }

    @Override public double variance() {
	return DoubleUtil.square(upper - lower) / 12.0;
    }

    @Override public double sample(JamRandom source) {
        return source.nextDouble(lower, upper);
    }

    @Override public DoubleRange support() {
        return support;
    }
}
