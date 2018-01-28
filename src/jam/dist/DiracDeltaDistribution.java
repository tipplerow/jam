
package jam.dist;

import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents a Dirac delta distribution at a fixed impulse location.
 */
public final class DiracDeltaDistribution extends AbstractRealDistribution {
    private final double impulse;

    /**
     * Creates a Dirac delta distribution for a fixed impulse
     * location.
     *
     * @param impulse the location of the impulse.
     */
    public DiracDeltaDistribution(double impulse) {
        this.impulse = impulse;
    }

    /**
     * Returns the location of the impulse.
     *
     * @return the location of the impulse.
     */
    public double getImpulse() {
        return impulse;
    }

    @Override public double cdf(double x) {
        if (x < impulse)
            return 0.0;
        else if (x == impulse)
            return 0.5;
        else
            return 1.0;
    }

    @Override public double pdf(double x) {
        if (x == impulse)
            return Double.POSITIVE_INFINITY;
        else
            return 0.0;
    }

    @Override public double quantile(double F) {
        throw new UnsupportedOperationException("Quantile not well defined for Dirac delta distribution.");
    }

    @Override public double mean() {
	return impulse;
    }

    @Override public double median() {
	return impulse;
    }

    @Override public double variance() {
	return 0.0;
    }

    @Override public double sample(JamRandom source) {
        return impulse;
    }

    @Override public DoubleRange support() {
        return DoubleRange.closed(impulse, impulse);
    }
}
