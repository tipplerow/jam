
package jam.dist;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.IntRange;
import jam.math.IntUtil;
import jam.math.JamRandom;
import jam.math.StatSummary;

/**
 * Provides a skeleton implementation of the {@code DiscreteDistribution}
 * interface.
 */
public abstract class AbstractDiscreteDistribution implements DiscreteDistribution {
    // Summary derived from random sampling, created on demand...
    private StatSummary summary = null;

    // Number of samples used to compute the statistical summary...
    private static final int SAMPLE_COUNT = 1000000;

    @Override public double cdf(int k) {
        int lower = effectiveRange().lower();
        int upper = k;

        return cumulatePDF(lower, upper);
    }

    @Override public double cdf(int j, int k) {
        //
        // "j + 1", because the argument "j" is not included in the
        // CDF range...
        //
        int lower = j + 1;
        int upper = k;

        return cumulatePDF(lower, upper);
    }

    private double cumulatePDF(int lower, int upper) {
        double result = 0.0;

        for (int k = lower; k <= upper; ++k)
            result += pdf(k);

        return result;
    }

    @Override public double mean() {
	return sampleSummary().getMean();
    }

    @Override public double median() {
	return sampleSummary().getMedian();
    }

    @Override public double stdev() {
	return Math.sqrt(variance());
    }

    @Override public double variance() {
	return sampleSummary().getVariance();
    }

    private StatSummary sampleSummary() {
        if (summary == null)
            summary = StatSummary.compute(IntUtil.toDouble(sample(SAMPLE_COUNT)));

        return summary;
    }

    @Override public int sample() {
        return sample(JamRandom.global());
    }

    @Override public Multiset<Integer> sample(int count) {
        return sample(JamRandom.global(), count);
    }

    @Override public Multiset<Integer> sample(JamRandom source, int count) {
        Multiset<Integer> values = HashMultiset.create();

        for (int index = 0; index < count; index++)
            values.add(sample(source));

        return values;
    }
}
