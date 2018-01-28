
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents a univariate probability distribution over the real
 * numbers.
 */
public interface RealDistribution {
    /**
     * Computes the cumulative distribution function at a point.  For
     * a random variable {@code X} drawn from this distribution, this
     * method returns the probability {@code P(X <= x)}.
     *
     * @param x the point at which the CDF is evaluated.
     *
     * @return the probability that a random variable drawn from this
     * distribution is less than or equal to {@code x}.
     */
    public abstract double cdf(double x);

    /**
     * Computes the cumulative distribution function for a range.
     *
     * @param lower the lower bound of the range.
     *
     * @param upper the upper bound of the range.
     *
     * @return the probability that a random variable drawn from this
     * distribution lies in the open range {@code (lower, upper]}.
     *
     * @throws IllegalArgumentException if {@code lower > upper}.
     */
    public abstract double cdf(double lower, double upper);

    /**
     * Computes the probability density function at a point.
     *
     * @param x the point at which the PDF is evaluated.
     *
     * @return the probability density at {@code x}.
     */
    public abstract double pdf(double x);

    /**
     * Computes the quantile (inverse CDF) function.
     *
     * <p>The quantile function {@code Q(F)} is the point which the
     * cumulative distribution function takes value {@code F}.
     *
     * @param F the cumulative probability
     *
     * @return the quantile (inverse CDF) function evaluated at the
     * given cumulative probability.
     */
    public abstract double quantile(double F);

    /**
     * Returns the mean value of this distribution.
     *
     * @return the mean value of this distribution.
     */
    public abstract double mean();

    /**
     * Returns the median value of this distribution.
     *
     * @return the median value of this distribution.
     */
    public abstract double median();

    /**
     * Returns the standard deviation this distribution.
     *
     * @return the standard deviation of this distribution.
     */
    public abstract double stdev();

    /**
     * Returns the variance of this distribution.
     *
     * @return the variance of this distribution.
     */
    public abstract double variance();

    /**
     * Samples from this distribution using the globally shared random
     * number source.
     *
     * @return the next value from this distribution.
     */
    public abstract double sample();

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @return the next value from this distribution.
     */
    public abstract double sample(JamRandom source);

    /**
     * Samples from this distribution using the globally shared random
     * number source.
     *
     * @param count the number of samples to generate.
     *
     * @return the next {@code count} values from this distribution.
     */
    public abstract double[] sample(int count);

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @param count the number of samples to generate.
     *
     * @return the next {@code count} values from this distribution.
     */
    public abstract double[] sample(JamRandom source, int count);

    /**
     * Returns the range of non-zero probability density.
     *
     * @return the range of non-zero probability density.
     */
    public abstract DoubleRange support();

    /**
     * Sample a series of random deviates and write them to standard
     * error to faciliatate analysis of their statistical properties,
     * say in R.
     *
     * @param args a single string argument defining the probability
     * distribution in the format: {@code TYPE; param1, param2, ...},
     * where {@code TYPE} is the enumerated type code and {@code
     * param1, param2, ...} are the comma-separated parameters
     * required to define a distribution of the specified type.
     *
     * @throws IllegalArgumentException unless the input string
     * defines a valid probability distribution.
     */
    public static void main(String[] args) {
        final int SAMPLE_COUNT = 100000;

        if (args.length != 1) {
            System.err.println("Usage: jam.dist.RealDistribution \"TYPE; param1, param2, ...\"");
            System.exit(1);
        }

        RealDistribution distribution =
            RealDistributionType.parse(args[0]);

        JamRandom source = JamRandom.generator();

        for (int index = 0; index < SAMPLE_COUNT; index++)
            System.err.println(distribution.sample(source));
    }
}
