
package jam.math;

public abstract class RandomSequence {
    /**
     * The underlying random number generator.
     */
    protected final JamRandom generator;

    /**
     * Creates a new pseudo-random sequence with a specific generator.
     *
     * @param generator the underlying random number generator.
     */
    protected RandomSequence(JamRandom generator) {
        this.generator = generator;
    }

    /**
     * Creates a psuedo-random sequence of values distributed
     * uniformly on {@code [0.0, 1.0]} using the global random
     * number generator as the underlying source.
     *
     * @return a psuedo-random sequence of values distributed
     * uniformly on {@code [0.0, 1.0]}.
     *
     * @throws IllegalStateException unless the global generator has
     * been initialized.
     */
    public static RandomSequence uniform() {
        return uniform(0.0, 1.0);
    }

    /**
     * Creates a psuedo-random sequence of values distributed
     * uniformly on {@code [0.0, 1.0]}.
     *
     * @param generator the underlying random number generator.
     *
     * @return a psuedo-random sequence of values distributed
     * uniformly on {@code [0.0, 1.0]}.
     */
    public static RandomSequence uniform(JamRandom generator) {
        return uniform(0.0, 1.0, generator);
    }


    /**
     * Creates a psuedo-random sequence of values distributed
     * uniformly on the interval {@code [lower, upper]} using the
     * global random number generator as the underlying source.
     *
     * @param lower the lower bound for the random values.
     *
     * @param upper the upper bound for the random values.
     *
     * @return a psuedo-random sequence of values distributed
     * uniformly on {@code [lower, upper]}.
     *
     * @throws IllegalStateException unless the global generator has
     * been initialized.
     */
    public static RandomSequence uniform(double lower, double upper) {
        return uniform(lower, upper, JamRandom.global());
    }

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
     *
     * @return a psuedo-random sequence of values distributed
     * uniformly on {@code [lower, upper]}.
     */
    public static RandomSequence uniform(double lower, double upper, JamRandom generator) {
        return new UniformRandomSequence(lower, upper, generator);
    }

    /**
     * Returns the next value in this pseudo-random sequence.
     *
     * @return the next value in this pseudo-random sequence.
     */
    public abstract double next();
}
