
package jam.dist;

import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

/**
 * Represents a continuous exponential probability distribution.
 */
public final class ExponentialDistribution extends AbstractRealDistribution {
    private final double rate;

    private final double mean;
    private final double median;
    private final double variance;

    /**
     * Creates an exponential distribution with a fixed rate parameter.
     *
     * @param rate the rate parameter for the distribution.
     *
     * @throws IllegalArgumentException unless the rate parameter is
     * positive.
     */
    public ExponentialDistribution(double rate) {
        validateRate(rate);

        this.rate = rate;
        this.mean = 1.0 / rate;
        this.median = DoubleUtil.LOG2 * mean;
        this.variance = mean * mean;
    }

    /**
     * Computes the cumulative distribution function for an
     * exponential distribution with a given rate parameter.
     *
     * @param x the location at which to evaluate the CDF.
     *
     * @param rate the rate parameter for the distribution.
     *
     * @return the cumulative distribution function for an exponential
     * distribution with the given rate parameter.
     *
     * @throws IllegalArgumentException unless the rate parameter is
     * positive.
     */
    public static double staticCDF(double x, double rate) {
        validateRate(rate);
        return validCDF(x, rate);
    }

    private static double validCDF(double x, double rate) {
        if (x < 0.0)
            return 0.0;
        else
            return 1.0 - Math.exp(-rate * x);
    }

    /**
     * Computes the probability distribution function for an
     * exponential distribution with a given rate parameter.
     *
     * @param x the location at which to evaluate the CDF.
     *
     * @param rate the rate parameter for the distribution.
     *
     * @return the probability distribution function for an exponential
     * distribution with the given rate parameter.
     *
     * @throws IllegalArgumentException unless the rate parameter is
     * positive.
     */
    public static double pdf(double x, double rate) {
        validateRate(rate);
        return validPDF(x, rate);
    }

    private static double validPDF(double x, double rate) {
        if (x < 0.0)
            return 0.0;
        else
            return rate * Math.exp(-rate * x);
    }

    /**
     * Samples a value from an exponential distribution with a given
     * rate parameter.
     *
     * @param rate the rate parameter for the distribution.
     *
     * @param source a source of uniform random deviates.
     *
     * @return a random deviate sampled from an exponential
     * distribution with the specified rate parameter.
     *
     * @throws IllegalArgumentException unless the rate parameter is
     * positive.
     */
    public static double sample(double rate, JamRandom source) {
        validateRate(rate);
        return sampleValid(rate, source);
    }

    private static double sampleValid(double rate, JamRandom source) {
        return -Math.log(source.nextDouble()) / rate;
    }

    /**
     * Validates a rate parameter for an exponential distribution.
     *
     * @param rate the rate parameter to validate.
     *
     * @throws IllegalArgumentException unless the rate parameter is
     * positive.
     */
    public static void validateRate(double rate) {
        if (!DoubleComparator.DEFAULT.isPositive(rate))
            throw new IllegalArgumentException("Rate parameter must be positive.");
    }

    /**
     * Returns the rate parameter for this exponential distribution.
     *
     * @return the rate parameter for this exponential distribution.
     */
    public double rate() {
        return rate;
    }

    @Override public double cdf(double x) {
        return validCDF(x, rate);
    }

    @Override public double pdf(double x) {
        return validPDF(x, rate);
    }

    @Override public double quantile(double F) {
        return -Math.log(1.0 - F) * mean;
    }

    @Override public double mean() {
	return mean;
    }

    @Override public double median() {
	return median;
    }

    @Override public double variance() {
	return variance;
    }

    @Override public double sample(JamRandom source) {
        return sample(rate, source);
    }

    @Override public DoubleRange support() {
        return DoubleRange.NON_NEGATIVE;
    }
}
