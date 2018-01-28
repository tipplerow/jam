
package jam.math;

public final class UniformRandomSequence extends RandomSequence {
    private final double lower;
    private final double upper;

    /**
     * Creates a psuedo-random sequence of values distributed
     * uniformly on the interval {@code [lower, upper]} using the
     * global random number generator as the underlying source.
     *
     * @param lower the lower bound for the random values.
     *
     * @param upper the upper bound for the random values.
     *
     * @param generator the underlying random number generator.
     */
    public UniformRandomSequence(double lower, double upper, JamRandom generator) {
        super(generator);
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Returns the lower bound for the random values.
     *
     * @return the lower bound for the random values.
     */
    public double getLower() {
        return lower;
    }

    /**
     * Returns the upper bound for the random values.
     *
     * @return the upper bound for the random values.
     */
    public double getUpper() {
        return upper;
    }

    @Override public double next() {
        return generator.nextDouble(lower, upper);
    }
}
