
package jam.stoch;

import java.util.Collection;
import java.util.Comparator;

import jam.dist.ExponentialDistribution;
import jam.lang.DomainDouble;
import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.math.JamRandom;

/**
 * Represents the rate at which a stochastic process occurs.
 *
 * <p><b>Units.</b> The units of the stochastic rates are not
 * explicitly represented in the rate objects.  Applications
 * should take care to use a consistent unit convention.
 */
public final class StochRate extends DomainDouble implements Comparable<StochRate> {
    private StochRate(double value) {
        super(value, RANGE);
    }

    /**
     * Valid range for rates.
     */
    public static final DoubleRange RANGE = DoubleRange.NON_NEGATIVE;

    /**
     * A globally sharable instance representing zero rate.
     */
    public static final StochRate ZERO = new StochRate(0.0);

    /**
     * A comparator that orders rates in ascending order (slowest rate
     * first).
     */
    public static final Comparator<StochRate> ASCENDING_COMPARATOR =
        new Comparator<StochRate>() {
            @Override public int compare(StochRate rate1, StochRate rate2) {
                return rate1.compareTo(rate2);
            }
        };

    /**
     * A comparator that orders rates in descending order (fastest
     * rate first).
     */
    public static final Comparator<StochRate> DESCENDING_COMPARATOR =
        new Comparator<StochRate>() {
            @Override public int compare(StochRate rate1, StochRate rate2) {
                return rate2.compareTo(rate1);
            }
        };

    /**
     * Computes the total over a collection of rates.
     *
     * @param rates the rates to aggregate.
     *
     * @return the total over the specified collection of rates.
     */
    public static StochRate total(Collection<StochRate> rates) {
        double total = 0.0;

        for (StochRate rate : rates)
            total += rate.doubleValue();

        return valueOf(total);
    }

    /**
     * Creates a new rate from a floating-point value.
     *
     * @param value the stochastic rate.
     *
     * @return a {@code StochRate} object having the specified rate.
     *
     * @throws IllegalArgumentException if the rate is
     * negative.
     */
    public static StochRate valueOf(double value) {
        if (DoubleComparator.DEFAULT.isZero(value))
            return ZERO;
        else
            return new StochRate(value);
    }

    /**
     * Generates a random sample for the (relative) time interval
     * until the next occurrence of a stochastic process with this
     * rate.
     *
     * @param random a random number source.
     *
     * @return a random sample for the (relative) time interval until
     * the next occurrence of a stochastic process with this rate.
     */
    public double sampleInterval(JamRandom random) {
        return ExponentialDistribution.sample(doubleValue(), random);
    }

    /**
     * Generates a random sample for the (absolute) time of the next
     * occurrence of a stochastic process with this rate.
     *
     * @param prevTime the time when the previous event occurred.
     *
     * @param random a random number source.
     *
     * @return a random sample for the (absolute) time of the next
     * occurrence of a stochastic process with this rate.
     */
    public StochTime sampleTime(StochTime prevTime, JamRandom random) {
        if (isZero())
            return StochTime.INFINITY;
        else
            return prevTime.plus(sampleInterval(random));
    }

    @Override public int compareTo(StochRate that) {
        return compare(this, that);
    }
}
