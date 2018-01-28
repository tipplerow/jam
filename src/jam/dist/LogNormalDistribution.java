
package jam.dist;

import jam.math.JamRandom;

/**
 * Represents a probability distribution in which the logarithm of the
 * random variable is normally distributed.
 */
public final class LogNormalDistribution extends LogDerivedDistribution {
    private final NormalDistribution normal;

    /**
     * Creates a log-normal probability distribution.
     *
     * @param meanLog the mean of the logarithm of the random
     * variable.
     *
     * @param stdevLog the standard deviation of the logarithm of the
     * random variable.
     *
     * @throws IllegalArgumentException unless the standard deviation
     * is positive.
     */
    public LogNormalDistribution(double meanLog, double stdevLog) {
        this.normal = new NormalDistribution(meanLog, stdevLog);
    }

    @Override public double mean() {
	double meanLog  = getMeanLog();
	double stdevLog = getStDevLog();
	double varLog   = stdevLog * stdevLog;

	return Math.exp(meanLog + 0.5 * varLog);
    }

    @Override public double variance() {
	double meanLog  = getMeanLog();
	double stdevLog = getStDevLog();
	double varLog   = stdevLog * stdevLog;

	return Math.exp(varLog - 1.0) * Math.exp(2.0 * meanLog + varLog);
    }

    /**
     * Returns the mean of the logarithm of this distribution.
     *
     * @return the mean of the logarithm of this distribution.
     */
    public double getMeanLog() {
        return normal.mean();
    }

    /**
     * Returns the standard deviation of the logarithm of this distribution.
     *
     * @return the standard deviation of the logarithm of this distribution.
     */
    public double getStDevLog() {
        return normal.stdev();
    }

    @Override protected RealDistribution getLogDistribution() {
        return normal;
    }
}
