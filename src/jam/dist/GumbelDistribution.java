
package jam.dist;

import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

/**
 * Represents the Gumbel distribution, the extreme value distribution
 * of type I.
 */
public final class GumbelDistribution extends GEVDistribution {
    private static final double LOGLOG2 = Math.log(Math.log(2.0));

    /**
     * Creates a Gumbel distribution
     *
     * @param location the location parameter.
     *
     * @param scale the scale parameter.
     *
     * @throws IllegalArgumentException unless the scale parameter is
     * positive.
     */
    public GumbelDistribution(double location, double scale) {
        super(location, scale, 0.0, DoubleRange.INFINITE);
    }

    @Override protected double kernel(double x) {
        return Math.exp(-(x - location) / scale);
    }

    @Override public double pdf(double x) {
        double tx = kernel(x);
        return tx * Math.exp(-tx) / scale;
    }

    @Override public double quantile(double F) {
        return location - scale * Math.log(-Math.log(F));
    }

    @Override public double mean() {
	return location + scale * DoubleUtil.EULER;
    }

    @Override public double median() {
	return location - scale * LOGLOG2;
    }

    @Override public double variance() {
	double sigma_pi = scale * Math.PI;
	return sigma_pi * sigma_pi / 6.0;
    }
}
