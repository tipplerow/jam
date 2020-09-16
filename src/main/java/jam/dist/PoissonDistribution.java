
package jam.dist;

import java.util.Arrays;

import org.apache.commons.math3.special.Gamma;

import jam.math.DoubleRange;
import jam.math.IntRange;
import jam.math.JamRandom;

/**
 * Implements a discrete Poisson distribution.
 */
public abstract class PoissonDistribution extends AbstractDiscreteDistribution {
    private final double mean;

    // Use the Knuth sampling algorithm for mean values above this
    // limit...
    private static final double KNUTH_MEAN_LIMIT = 1.0;

    // Use a normal approximation for mean values above this limit...
    private static final double NORMAL_MEAN_LIMIT = 50.0;

    /**
     * The range of valid mean values.
     */
    public static final DoubleRange MEAN_RANGE = DoubleRange.POSITIVE;

    /**
     * Creates a discrete Poisson distribution.
     *
     * @param mean the mean of the distribution.
     *
     * @throws IllegalArgumentException unless the mean is positive.
     */
    protected PoissonDistribution(double mean) {
        MEAN_RANGE.validate("Mean", mean);
        this.mean = mean;
    }

    private static void validateMean(double mean) {
        if (mean <= 0.0)
            throw new IllegalArgumentException("Mean must be positive.");
    }

    /**
     * Creates a discrete Poisson distribution.
     *
     * @param mean the mean of the distribution.
     *
     * @return the new Poisson distribution.
     *
     * @throws IllegalArgumentException unless the mean is positive.
     */
    public static PoissonDistribution create(double mean) {
        if (mean < KNUTH_MEAN_LIMIT)
            return new PoissonDistributionExact(mean);
        else if (mean < NORMAL_MEAN_LIMIT)
            return new PoissonDistributionKnuth(mean);
        else
            return new PoissonDistributionNormal(mean);
    }

    /**
     * Computes the probability mass function for a Poisson
     * distribution with a given mean.
     *
     * @param k the point at which to evaluate the probability.
     *
     * @param mean the mean of the distribution.
     *
     * @return the probability mass function for a Poisson
     * distribution with the given mean.
     */
    public static double pdf(int k, double mean) {
        if (k < 0)
            return 0.0;
        else
            return Math.exp(logPDF(k, mean));
    }

    /**
     * Computes the <em>logarithm</em> of the probability mass
     * function for a Poisson distribution with a given mean.
     *
     * @param k the point at which to evaluate the log-probability.
     *
     * @param mean the mean of the distribution.
     *
     * @return the <em>logarithm</em> of the probability mass function
     * for a Poisson distribution with the given mean.
     */
    public static double logPDF(int k, double mean) {
        validateMean(mean);

        if (k < 0)
            return Double.NEGATIVE_INFINITY;
        else
            return -mean + k * Math.log(mean) - Gamma.logGamma(k + 1);
    }

    @Override public IntRange effectiveRange() {
        int lower;
        int upper;

        if (mean < 0.01) {
            lower = 0;
            upper = 3;
        }
        else if (mean < 0.1) {
            lower = 0;
            upper = 6;
        }
        else if (mean < 1.0) {
            lower = 0;
            upper = 12;
        }
        else if (mean < 10.0) {
            lower = 0;
            upper = (int) Math.ceil(12.0 * stdev());
        }
        else {
            lower = Math.max(0, (int) Math.floor(mean - 8.0 * stdev()));
            upper = (int) Math.ceil(mean + 8.0 * stdev());
        }

        return IntRange.instance(lower, upper);
    }

    @Override public double pdf(int k) {
        return pdf(k, mean);
    }

    @Override public double mean() {
	return mean;
    }

    @Override public double median() {
        //
        // Approximate expression...
        //
	return Math.max(0.0, Math.floor(mean() + 1.0 / 3.0 - 0.02 / mean()));
    }

    @Override public double variance() {
	return mean();
    }

    @Override public IntRange support() {
        return IntRange.NON_NEGATIVE;
    }
}

final class PoissonDistributionNormal extends PoissonDistribution {
    private final NormalDistribution normal;

    PoissonDistributionNormal(double mean) {
        super(mean);
        this.normal = new NormalDistribution(mean, Math.sqrt(mean));
    }

    @Override public int sample(JamRandom source) {
        return (int) Math.round(normal.sample(source));
    }
}

final class PoissonDistributionKnuth extends PoissonDistribution {
    private final int maxit;
    private final double accept;

    PoissonDistributionKnuth(double mean) {
        super(mean);

        this.maxit = (int) Math.round(1000.0 * mean);
        this.accept = Math.exp(-mean);

        if (this.maxit < 1)
            throw new IllegalStateException("Knuth sample is not appropriate for very small mean values.");
    }

    @Override public int sample(JamRandom source) {
        //
        // Algorithm attributed to Knuth...
        //
        int    count = 0;
        double draw  = source.nextDouble();

        while (draw > accept && count < maxit) {
            ++count;
            draw *= source.nextDouble();
        }
            
        return count;
    }
}

final class PoissonDistributionExact extends PoissonDistribution {
    private final double[] sampleCDF;

    PoissonDistributionExact(double mean) {
        super(mean);
        this.sampleCDF = computeSampleCDF(mean);
    }

    private static double[] computeSampleCDF(double mean) {
        //
        // Choose an initial length "L" sure to be larger than the
        // effective maximum value (the value of "k" for which the
        // cumulative distribution is within the default tolerance
        // of 1.0)...
        //
        double[] CDF = new double[100];
        CDF[0] = pdf(0, mean);

        for (int k = 1; k < CDF.length; ++k) {
            CDF[k] = CDF[k - 1] + pdf(k, mean);

            if (1.0 - CDF[k] < 1.0E-15) {
                CDF[k + 1] = 1.0;
                return Arrays.copyOf(CDF, k + 2);
            }
        }

        throw new IllegalStateException("The mean value is too large for explicit CDF sampling.");
    }

    @Override public int sample(JamRandom source) {
        return source.selectCDF(sampleCDF);
    }
}
