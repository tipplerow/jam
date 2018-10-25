
package jam.dist;

import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.math.JamRandom;

/**
 * Implements a uniform discrete probability distribution.
 */
public final class UniformDiscreteDistribution extends AbstractDiscreteDistribution {
    private final int lower; // Inclusive
    private final int upper; // Exclusive
    private final double density;
    private final IntRange support;

    /**
     * Creates a discrete distribution with integer values distributed
     * uniformly on the interval {@code (lower, upper]}.
     *
     * @param lower the lower bound of the interval (inclusive).
     *
     * @param upper the upper bound of the interval (exclusive).
     *
     * @throws IllegalArgumentException unless the interval is valid.
     */
    public UniformDiscreteDistribution(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;

        this.support = new IntRange(lower, upper - 1);

        if (support.size() < 1)
            throw new IllegalArgumentException("Invalid support range.");

        this.density = DoubleUtil.ratio(1, support.size());
    }

    /**
     * Returns the lower bound of the interval (inclusive).
     *
     * @return the lower bound of the interval (inclusive).
     */
    public int getLower() {
        return lower;
    }

    /**
     * Returns the upper bound of the interval (exclusive).
     *
     * @return the upper bound of the interval (exclusive).
     */
    public int getUpper() {
        return upper;
    }

    @Override public double cdf(int k) {
        if (k < lower)
            return 0.0;
        else if (k < upper)
            return density * (k + 1 - lower);
        else
            return 1.0;
    }

    @Override public double pdf(int k) {
        if (k < lower)
            return 0.0;
        else if (k < upper)
            return density;
        else
            return 0.0;
    }

    @Override public double mean() {
	return 0.5 * (lower + upper - 1);
    }

    @Override public double median() {
        return mean();
    }

    @Override public IntRange medianRange() {
        return new IntRange((int) Math.floor(mean()), (int) Math.ceil(mean()));
    }

    @Override public IntRange support() {
        return support;
    }

    @Override public int sample(JamRandom random) {
        return random.nextInt(lower, upper);
    }
}
