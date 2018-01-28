
package jam.dist;

import com.google.common.collect.Multiset;

import jam.math.IntRange;
import jam.math.JamRandom;

/**
 * Represents a univariate probability distribution taking integer
 * values.
 */
public interface DiscreteDistribution {
    /**
     * Computes the cumulative distribution function at a point.  For
     * a random variable {@code X} drawn from this distribution, this
     * method returns the probability {@code P(X <= k)}.
     *
     * @param k the point at which the CDF is evaluated.
     *
     * @return the probability that a random variable drawn from this
     * distribution is less than or equal to {@code k}.
     */
    public abstract double cdf(int k);

    /**
     * Computes the cumulative distribution function for a half-open
     * range.  For a random variable {@code X} drawn from this
     * distribution, this method returns {@code P(j < X <= k)}.
     *
     * @param j the greatest integer <em>not</em> in the sample range.
     *
     * @param k the greatest integer in the sample range.
     *
     * @return the probability that a random variable drawn from this
     * distribution lies in the half-open range {@code (j, k])}.
     *
     * @throws IllegalArgumentException if {@code j > k}.
     */
    public abstract double cdf(int j, int k);

    /**
     * Computes the cumulative distribution function over a closed
     * range.
     *
     * @param range the sample range.
     *
     * @return the probability that the specified range contains a
     * random variable drawn from this distribution.
     */
    public default double cdf(IntRange range) {
        return cdf(range.lower() - 1, range.upper());
    }

    /**
     * Computes the probability mass function at a point.
     *
     * @param k the point at which the PDF is evaluated.
     *
     * @return the probability mass at {@code x}.
     */
    public abstract double pdf(int k);

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
     * Returns the smallest integer range containing the median.
     *
     * @return the smallest integer range containing the median.
     */
    public default IntRange medianRange() {
        double median = median();
        return new IntRange((int) Math.floor(median), (int) Math.ceil(median));
    }

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
    public abstract int sample();

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @return the next value from this distribution.
     */
    public abstract int sample(JamRandom source);

    /**
     * Samples from this distribution using the globally shared random
     * number source.
     *
     * @param count the number of samples to generate.
     *
     * @return the next {@code count} values from this distribution.
     */
    public abstract Multiset<Integer> sample(int count);

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
    public abstract Multiset<Integer> sample(JamRandom source, int count);

    /**
     * Returns the single closed contiguous range of integers with
     * non-zero probability mass.
     *
     * @return the single closed contiguous range of integers with
     * non-zero probability mass.
     *
     * @throws IllegalStateException unless this distribution has a
     * single contiguous range of support.
     */
    public abstract IntRange support();

    /**
     * Returns the effective range of this distribution: a range that
     * omits at most one-billionth (1.0E-09) of the total probability
     * mass.
     *
     * @return the effective range of this distribution.
     */
    public default IntRange effectiveRange() {
        //
        // The effective range for a normal distribution is seven
        // standard deviations around the mean, so we use this as
        // a default rule of thumb...
        //
        double mean  = mean();
        double stdev = stdev();

        int lower = (int) Math.floor(mean - 7.0 * stdev);
        int upper = (int) Math.ceil( mean + 7.0 * stdev);

        lower = Math.max(lower, support().lower());
        upper = Math.min(upper, support().upper());
        
        return new IntRange(lower, upper);
    }

    /**
     * Summarizes this distribution in a string suitable for writing
     * to the console or a file.
     *
     * @param range the range over which to display this distribution.
     *
     * @return a summary description suitable for writing to the
     * console or a file.
     */
    public default String display(IntRange range) {
        StringBuilder builder = new StringBuilder();

        builder.append(" k      pdf        cdf   \n");
        builder.append("---  ---------  ---------\n");

        for (int k = range.lower(); k <= range.upper(); k++)
            builder.append(String.format("%3d   %8.6f   %8.6f\n", k, pdf(k), cdf(k)));

        return builder.toString();
    }

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
            System.err.println("Usage: jam.dist.DiscreteDistribution \"TYPE; param1, param2, ...\"");
            System.exit(1);
        }

        DiscreteDistribution distribution =
            DiscreteDistributionType.parse(args[0]);

        JamRandom source = JamRandom.generator();

        for (int index = 0; index < SAMPLE_COUNT; index++)
            System.err.println(distribution.sample(source));
    }
}
