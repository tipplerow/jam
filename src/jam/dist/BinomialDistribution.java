
package jam.dist;

import jam.math.DoubleComparator;
import jam.math.DoubleUtil;
import jam.math.Factorial;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.math.Probability;

/**
 * Implements the binomial distribution describing the number of
 * successful trials in a sequence of {@code N} independent trials
 * where each trial has probability {@code p} of success.
 */
public abstract class BinomialDistribution extends AbstractDiscreteDistribution {
    private final int trialCount;
    private final Probability successProb;

    /**
     * Creates a binomial distribution.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @throws IllegalArgumentException if the trial count is
     * negative.
     */
    protected BinomialDistribution(int trialCount, Probability successProb) {
        validateTrialCount(trialCount);

        this.trialCount  = trialCount;
        this.successProb = successProb;
    }

    private static void validateTrialCount(int trialCount) {
        if (trialCount < 0)
            throw new IllegalArgumentException("Trial count cannot be negative.");
    }

    /**
     * Creates a binomial distribution.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @return the new binomial distribution.
     *
     * @throws IllegalArgumentException if the trial count is
     * negative.
     */
    public static BinomialDistribution create(int trialCount, Probability successProb) {
        if (useApprox(trialCount, successProb))
            return new BinomialDistributionApprox(trialCount, successProb);
        else
            return new BinomialDistributionExact(trialCount, successProb);
    }

    private static boolean useApprox(int trialCount, Probability successProb) {
        //
        // Always use the exact implementation for fewer than ten
        // trials...
        //
        if (trialCount < 10)
            return false;

        // The normal approximation is sufficiently accurate if the
        // entire range of support [0, N] lies within four standard
        // deviations of the mean...
        double mean  = mean(trialCount, successProb);
        double stdev = Math.sqrt(variance(trialCount, successProb));

        double lower = mean - 4.0 * stdev;
        double upper = mean + 4.0 * stdev;

        IntRange range = support(trialCount);
        return range.containsDouble(lower) && range.containsDouble(upper);
    }

    /**
     * Computes the mean of a binomial distribution.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @return the mean of the binomial distribution with the given
     * trial count and success probability.
     *
     * @throws IllegalArgumentException if the trial count is
     * negative.
     */
    public static double mean(int trialCount, Probability successProb) {
        validateTrialCount(trialCount);
        return trialCount * successProb.doubleValue();
    }

    /**
     * Computes the variance of a binomial distribution.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @return the variance of the binomial distribution with the given
     * trial count and success probability.
     *
     * @throws IllegalArgumentException if the trial count is
     * negative.
     */
    public static double variance(int trialCount, Probability successProb) {
        validateTrialCount(trialCount);
        return trialCount * successProb.doubleValue() * (1.0 - successProb.doubleValue());
    }

    /**
     * Computes the probability mass function for a binomial
     * distribution.
     *
     * @param k the point at which to evaluate the probability.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @return the probability mass function for the binomial
     * distribution with the given trial count and success
     * probability.
     *
     * @throws IllegalArgumentException if the trial count is
     * negative.
     */
    public static double pdf(int k, int trialCount, Probability successProb) {
        if (k < 0 || k > trialCount)
            return 0.0;
        else
            return Math.exp(logPDF(k, trialCount, successProb));
    }

    /**
     * Computes the <em>logarithm</em> of the probability mass
     * function for a binomial distribution.
     *
     * @param k the point at which to evaluate the log-probability.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @param successProb the probability of success in a single
     * trial.
     *
     * @return the <em>logarithm</em> of the probability mass function
     * for the binomial distribution with the given trial count and
     * success probability.
     */
    public static double logPDF(int k, int trialCount, Probability successProb) {
        validateTrialCount(trialCount);

        int N = trialCount;

        double logp   = Math.log(successProb.doubleValue());
        double log1_p = Math.log(1.0 - successProb.doubleValue());

        if (k < 0 || k > trialCount)
            return Double.NEGATIVE_INFINITY;
        else
            return k * logp + (N - k) * log1_p + Factorial.chooseLog(N, k);
    }

    /**
     * Returns the range of support for a binomial distribution.
     *
     * @param trialCount the number of trials for the distribution.
     *
     * @return the range of support for the binomial distribution with
     * the specified trial count.
     */
    public static IntRange support(int trialCount) {
        return new IntRange(0, trialCount);
    }

    /**
     * Returns the number of trials for this distribution.
     *
     * @return the number of trials for this distribution.
     */
    public final int getTrialCount() {
        return trialCount;
    }

    /**
     * Returns the probability of success in a single trial.
     *
     * @return the probability of success in a single trial.
     */
    public final Probability getSuccessProb() {
        return successProb;
    }

    @Override public IntRange effectiveRange() {
        double mean  = mean();
        double stdev = stdev();
        double width = 12.0;

        int lower = Math.max(0,          (int) Math.floor(mean - width * stdev));
        int upper = Math.min(trialCount, (int) Math.ceil( mean + width * stdev));

        return new IntRange(lower, upper);
    }

    @Override public double pdf(int k) {
        return pdf(k, trialCount, successProb);
    }

    @Override public double mean() {
	return mean(trialCount, successProb);
    }

    @Override public double median() {
        double p = successProb.doubleValue();
        double mean = mean();

        if (DoubleComparator.DEFAULT.isInteger(mean))
            return mean;

        if (DoubleComparator.DEFAULT.EQ(p, 0.5))
            return mean;

        if (p <= 1.0 - DoubleUtil.LOG2 || p > DoubleUtil.LOG2)
            return Math.round(mean);

        return 0.5 * (Math.floor(mean) + Math.ceil(mean));
    }

    @Override public IntRange medianRange() {
        return new IntRange((int) Math.floor(mean()), (int) Math.ceil(mean()));
    }

    @Override public double variance() {
	return variance(trialCount, successProb);
    }

    @Override public IntRange support() {
        return support(trialCount);
    }
}

final class BinomialDistributionApprox extends BinomialDistribution {
    private final NormalDistribution normal;

    BinomialDistributionApprox(int trialCount, Probability successProb) {
        super(trialCount, successProb);

        double mean  = mean(trialCount, successProb);
        double stdev = Math.sqrt(variance(trialCount, successProb));

        this.normal = new NormalDistribution(mean, stdev);
    }

    @Override public double median() {
        return mean();
    }

    @Override public int sample(JamRandom source) {
        int result = (int) Math.round(normal.sample(source));

        result = Math.max(result, 0);
        result = Math.min(result, getTrialCount());

        return result;
    }
}

final class BinomialDistributionExact extends BinomialDistribution {
    BinomialDistributionExact(int trialCount, Probability successProb) {
        super(trialCount, successProb);
    }

    @Override public int sample(JamRandom source) {
        //
        // Brute force explicit sampling...
        //
        int result = 0;

        for (int trial = 0; trial < getTrialCount(); ++trial)
            if (getSuccessProb().accept(source))
                ++result;

        return result;
    }
}
