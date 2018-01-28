
package jam.dist;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.DoubleComparator;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.math.Probability;
import jam.vector.VectorUtil;

/**
 * Represents a discrete cumulative probability distribution function (CDF).
 */
public final class DiscreteCDF extends DiscreteDistributionFunction {
    private DiscreteCDF(IntRange support, double[] values) {
        super(support, values);
        validate(values);
    }

    private static void validate(double[] values) {
        //
        // All values must be valid probabilities...
        //
        for (double value : values)
            Probability.validate(value);

        // They must be non-decreasing...
        if (!DoubleComparator.DEFAULT.isNonDecreasing(values))
            throw new IllegalArgumentException("Values are non-decreasing.");

        // And the last value must be 1.0...
        if (!DoubleComparator.DEFAULT.EQ(1.0, values[values.length - 1]))
            throw new IllegalArgumentException("Distribution is not normalized.");
    }

    /**
     * Computes the discrete cumulative distribution function
     * describing a set of empirical observations.
     *
     * @param observations the observations to describe.
     *
     * @return the discrete cumulative distribution function
     * describing the given data.
     */
    public static DiscreteCDF compute(int... observations) {
        return compute(DiscretePDF.compute(observations));
    }

    /**
     * Computes the discrete cumulative distribution function
     * corresponding to a discrete density function.
     *
     * @param pdf a discrete probability density function.
     *
     * @return the discrete cumulative distribution function
     * corresponding to the given probability density function.
     */
    public static DiscreteCDF compute(DiscretePDF pdf) {
        IntRange support = pdf.support();
        double[] values  = new double[support.size()];

        values[0] = pdf.evaluate(support.lower());

        for (int k = 1; k < support.size(); ++k)
            values[k] = values[k - 1] + pdf.evaluate(support.lower() + k);

        return new DiscreteCDF(support, values);
    }

    /**
     * Evaluates this distribution function at a given location.
     *
     * @param k the point at which to evaluate this distribution.
     *
     * @return the value of this distribution function at the
     * specified location.
     */
    public double evaluate(int k) {
        if (k < support().lower())
            return 0.0;
        else if (k > support().upper())
            return 1.0;
        else
            return super.evaluate(k);
    }

    /**
     * Evaluates this distribution function for a half-open range.
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
    public double evaluate(int j, int k) {
        if (j > k)
            throw new IllegalArgumentException("Invalid range.");

        return evaluate(k) - evaluate(j);
    }

    /**
     * Computes the inverse cumulative distribution.
     *
     * @param cdf the target CDF value.
     *
     * @return the observation {@code k} containing the specified
     * cumulative distribution value.
     *
     * @throws IllegalArgumentException unless the target CDF is in
     * the valid range {@code [0.0, 1.0]}.
     */
    public int inverse(double cdf) {
        Probability.validate(cdf);

        // A binary search would be more efficient since the CDF
        // values are sorted in increasing order, but the corner 
        // cases are not as transparent.  Instead, move forward 
        // while 
        for (int observation : support())
            if (DoubleComparator.DEFAULT.LE(cdf, evaluate(observation)))
                return observation;

        // Cannot happen for a valid CDF...
        throw new IllegalStateException("Invalid CDF.");
    }

    /**
     * Returns the median for this discrete cumulative distribution
     * function.
     *
     * @return the median for this discrete cumulative distribution
     * function.
     */
    public double median() {
        int observation = inverse(0.5);

        if (DoubleComparator.DEFAULT.EQ(evaluate(observation), 0.5))
            return 0.5 * (observation + inverse(0.50000001)); // Break the tie...
        else
            return observation;
    }

    /**
     * Returns the PDF corresponding to this cumulative distribution.
     *
     * @return the PDF corresponding to this cumulative distribution.
     */
    public DiscretePDF pdf() {
        double[] pdf = new double[values.length];
        pdf[0] = values[0];

        for (int k = 1; k < values.length; ++k)
            pdf[k] = values[k] - values[k - 1];

        return DiscretePDF.create(support().lower(), pdf);
    }

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @return the next value from this distribution.
     */
    public int sample(JamRandom source) {
        return inverse(source.nextDouble());
    }

    /**
     * Samples repeatedly from this distribution using a specified
     * random number source.
     *
     * @param source the source of uniform random deviates.
     *
     * @param count the number of samples to generate.
     *
     * @return a multiset containing the observations sampled.
     */
    public Multiset<Integer> sample(JamRandom source, int count) {
        Multiset<Integer> samples = HashMultiset.create();

        for (int index = 0; index < count; ++index)
            samples.add(sample(source));

        return samples;
    }

    /**
     * Compares this distribution function to another, allowing for a
     * specific floating-point tolerance.
     *
     * @param that the other distribution function.
     *
     * @param tolerance the floating-point tolerance.
     *
     * @return {@code true} iff this distribution function has the
     * same support range as the input function and its values are
     * equal within the specified tolerance.
     */
    public boolean equals(DiscreteCDF that, double tolerance) {
        return equalsDistribution(that, tolerance);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DiscreteCDF) && equals((DiscreteCDF) that, DoubleComparator.DEFAULT_TOLERANCE);
    }
}
