
package jam.dist;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.DoubleComparator;
import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.math.IntUtil;
import jam.math.Probability;
import jam.vector.JamVector;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

/**
 * Represents the probability density function (PDF) for a discrete
 * probability distribution.
 */
public final class DiscretePDF extends DiscreteDistributionFunction {
    private DiscretePDF(IntRange support, double[] values) {
        super(support, values);
        validate(values);
    }

    private static void validate(double[] values) {
        Probability.validate(VectorView.wrap(values));
    }

    /**
     * Computes the PDF and CDF of another discrete distribution over
     * its effective range and returns a new cached distribution that
     * is <em>nearly equivalent</em> to the input distribution.
     *
     * <p>The new distribution will differ from the input distribution
     * by less than one part per billion.
     *
     * @param dist the distribution to cache.
     *
     * @return a new distribution with the PDF and CDF pre-computed
     * over the effective range of the input distribution.
     */
    public static DiscretePDF cache(DiscreteDistribution dist) {
        IntRange  range = dist.effectiveRange();
        JamVector PDF   = new JamVector(range.size());

        for (int k = 0; k < PDF.length(); ++k)
            PDF.set(k, dist.pdf(range.lower() + k));

        PDF.normalize();
        return create(range.lower(), PDF);
    }

    /**
     * Creates a new discrete distribution function with fixed values.
     *
     * @param lower the lower bound of the range of support.
     *
     * @param PDF the fixed probability density values, where {@code
     * PDF[k]} is the probability for observation {@code lower + k}.
     *
     * @return the discrete distribution function.
     */
    public static DiscretePDF create(int lower, double[] PDF) {
        IntRange support = IntRange.instance(lower, lower + PDF.length - 1);
        double[] values  = VectorUtil.copy(PDF); // Defensive copy

        return new DiscretePDF(support, values);
    }

    /**
     * Creates a new discrete distribution function with fixed values.
     *
     * @param lower the lower bound of the range of support.
     *
     * @param PDF the fixed probability density values, where 
     * {@code PDF.getDouble(k)} is the probability for observation 
     * {@code lower + k}.
     *
     * @return the discrete distribution function.
     */
    public static DiscretePDF create(int lower, VectorView PDF) {
        return create(lower, PDF.toNumeric());
    }

    /**
     * Computes the discrete probability density function describing a
     * set of empirical observations.
     *
     * @param observations the observations to describe.
     *
     * @return the discrete probability density function describing
     * the given data.
     */
    public static DiscretePDF compute(Collection<Integer> observations) {
        return compute(IntUtil.count(observations));
    }

    /**
     * Computes the discrete probability density function describing a
     * set of empirical observations.
     *
     * @param observations the observations to describe.
     *
     * @return the discrete probability density function describing
     * the given data.
     */
    public static DiscretePDF compute(int... observations) {
        return compute(IntUtil.count(observations));
    }

    /**
     * Computes the discrete probability density function describing a
     * set of empirical observations.
     *
     * @param counts the observations to describe, grouped in a bag
     * (multiset).
     *
     * @return the discrete probability density function describing
     * the given data.
     *
     * @throws IllegalArgumentException if there are no observations.
     */
    public static DiscretePDF compute(Multiset<Integer> counts) {
        if (counts.isEmpty())
            throw new IllegalArgumentException("At least one observation is required.");

        int lower = Collections.min(counts.elementSet());
        int upper = Collections.max(counts.elementSet());

        IntRange support = IntRange.instance(lower, upper);
        double[] values  = new double[support.size()];

        for (int observation : support)
            values[observation - lower] = 
                DoubleUtil.ratio(counts.count(observation), counts.size());

        return new DiscretePDF(support, values);
    }

    /**
     * Returns the CDF corresponding to this distribution.
     *
     * @return the CDF corresponding to this distribution.
     */
    public DiscreteCDF cdf() {
        return DiscreteCDF.compute(this);
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
        if (support().contains(k))
            return super.evaluate(k);
        else
            return 0.0;
    }

    /**
     * Returns the mean value for this probability density.
     *
     * @return the mean value for this probability density.
     */
    public double mean() {
        double result = 0.0;

        for (int k : support())
            result += evaluate(k) * k;

        return result;
    }

    /**
     * Returns the variance for this probability density.
     *
     * @return the variance for this probability density.
     */
    public double variance() {
        double mean = mean();
        double result = 0.0;

        for (int k : support())
            result += evaluate(k) * DoubleUtil.square(k - mean);

        return result;
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
    public boolean equals(DiscretePDF that, double tolerance) {
        return equalsDistribution(that, tolerance);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DiscretePDF) && equals((DiscretePDF) that, DoubleComparator.DEFAULT_TOLERANCE);
    }
}
