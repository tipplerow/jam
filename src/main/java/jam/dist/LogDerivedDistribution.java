
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents a probability distribution in which the logarithm of the
 * random variable is described by another distribution.
 */
public abstract class LogDerivedDistribution extends AbstractRealDistribution {
    /**
     * Returns the distribution of the logarithm of the random
     * variable.
     *
     * @return the distribution of the logarithm of the random
     * variable.
     */
    protected abstract RealDistribution getLogDistribution();

    @Override public double cdf(double x) {
        return getLogDistribution().cdf(Math.log(x));
    }

    @Override public double median() {
	return Math.exp(getLogDistribution().median());
    }

    @Override public double pdf(double x) {
        return getLogDistribution().pdf(Math.log(x)) / x;
    }

    @Override public double quantile(double F) {
        return Math.exp(getLogDistribution().quantile(F));
    }

    @Override public double sample(JamRandom source) {
        return Math.exp(getLogDistribution().sample(source));
    }

    @Override public DoubleRange support() {
        return DoubleRange.POSITIVE;
    }
}
