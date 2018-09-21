
package jam.vector;

import jam.math.StatUtil;

/**
 * Computes scalar aggregate values over vectors.
 *
 * <p>Aaggregators take a vector as input and return a scalar
 * aggregate quantity as output; the input vector is unchanged.
 */
public abstract class VectorAggregator {
    /**
     * Aggregates numeric data.
     *
     * @param values the values to aggregate.
     *
     * @return the aggregate value.
     */
    public abstract double compute(VectorView values);

    /**
     * The maximum-value aggregator.
     */
    public static final VectorAggregator MAX =
	new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.max(values);
            }
        };

    /**
     * The mean-value aggregator.
     */
    public static final VectorAggregator MEAN =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.mean(values);
            }
        };

    /**
     * The mean-square aggregator.
     */
    public static final VectorAggregator MEANSQR =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.meansqr(values);
            }
        };

    /**
     * The median-absolute aggregator.
     */
    public static final VectorAggregator MEDABS =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.medabs(values);
            }
        };

    /**
     * The minimum-value aggregator.
     */
    public static final VectorAggregator MIN =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.min(values);
            }
        };

    /**
     * The 1-norm aggregator.
     */
    public static final VectorAggregator NORM1 =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.norm1(values);
            }
        };

    /**
     * The 2-norm aggregator.
     */
    public static final VectorAggregator NORM2 =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.norm2(values);
            }
        };

    /**
     * The infinity norm aggregator.
     */
    public static final VectorAggregator NORM_INF =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.normInf(values);
            }
        };

    /**
     * The standard deviation aggregator.
     */
    public static final VectorAggregator SD =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.stdev(values);
            }
        };

    /**
     * The sum aggregator.
     */
    public static final VectorAggregator SUM =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.sum(values);
            }
        };

    /**
     * The sum-of-squares aggregator.
     */
    public static final VectorAggregator SUMSQR =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.sumsqr(values);
            }
        };

    /**
     * The variance aggregator.
     */
    public static final VectorAggregator VARIANCE =
        new VectorAggregator() {
            @Override public double compute(VectorView values) {
                return StatUtil.variance(values);
            }
        };

    /**
     * Computes the maximum value of a vector; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the maximum value.
     */
    public static double max(VectorView values) {
        return MAX.compute(values);
    }

    /**
     * Computes the mean value of a vector; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the mean value.
     */
    public static double mean(VectorView values) {
        return MEAN.compute(values);
    }

    /**
     * Computes the mean of the squared values in a vector; missing
     * values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the mean squared value.
     */
    public static double meansqr(VectorView values) {
        return MEANSQR.compute(values);
    }

    /**
     * Computes the median of the absolute values in a vector; missing
     * values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the median absolute value.
     */
    public static double medabs(VectorView values) {
        return MEDABS.compute(values);
    }

    /**
     * Computes the minimum value of a vector; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the minimum value.
     */
    public static double min(VectorView values) {
        return MIN.compute(values);
    }

    /**
     * Computes the 1-norm of a vector; missing values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the 1-norm.
     */
    public static double norm1(VectorView values) {
        return NORM1.compute(values);
    }

    /**
     * Computes the 2-norm of a vector; missing values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the 2-norm.
     */
    public static double norm2(VectorView values) {
        return NORM2.compute(values);
    }

    /**
     * Computes the infinity norm of a vector (its maximum absolute
     * value); missing values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the infinity norm.
     */
    public static double normInf(VectorView values) {
        return NORM_INF.compute(values);
    }

    /**
     * Computes the standard deviation of a vector; missing
     * values are excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the standard deviation.
     */
    public static double sd(VectorView values) {
        return SD.compute(values);
    }

    /**
     * Computes the sum of vector elements; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the sum of vector elements.
     */
    public static double sum(VectorView values) {
        return SUM.compute(values);
    }

    /**
     * Computes the sum of squared vector elements; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the sum of squared vector elements.
     */
    public static double sumsqr(VectorView values) {
        return SUMSQR.compute(values);
    }

    /**
     * Computes the variance of a vector; missing values are
     * excluded.
     *
     * @param values the values to aggregate.
     *
     * @return the variance.
     */
    public static double variance(VectorView values) {
        return VARIANCE.compute(values);
    }
}
