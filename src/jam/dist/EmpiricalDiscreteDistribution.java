
package jam.dist;

import java.util.Collection;

import com.google.common.collect.Multiset;

import jam.math.IntRange;
import jam.math.IntUtil;
import jam.math.JamRandom;

/**
 * Represents a univariate probability distribution taking integer
 * values derived from an empirical data set.
 */
public final class EmpiricalDiscreteDistribution extends CompactDiscreteDistribution {
    private final int nob;

    private EmpiricalDiscreteDistribution(int nob, DiscretePDF pdf) {
        super(pdf);
        this.nob = nob;
    }

    /**
     * Creates a new discrete distribution describing a set of
     * empirical observations.
     *
     * @param observations the observations to describe.
     *
     * @return a discrete distribution describing the given data.
     */
    public static EmpiricalDiscreteDistribution compute(Collection<Integer> observations) {
        return compute(IntUtil.count(observations));
    }

    /**
     * Creates a new discrete distribution describing a set of
     * empirical observations.
     *
     * @param observations the observations to describe.
     *
     * @return a discrete distribution describing the given data.
     */
    public static EmpiricalDiscreteDistribution compute(int... observations) {
        return compute(IntUtil.count(observations));
    }

    /**
     * Creates a new discrete distribution describing a set of
     * empirical observations.
     *
     * @param counts the observations to describe, grouped in a bag
     * (multiset).
     *
     * @return the discrete distribution describing the given data.
     */
    public static EmpiricalDiscreteDistribution compute(Multiset<Integer> counts) {
        return new EmpiricalDiscreteDistribution(counts.size(), DiscretePDF.compute(counts));
    }

    /**
     * Returns the number of observations used to estimate this
     * distribution.
     *
     * @return the number of observations used to estimate this
     * distribution.
     */
    public int countObservations() {
        return nob;
    }

    /**
     * Returns the standard error around the mean value.
     *
     * @return the standard error around the mean value.
     */
    public double sterr() {
        //
        // Assume independent observations...
        //
        return stdev() / Math.sqrt(nob);
    }
}
