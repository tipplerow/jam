
package jam.math;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import jam.vector.VectorView;

/**
 * Computes quantiles for numeric data.
 *
 * <p>The quantile calculator operates on a fixed set of numerical
 * data, specified when the calculator is created.  Once created,
 * users may compute any number of quantile positions from that
 * original data set.
 */
public final class Quantile {
    private final Percentile percentile;

    /**
     * Creates a new quantile calculator.
     *
     * @param values the data on which to operate.
     */
    public Quantile(double... values) {
        percentile = new Percentile();
        percentile.setData(values);
    }

    /**
     * Creates a new quantile calculator.
     *
     * @param values the data on which to operate.
     */
    public Quantile(VectorView values) {
        this(values.toNumeric());
    }

    /**
     * Computes the location of a specified quantile.
     *
     * @param quantile the desired quantile location.
     *
     * @return an estimate of the specified quantile.
     *
     * @throws IllegalArgumentException unless the quantile is in the
     * valid range {@code (0, 1]}.
     */
    public double evaluate(double quantile) {
        validateQuantile(quantile);
        return percentile.evaluate(100.0 * quantile);
    }

    /**
     * Computes the locations of specified quantiles.
     *
     * @param quantiles the desired quantile locations.
     *
     * @return estimates of the specified quantiles.
     *
     * @throws IllegalArgumentException unless all quantiles are in
     * the valid range {@code (0, 1]}.
     */
    public double[] evaluate(double... quantiles) {
        double[] result = new double[quantiles.length];

        for (int k = 0; k < quantiles.length; k++)
            result[k] = evaluate(quantiles[k]);

        return result;
    }

    private static void validateQuantile(double quantile) {
        if (quantile <= 0.0 || quantile > 1.0)
            throw new IllegalArgumentException("Invalid quantile.");
    }
}
