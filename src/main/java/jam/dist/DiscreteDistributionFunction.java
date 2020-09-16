
package jam.dist;

import jam.math.IntRange;
import jam.vector.VectorUtil;

/**
 * Represents the cumulative probability distribution (CDF) or
 * probability density function (PDF) for a discrete probability
 * distribution.
 */
public abstract class DiscreteDistributionFunction {
    /**
     * The range of support for the distribution.
     */
    protected final IntRange support;

    /**
     * The values of the distribution function for each point in the
     * range of support, where {@code values[k]} contains the value at
     * {@code support.lower() + k}.
     */
    protected final double[] values;

    /**
     * Creates a new distribution function.
     *
     * @param support the range of support for the distribution.
     *
     * @param values the values of the distribution function for each
     * point in the range of support; {@code values[k]} must contain
     * the value at location {@code support.lower() + k}.
     *
     * @throws IllegalArgumentException unless the number of values is
     * equal to the number of points in the range of support.
     */
    protected DiscreteDistributionFunction(IntRange support, double[] values) {
        validate(support, values);

        this.support = support;
        this.values  = values;
    }

    private static void validate(IntRange support, double[] values) {
        if (values.length != support.size())
            throw new IllegalArgumentException("Values to not span the range of support.");
    }

    /**
     * Summarizes this distribution in a string suitable for writing
     * to the console or a file.
     *
     * @return a summary description suitable for writing to the
     * console or a file.
     */
    public String display() {
        StringBuilder builder = new StringBuilder();

        for (int k : support)
            builder.append(String.format("%6d => %8.6f\n", k, evaluate(k)));

        return builder.toString();
    }

    /**
     * Evaluates this distribution function at a given location.
     *
     * @param k the point at which to evaluate this distribution.
     *
     * @return the value of this distribution function at the
     * specified location.
     *
     * @throws IllegalArgumentException unless the point falls within
     * the range of support.
     */
    public double evaluate(int k) {
        if (!support.contains(k))
            throw new IllegalArgumentException("Evaluation point is outside the range of support.");

        return values[indexOf(k)];
    }

    private int indexOf(int k) {
        return k - support.lower();
    }

    /**
     * Returns the range of support for this distribution function.
     *
     * @return the range of support for this distribution function.
     */
    public IntRange support() {
        return support;
    }

    /**
     * Compares this distribution function with another.
     *
     * @param that the other distribution function.
     *
     * @param tolerance the floating-point tolerance for equality
     * comparison.
     *
     * @return {@code true} iff this distribution function has the
     * same support range as the input function and its values are
     * equal within the specified tolerance.
     */
    protected boolean equalsDistribution(DiscreteDistributionFunction that, double tolerance) {
        return this.support.equals(that.support) && VectorUtil.equals(this.values, that.values, tolerance);
    }

    @Override public String toString() {
        return display();
    }
}
