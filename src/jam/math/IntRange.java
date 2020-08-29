
package jam.math;

import java.util.Iterator;

import jam.lang.JamException;
import jam.util.RegexUtil;

/**
 * Represents a closed contiguous range of integer values.
 */
public final class IntRange implements Iterable<Integer> {
    private final int lower;
    private final int upper;

    private IntRange(int lower, int upper) {
        validate(lower, upper);

        this.lower = lower;
        this.upper = upper;
    }

    /**
     * A globally sharable range containing all integers.
     */
    public static IntRange ALL = instance(Integer.MIN_VALUE, Integer.MAX_VALUE);
    
    /**
     * A globally sharable range containing all non-negative integers.
     */
    public static IntRange NON_NEGATIVE = instance(0, Integer.MAX_VALUE);
    
    /**
     * A globally sharable range containing all positive integers.
     */
    public static IntRange POSITIVE = instance(1, Integer.MAX_VALUE);
    
    /**
     * A globally sharable range containing all negative integers.
     */
    public static IntRange NEGATIVE = instance(Integer.MIN_VALUE, -1);
    
    /**
     * A globally sharable range containing all non-positive integers.
     */
    public static IntRange NON_POSITIVE = instance(Integer.MIN_VALUE, 0);
    
    /**
     * Returns the closed integer range {@code [lower, upper]}.
     *
     * @param lower the first integer to be contained in the range.
     *
     * @param upper the last integer to be contained in the range.
     *
     * @return the closed integer range {@code [lower, upper]}.
     *
     * @throws IllegalArgumentException unless {@code upper >= lower}.
     */
    public static IntRange instance(int lower, int upper) {
        return new IntRange(lower, upper);
    }

    /**
     * Creates an integer range from a formatted string.
     *
     * @param s a string formatted as {@code [lower, upper]}.
     *
     * @return an integer range with the bounds encoded in the input
     * string.
     *
     * @throws RuntimeException unless the input string is properly
     * formatted.
     */
    public static IntRange parse(String s) {
        if (!s.startsWith("["))
            invalidFormat(s);

        if (!s.endsWith("]"))
            invalidFormat(s);

        String[] fields = RegexUtil.COMMA.split(s.substring(1, s.length() - 1));

        if (fields.length != 2)
            invalidFormat(s);

        int lower = Integer.parseInt(fields[0].trim());
        int upper = Integer.parseInt(fields[1].trim());

        return instance(lower, upper);
    }

    private static void invalidFormat(String s) {
        throw JamException.runtime("Invalid IntRange format: [%s].", s);
    }

    /**
     * Identifies integer values in this range.
     *
     * @param value a value to examine.
     *
     * @return {@code true} iff this range contains the input value.
     */
    public boolean contains(int value) {
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
    public int lower() {
	return lower;
    }

    /**
     * Returns the last integer contained in this range.
     *
     * @return the last integer contained in this range.
     */
    public int upper() {
	return upper;
    }

    /**
     * Returns the number of integers contained in this range.
     *
     * @return the number of integers contained in this range.
     */
    public int size() {
	return 1 + upper - lower;
    }

    /**
     * Ensures that an integer value lies within this range.
     *
     * @param value the value to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(int value) {
        validate("Value", value);
    }

    /**
     * Ensures that an integer value lies within this range.
     *
     * @param name the name to use in the exception message.
     *
     * @param value the value to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(String name, int value) {
        if (!contains(value))
            throw new IllegalArgumentException(name + " [" + value + "] is outside the allowed range " + this + ".");
    }

    /**
     * Validates an integer range.
     *
     * @param lower the first integer to be contained in the range.
     *
     * @param upper the last integer to be contained in the range.
     *
     * @throws IllegalArgumentException unless {@code upper >= lower}.
     */
    public static void validate(int lower, int upper) {
        if (upper < lower)
            throw new IllegalArgumentException("Inconsistent interval bounds.");
    }

    @Override public Iterator<Integer> iterator() {
        return new IntRangeIterator(this);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof IntRange) && equalsRange((IntRange) that);
    }

    private boolean equalsRange(IntRange that) {
        return this.lower == that.lower
            && this.upper == that.upper;
    }

    @Override public int hashCode() {
        return lower + 37 * upper;
    }
    
    @Override public String toString() {
        return "IntRange(" + format() + ")";
    }
}
