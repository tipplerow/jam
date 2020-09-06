
package jam.math;

import java.util.Comparator;

import jam.lang.JamException;

/**
 * Represents a closed contiguous range of unit-offset indexes.
 */
public final class UnitIndexRange {
    private final IntRange range;
    private final UnitIndex lower;
    private final UnitIndex upper;

    private UnitIndexRange(IntRange range) {
        validate(range);

        this.range = range;
        this.lower = UnitIndex.instance(range.lower());
        this.upper = UnitIndex.instance(range.upper());
    }

    /**
     * A comparator that orders unit index ranges by their lower bound
     * first, upper bound second (when the lower bounds are equal).
     */
    public static Comparator<UnitIndexRange> BOUND_COMPARATOR = new Comparator<UnitIndexRange>() {
            @Override public int compare(UnitIndexRange range1, UnitIndexRange range2) {
                return IntRange.BOUND_COMPARATOR.compare(range1.range, range2.range);
            }
        };
    
    /**
     * Returns the closed unit-offset index range with bounds contained
     * in an existing integer range.
     *
     * @param range the unit-offset index range.
     *
     * @return the closed unit-offset index range with bounds contained
     * in the specified integer range.
     *
     * @throws IllegalArgumentException unless {@code range.lower() > 0}.
     */
    public static UnitIndexRange instance(IntRange range) {
        return new UnitIndexRange(range);
    }

    /**
     * Returns the closed unit-offset index range {@code [lower, upper]}.
     *
     * @param lower the first unit-offset index to be contained in the
     * range.
     *
     * @param upper the last unit-offset index to be contained in the
     * range.
     *
     * @return the closed unit-offset index range {@code [lower, upper]}.
     *
     * @throws IllegalArgumentException unless {@code lower > 0 && upper >= lower}.
     */
    public static UnitIndexRange instance(int lower, int upper) {
        return instance(IntRange.instance(lower, upper));
    }

    /**
     * Returns the closed unit-offset index range {@code [lower, upper]}.
     *
     * @param lower the first index to be contained in the range.
     *
     * @param upper the last index to be contained in the range.
     *
     * @return the closed unit index range {@code [lower, upper]}.
     *
     * @throws IllegalArgumentException if the lower index is above
     * the upper index.
     */
    public static UnitIndexRange instance(UnitIndex lower, UnitIndex upper) {
        return instance(lower.getUnitIndex(), upper.getUnitIndex());
    }

    /**
     * Returns the closed unit-offset index range with a fixed upper
     * bound and size.
     *
     * @param upper the upper bound of the desired range.
     *
     * @param size the size of the desired range.
     *
     * @return the closed unit-offset index range with the specified
     * upper bound and size.
     */
    public static UnitIndexRange backward(UnitIndex upper, int size) {
        return instance(upper.minus(size - 1), upper);
    }

    /**
     * Returns the closed unit-offset index range with a fixed lower
     * bound and size.
     *
     * @param lower the lower bound of the desired range.
     *
     * @param size the size of the desired range.
     *
     * @return the closed unit-offset index range with the specified
     * lower bound and size.
     */
    public static UnitIndexRange forward(UnitIndex lower, int size) {
        return instance(lower, lower.plus(size - 1));
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
    public static UnitIndexRange parse(String s) {
        return instance(IntRange.parse(s));
    }

    /**
     * Identifies unit-offset indexes in this range.
     *
     * @param index a unif-offset index to examine.
     *
     * @return {@code true} iff this range contains the specified
     * unit-offset index.
     */
    public boolean contains(int index) {
        return range.contains(index);
    }

    /**
     * Identifies unit-offset indexes in this range.
     *
     * @param index a unif-offset index to examine.
     *
     * @return {@code true} iff this range contains the specified
     * unit-offset index.
     */
    public boolean contains(UnitIndex index) {
        return contains(index.getUnitIndex());
    }

    /**
     * Returns a description of this range formatted as {@code [lower, upper]}.
     *
     * @return a description of this range.
     */
    public String format() {
        return range.format();
    }

    /**
     * Returns the first unit-offset index contained in this range.
     *
     * @return the first unit-offset index contained in this range.
     */
    public UnitIndex lower() {
	return lower;
    }

    /**
     * Returns the last unit-offset index contained in this range.
     *
     * @return the last unit-offset index contained in this range.
     */
    public UnitIndex upper() {
	return upper;
    }

    /**
     * Returns the number of elements contained in this range.
     *
     * @return the number of elements contained in this range.
     */
    public int size() {
	return range.size();
    }

    /**
     * Ensures that a unit-offset index value lies within this range.
     *
     * @param index the index to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(int index) {
        range.validate(index);
    }

    /**
     * Validates a unit-offset index range.
     *
     * @param lower the first unit-offset index to be contained in the
     * range.
     *
     * @param upper the last unit-offset index to be contained in the
     * range.
     *
     * @throws IllegalArgumentException unless {@code lower > 0 && upper >= lower}.
     */
    public static void validate(int lower, int upper) {
        if (lower < 1)
            throw new IllegalArgumentException("Lower bound must be positive.");

        if (upper < lower)
            throw new IllegalArgumentException("Inconsistent interval bounds.");
    }

    /**
     * Validates a unit-offset index range.
     *
     * @param range a bare integer range.
     *
     * @throws IllegalArgumentException unless {@code range.lower() > 0 && range.upper() >= range.lower()}.
     */
    public static void validate(IntRange range) {
        validate(range.lower(), range.upper());
    }

    @Override public boolean equals(Object that) {
        return (that instanceof UnitIndexRange) && equalsRange((UnitIndexRange) that);
    }

    private boolean equalsRange(UnitIndexRange that) {
        return this.range.equals(that.range);
    }

    @Override public int hashCode() {
        return range.hashCode();
    }
    
    @Override public String toString() {
        return "UnitIndexRange(" + format() + ")";
    }
}
