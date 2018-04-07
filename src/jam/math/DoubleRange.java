
package jam.math;

/**
 * Represents an interval on the real number line with some finite
 * tolerance for imprecision in floating-point comparisons.
 */
public final class DoubleRange {
    private final double lowerBound;
    private final double upperBound;
    private final RangeType rangeType;
    private final DoublePredicate lowerPredicate;
    private final DoublePredicate upperPredicate;

    private DoubleRange(double lowerBound, double upperBound, RangeType rangeType) {
        validate(lowerBound, upperBound);

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.rangeType  = rangeType;

        this.lowerPredicate = rangeType.lowerPredicate(lowerBound);
        this.upperPredicate = rangeType.upperPredicate(upperBound);
    }

    /**
     * The empty range: {@code (x, x)}.
     */
    public static DoubleRange EMPTY = open(0.0, 0.0);

    /**
     * The range of <em>fractional</em> real numbers: {@code [0.0, 1.0]}.
     */
    public static DoubleRange FRACTIONAL = closed(0.0, 1.0);

    /**
     * The range containing all real numbers: {@code [-Inf, +Inf]}.
     */
    public static DoubleRange INFINITE = closed(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    
    /**
     * The range containing all negative real numbers: {@code [-Inf, 0.0)}.
     */
    public static DoubleRange NEGATIVE = leftClosed(Double.NEGATIVE_INFINITY, 0.0);
    
    /**
     * The range containing all non-negative real numbers: {@code [0.0, +Inf]}.
     */
    public static DoubleRange NON_NEGATIVE = closed(0.0, Double.POSITIVE_INFINITY);
    
    /**
     * The range containing all non-positive real numbers: {@code [-Inf, 0.0]}.
     */
    public static DoubleRange NON_POSITIVE = closed(Double.NEGATIVE_INFINITY, 0.0);
    
    /**
     * The range containing all positive real numbers: {@code (0.0, +Inf]}.
     */
    public static DoubleRange POSITIVE = leftOpen(0.0, Double.POSITIVE_INFINITY);
    
    /**
     * Creates a new open range.
     *
     * @param lowerBound the (exclusive) lower bound of the range.
     *
     * @param upperBound the (exclusive) upper bound of the range.
     *
     * @return the new open range {@code (lowerBound, upperBound)}.
     */
    public static DoubleRange open(double lowerBound, double upperBound) {
        return new DoubleRange(lowerBound, upperBound, RangeType.OPEN);
    }
    
    /**
     * Creates a new left-open range.
     *
     * @param lowerBound the (exclusive) lower bound of the range.
     *
     * @param upperBound the (inclusive) upper bound of the range.
     *
     * @return the new left-open range {@code (lowerBound, upperBound]}.
     */
    public static DoubleRange leftOpen(double lowerBound, double upperBound) {
        return new DoubleRange(lowerBound, upperBound, RangeType.LEFT_OPEN);
    }
    
    /**
     * Creates a new left-closed range.
     *
     * @param lowerBound the (inclusive) lower bound of the range.
     *
     * @param upperBound the (exclusive) upper bound of the range.
     *
     * @return the new closed range {@code [lowerBound, upperBound)}.
     */
    public static DoubleRange leftClosed(double lowerBound, double upperBound) {
        return new DoubleRange(lowerBound, upperBound, RangeType.LEFT_CLOSED);
    }
    
    /**
     * Creates a new left-closed range.
     *
     * @param lowerBound the (inclusive) lower bound of the range.
     *
     * @param upperBound the (inclusive) upper bound of the range.
     *
     * @return the new closed range {@code [lowerBound, upperBound]}.
     */
    public static DoubleRange closed(double lowerBound, double upperBound) {
        return new DoubleRange(lowerBound, upperBound, RangeType.CLOSED);
    }

    /**
     * Identifies floating-point values in this range.
     *
     * @param value a value to examine.
     *
     * @return {@code true} iff this range contains the input value.
     */
    public boolean contains(double value) {
        return lowerPredicate.test(value) && upperPredicate.test(value);
    }

    /**
     * Returns a description of this range with parentheses or
     * brackets used as necessary to denote the closed or open
     * boundaries.
     *
     * @return a description of this range.
     */
    public String format() {
        return rangeType.format(lowerBound, upperBound);
    }

    /**
     * Returns the lower bound of this range.
     *
     * @return the lower bound of this range.
     */
    public double getLowerBound() {
	return lowerBound;
    }

    /**
     * Returns the upper bound of this range.
     *
     * @return the upper bound of this range.
     */
    public double getUpperBound() {
	return upperBound;
    }

    /**
     * Returns the type of this range.
     *
     * @return the type of this range.
     */
    public RangeType getRangeType() {
	return rangeType;
    }

    /**
     * Ensures that a floating-point value lies within this range.
     *
     * @param value the value to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(double value) {
        validate("Value", value);
    }

    /**
     * Ensures that a floating-point value lies within this range.
     *
     * @param name the name to use in the exception message.
     *
     * @param value the value to test.
     *
     * @throws IllegalArgumentException unless the input value lies
     * within this range.
     */
    public void validate(String name, double value) {
        if (!contains(value))
            throw new IllegalArgumentException(name + " [" + value + "] is outside the allowed range " + this + ".");
    }

    /**
     * Validates a floating-point range.
     *
     * @param lowerBound the lower bound of the range.
     *
     * @param upperBound the upper bound of the range.
     *
     * @throws IllegalArgumentException unless {@code upperBound >= lowerBound}.
     */
    public static void validate(double lowerBound, double upperBound) {
        if (upperBound < lowerBound)
            throw new IllegalArgumentException("Inconsistent interval bounds.");
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DoubleRange) && equalsRange((DoubleRange) that);
    }

    private boolean equalsRange(DoubleRange that) {
        return this.rangeType.equals(that.rangeType)
            && DoubleComparator.DEFAULT.EQ(this.lowerBound, that.lowerBound)
            && DoubleComparator.DEFAULT.EQ(this.upperBound, that.upperBound);
    }
    
    @Override public String toString() {
        return "DoubleRange(" + format() + ")";
    }
}
