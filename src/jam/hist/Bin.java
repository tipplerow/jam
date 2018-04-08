
package jam.hist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.math.DoubleRange;
import jam.math.LongRange;
import jam.vector.VectorUtil;

/**
 * Represents one bin in a histogram.
 */
public final class Bin {
    private final DoubleRange range;

    private long count = 0;
    private boolean frozen = false;

    /**
     * Creates an empty bin with a specified range.
     *
     * @param range the range covered by the bin.
     */
    public Bin(DoubleRange range) {
        this(range, 0, false);
    }

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
     * Creates a contiguous sequence of bins with fixed breakpoints.
     *
     * @param brkpts the breakpoints for the bins, in ascending order.
     *
     * @return a list containing {@code brkpts.length - 1} bins, 
     * with bin {@code k} having bounds {@code brkpts[k]} and 
     * {@code brkpts[k + 1]}.
     *
     * @throws IllegalArgumentException unless two or more breakpoints
     * are given in ascending order.
     */
    public static List<Bin> create(double... brkpts) {
        int nbin = brkpts.length - 1;

        if (nbin < 1)
            throw new IllegalArgumentException("At least one bin is required.");

        List<Bin> bins = new ArrayList<Bin>(nbin);

        // All bins but the last are half-open (left-closed)...
        for (int k = 0; k < nbin - 1; ++k)
            bins.add(new Bin(DoubleRange.leftClosed(brkpts[k], brkpts[k + 1])));

        // The final bin is fully closed...
        bins.add(new Bin(DoubleRange.closed(brkpts[nbin - 1], brkpts[nbin])));
        return bins;
    }

    /**
     * Creates a sequence of empty, equally-sized bins that span a
     * specified floating-point range.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of equally-sized bins to create.
     *
     * @return a list containing the empty, equally-sized bins
     * arranged in ascending order.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static List<Bin> span(double lower, double upper, int nbin) {
        if (upper <= lower)
            throw new IllegalArgumentException("Invalid range.");

        return create(VectorUtil.sequence(lower, upper, nbin + 1));
    }

    /**
     * Creates a sequence of empty bins that span a floating-point
     * range uniformly <em>in logarithmic space</em>.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of bins to create.
     *
     * @return a list containing the empty bins arranged in ascending
     * order.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size, is strictly positive, and the number of bins is
     * positive.
     */
    public static List<Bin> spanLog(double lower, double upper, int nbin) {
        if (upper <= lower)
            throw new IllegalArgumentException("Invalid range.");

        return create(VectorUtil.sequenceLog(lower, upper, nbin + 1));
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
