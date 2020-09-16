
package jam.hist;

import java.util.Collection;

import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.LongRange;

/**
 * Represents one bin in a histogram.
 */
public final class Bin {
    private final DoubleRange range;

    private long count = 0;
    private boolean frozen = false;

    /**
     * Creates a bin with a specified range and count.
     *
     * @param range the range covered by the bin.
     *
     * @param count the initial count for the bin.
     *
     * @param frozen whether or not the bin is frozen or may be
     * incremented.
     *
     * @throws IllegalArgumentException if the count is negative.
     */
    public Bin(DoubleRange range, long count, boolean frozen) {
        LongRange.NON_NEGATIVE.validate(count);

        this.range  = range;
        this.count  = count;
        this.frozen = frozen;
    }

    /**
     * Creates an empty bin with a specified range.
     *
     * @param range the range covered by the bin.
     *
     * @return the new empty bin.
     */
    public static Bin empty(DoubleRange range) {
        return new Bin(range, 0, false);
    }

    /**
     * Adds counts to this bin.
     *
     * @param counts the counts to add.
     *
     * @throws IllegalArgumentException if the number of counts is
     * negative.
     *
     * @throws IllegalStateException if this bin has been frozen.
     */
    public void add(long counts) {
        LongRange.NON_NEGATIVE.validate(counts);

        if (frozen)
            throw new IllegalStateException("Cannot add to a frozen bin.");

        count += counts;
    }

    /**
     * Computes the total number of counts in a collection of bins.
     *
     * @param bins the bins to aggregate.
     *
     * @return the total number of counts in the collection of bins.
     */
    public static long computeTotalCount(Collection<Bin> bins) {
        long total = 0;

        for (Bin bin : bins)
            total += bin.count;

        return total;
    }

    /**
     * Orders an observation relative to this bin.
     *
     * @param obs an observation to order.
     *
     * @return (1) a negative integer, if the observation lies in a
     * bin to the left of this bin; (2) zero if the observation lies
     * in this bin itself; or (3) a positive integer otherwise (the
     * observation lies in a bin to the right of this bin.
     */
    public int compare(double obs) {
        if (contains(obs))
            return 0;

        if (range.getLowerPredicate().test(obs)) {
            //
            // The observation is greater than or equal to the lower
            // range, but not within it, so it must lie to the right
            // of this bin...
            //
            return 1;
        }

        return -1;
    }

    /**
     * Identifies observations that fall in this bin.
     *
     * @param obs an observation to test.
     *
     * @return {@code true} iff the specified observation falls in
     * this bin.
     */
    public boolean contains(double obs) {
        return range.contains(obs);
    }

    /**
     * Add one count to this bin.
     *
     * @throws IllegalStateException if this bin has been frozen.
     */
    public void increment() {
        add(1);
    }

    /**
     * Freezes this bin: forbids further modification of the count.
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Freezes a collection of bins: forbids further modification of
     * the counts.
     *
     * @param bins the bins to freeze.
     */
    public static void freeze(Collection<Bin> bins) {
        for (Bin bin : bins)
            bin.freeze();
    }

    /**
     * Returns the number of counts in this bin.
     *
     * @return the number of counts in this bin.
     */
    public long getCount() {
        return count;
    }

    /**
     * Returns the fraction of observations in this bin.
     *
     * @param total the total number of observations.
     *
     * @return the fraction of observations in this bin.
     */
    public double getFrequency(long total) {
        return DoubleUtil.ratio(count, total);
    }

    /**
     * Returns the mid-point of the range for this bin.
     *
     * @return the mid-point of the range for this bin.
     */
    public double getMidPoint() {
        return 0.5 * (range.getLowerBound() + range.getUpperBound());
    }

    /**
     * Returns the range for this bin.
     *
     * @return the range for this bin.
     */
    public DoubleRange getRange() {
        return range;
    }

    /**
     * Returns {@code true} iff this bin has been frozen to forbid
     * changing the count.
     *
     * @return {@code true} iff this bin has been frozen to forbid
     * changing the count.
     */
    public boolean isFrozen() {
        return frozen;
    }

    @Override public String toString() {
        return String.format("Bin(%s: %d)", range.toString(), count);
    }
}
