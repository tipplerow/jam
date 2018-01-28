
package jam.dist;

import jam.math.JamRandom;

/**
 * Represents a probability distribution in which the logarithm of the
 * random variable is distributed uniformly within a fixed range.
 */
public final class LogUniformDistribution extends LogDerivedDistribution {
    private final UniformRealDistribution uniform;

    /**
     * Creates a log-uniform probability distribution for a fixed
     * logarithmic range.
     *
     * @param lowerLog the lower bound of the logarithm of the random
     * variable.
     *
     * @param upperLog the upper bound of the logarithm of the random
     * variable.
     *
     * @throws IllegalArgumentException if {@code lower >= upper}.
     */
    public LogUniformDistribution(double lowerLog, double upperLog) {
        this.uniform = new UniformRealDistribution(lowerLog, upperLog);
    }

    /**
     * Returns the lower bound of the logarithm of the random variable.
     *
     * @return the lower bound of the logarithm of the random variable.
     */
    public double getLowerLog() {
        return uniform.getLower();
    }

    /**
     * Returns the upper bound of the logarithm of the random variable.
     *
     * @return the upper bound of the logarithm of the random variable.
     */
    public double getUpperLog() {
        return uniform.getUpper();
    }

    @Override protected RealDistribution getLogDistribution() {
        return uniform;
    }
}
