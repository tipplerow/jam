
package jam.math;

import java.util.Iterator;

import jam.lang.JamException;
import jam.util.RegexUtil;

/**
 * Represents a closed contiguous range of {@code long} values.
 */
public final class LongRange implements Iterable<Long> {
    private final long lower;
    private final long upper;

    /**
     * A globally sharable range containing all longs.
     */
    public static LongRange ALL = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE);
    
    /**
     * A globally sharable range containing all non-negative longs.
     */
    public static LongRange NON_NEGATIVE = new LongRange(0, Long.MAX_VALUE);
    
    /**
     * A globally sharable range containing all positive longs.
     */
    public static LongRange POSITIVE = new LongRange(1, Long.MAX_VALUE);
    
    /**
     * A globally sharable range containing all negative longs.
     */
    public static LongRange NEGATIVE = new LongRange(Long.MIN_VALUE, -1);
    
    /**
     * A globally sharable range containing all non-positive longs.
     */
    public static LongRange NON_POSITIVE = new LongRange(Long.MIN_VALUE, 0);
    
    /**
     * Creates the new closed long integer range {@code [lower, upper]}.
     *
     * @param lower the first long value to be contained in the range.
     *
     * @param upper the last long value to be contained in the range.
     *
     * @throws IllegalArgumentException unless {@code upper >= lower}.
     */
    public LongRange(long lower, long upper) {
        validate(lower, upper);

        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Creates a long integer range from a formatted string.
     *
     * @param s a string formatted as {@code [lower, upper]}.
     *
     * @return a long integer range with the bounds encoded in the input
     * string.
     *
     * @throws RuntimeException unless the input string is properly
     * formatted.
     */
    public static LongRange parse(String s) {
        if (!s.startsWith("["))
            invalidFormat(s);

        if (!s.endsWith("]"))
            invalidFormat(s);

        String[] fields = RegexUtil.COMMA.split(s.substring(1, s.length() - 1));

        if (fields.length != 2)
            invalidFormat(s);

        long lower = Long.parseLong(fields[0].trim());
        long upper = Long.parseLong(fields[1].trim());

        return new LongRange(lower, upper);
    }

    private static void invalidFormat(String s) {
        throw JamException.runtime("Invalid LongRange format: [%s].", s);
    }

    /**
     * Identifies long values in this range.
     *
     * @param value a value to examine.
     *
     * @return {@code true} iff this range contains the input value.
     */
    public boolean contains(long value) {
        return lower <= value && value <= upper;
    }

    /**
     * Identifies floating-point values in this range.
     *
     * @param value a value to examine.
     *
     * @return {@code true} iff this range contains the input value
     * (allowing for the default floating-point tolerance).
     */
    public boolean containsDouble(double value) {
        return DoubleComparator.DEFAULT.GE(value, lower)
            && DoubleComparator.DEFAULT.LE(value, upper);
    }

    /**
     * Returns a description of this range formatted as {@code [lower, upper]}.
     *
     * @return a description of this range.
     */
    public String format() {
        return String.format("[%d, %d]", lower, upper);
    }

    /**
     * Returns the first integer contained in this range.
     *
     * @return the first integer contained in this range.
     */
    public long lower() {
	return lower;
    }

    /**
     * Returns the last integer contained in this range.
     *
     * @return the last integer contained in this range.
     */
    public long upper() {
	return upper;
    }

    /**
     * Returns the number of longs contained in this range.
     *
     * @return the number of longs contained in this range.
     */
    public long size() {
	return 1 + upper - lower;
    }

    /**
     * Ensures that a long integer value lies within this range.
     *
     * @param value the value to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(long value) {
        if (!contains(value))
            throw new IllegalArgumentException("Value [" + value + "] is outside the allowed range " + this + ".");
    }

    /**
     * Validates a long integer range.
     *
     * @param lower the first integer to be contained in the range.
     *
     * @param upper the last integer to be contained in the range.
     *
     * @throws IllegalArgumentException unless {@code upper >= lower}.
     */
    public static void validate(long lower, long upper) {
        if (upper < lower)
            throw new IllegalArgumentException("Inconsistent interval bounds.");
    }

    @Override public Iterator<Long> iterator() {
        return new LongRangeIterator(this);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof LongRange) && equalsRange((LongRange) that);
    }

    private boolean equalsRange(LongRange that) {
        return this.lower == that.lower && this.upper == that.upper;
    }

    @Override public int hashCode() {
        return (int) (lower + 37 * upper);
    }
    
    @Override public String toString() {
        return "LongRange(" + format() + ")";
    }
}
