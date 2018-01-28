
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;
import jam.math.StatSummary;

/**
 * Provides a skeleton implementation of the {@code RealDistribution}
 * interface.
 */
public abstract class AbstractRealDistribution implements RealDistribution {
    //
    // Number of samples used to compute mean, median, or variance
    // when analytical values are not available.
    //
    private static final int SAMPLE_COUNT = 1000000;

    /**
     * Computes a standard Z-score.
     *
     * @param x an observation.
     *
     * @param mean the mean of a real distribution.
     *
     * @param stdev the standard deviation of a real distribution.
     *
     * @return the standard Z-score: {@code (x - mean) / stdev}.
     */
    public static double scoreZ(double x, double mean, double stdev) {
        validateStDev(stdev);
        return (x - mean) / stdev;
    }

    /**
     * Validates a standard deviation.
     *
     * @param stdev the standard deviation to validate.
     *
     * @throws IllegalArgumentException unless the standard deviation
     * is positive.
     */
    public static void validateStDev(double stdev) {
        if (stdev <= 0.0)
            throw new IllegalArgumentException("Non-positive standard deviation.");
    }

    @Override public double cdf(double lower, double upper) {
        DoubleRange.validate(lower, upper);
        return cdf(upper) - cdf(lower);
    }

    @Override public double mean() {
	return sampleSummary().getMean();
    }

    @Override public double median() {
	return sampleSummary().getMedian();
    }

    @Override public double stdev() {
	return Math.sqrt(variance());
    }

    @Override public double variance() {
	return sampleSummary().getVariance();
    }

    private StatSummary sampleSummary() {
	return StatSummary.compute(sample(SAMPLE_COUNT));
    }

    @Override public double sample(JamRandom source) {
        return quantile(source.nextDouble());
    }

    @Override public double sample() {
        return sample(JamRandom.global());
    }

    @Override public double[] sample(int count) {
        return sample(JamRandom.global(), count);
    }

    @Override public double[] sample(JamRandom source, int count) {
        double[] values = new double[count];

        for (int index = 0; index < values.length; index++)
            values[index] = sample(source);

        return values;
    }
}
