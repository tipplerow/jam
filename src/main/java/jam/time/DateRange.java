
package jam.time;

import java.time.LocalDate;

/**
 * Represents a calendar date interval.
 */
public final class DateRange {
    private final LocalDate lowerDate;
    private final LocalDate upperDate;
    private final DateRangeType rangeType;
    private final DateRangePredicate lowerPredicate;
    private final DateRangePredicate upperPredicate;

    private DateRange(LocalDate lowerDate, LocalDate upperDate, DateRangeType rangeType) {
        validate(lowerDate, upperDate);

        this.lowerDate = lowerDate;
        this.upperDate = upperDate;
        this.rangeType = rangeType;

        this.lowerPredicate = rangeType.lowerPredicate(lowerDate);
        this.upperPredicate = rangeType.upperPredicate(upperDate);
    }

    /**
     * The range containing no dates.
     */
    public static DateRange EMPTY = open(LocalDate.EPOCH, LocalDate.EPOCH);

    /**
     * The range containing all dates.
     */
    public static DateRange INFINITE = closed(LocalDate.MIN, LocalDate.MAX);

    /**
     * Creates a new open range.
     *
     * @param lowerDate the (exclusive) lower date of the range.
     *
     * @param upperDate the (exclusive) upper date of the range.
     *
     * @return the new open range {@code (lowerDate, upperDate)}.
     */
    public static DateRange open(LocalDate lowerDate, LocalDate upperDate) {
        return new DateRange(lowerDate, upperDate, DateRangeType.OPEN);
    }
    
    /**
     * Creates a new left-open range.
     *
     * @param lowerDate the (exclusive) lower date of the range.
     *
     * @param upperDate the (inclusive) upper date of the range.
     *
     * @return the new left-open range {@code (lowerDate, upperDate]}.
     */
    public static DateRange leftOpen(LocalDate lowerDate, LocalDate upperDate) {
        return new DateRange(lowerDate, upperDate, DateRangeType.LEFT_OPEN);
    }
    
    /**
     * Creates a new left-closed range.
     *
     * @param lowerDate the (inclusive) lower date of the range.
     *
     * @param upperDate the (exclusive) upper date of the range.
     *
     * @return the new closed range {@code [lowerDate, upperDate)}.
     */
    public static DateRange leftClosed(LocalDate lowerDate, LocalDate upperDate) {
        return new DateRange(lowerDate, upperDate, DateRangeType.LEFT_CLOSED);
    }
    
    /**
     * Creates a new left-closed range.
     *
     * @param lowerDate the (inclusive) lower date of the range.
     *
     * @param upperDate the (inclusive) upper date of the range.
     *
     * @return the new closed range {@code [lowerDate, upperDate]}.
     */
    public static DateRange closed(LocalDate lowerDate, LocalDate upperDate) {
        return new DateRange(lowerDate, upperDate, DateRangeType.CLOSED);
    }

    /**
     * Creates a range containing all dates that fall after a
     * specified date.
     *
     * @param lowerDate the last date to exclude from the range.
     *
     * @return a range containing all dates that fall after the
     * specified date.
     */
    public static DateRange after(LocalDate lowerDate) {
        return leftOpen(lowerDate, LocalDate.MAX);
    }

    /**
     * Creates a range containing all dates that fall before a
     * specified date.
     *
     * @param upperDate the first date to exclude from the range.
     *
     * @return a range containing all dates that fall before the
     * specified date.
     */
    public static DateRange before(LocalDate upperDate) {
        return leftClosed(LocalDate.MIN, upperDate);
    }

    /**
     * Creates a range containing all dates that fall on or after a
     * specified date.
     *
     * @param lowerDate the first date to include in the range.
     *
     * @return a range containing all dates that fall on or after the
     * specified date.
     */
    public static DateRange onOrAfter(LocalDate lowerDate) {
        return closed(lowerDate, LocalDate.MAX);
    }

    /**
     * Creates a range containing all dates that fall on or before a
     * specified date.
     *
     * @param upperDate the last date to includein the range.
     *
     * @return a range containing all dates that fall on or before the
     * specified date.
     */
    public static DateRange onOrBefore(LocalDate upperDate) {
        return closed(LocalDate.MIN, upperDate);
    }

    /**
     * Creates a one-week range ending on a specified date.
     *
     * @param upperDate the (inclusive) last date to include in the
     * range.
     *
     * @return a one-week range ending on the specified date.
     */
    public static DateRange trailingWeek(LocalDate upperDate) {
        return leftOpen(upperDate.minusDays(7), upperDate);
    }

    /**
     * Creates a one-month range ending on a specified date.
     *
     * @param upperDate the (inclusive) last date to include in the
     * range.
     *
     * @return a one-month range ending on the specified date.
     */
    public static DateRange trailingMonth(LocalDate upperDate) {
        return leftOpen(upperDate.minusMonths(1), upperDate);
    }

    /**
     * Creates a one-year range ending on a specified date.
     *
     * @param upperDate the (inclusive) last date to include in the
     * range.
     *
     * @return a one-year range ending on the specified date.
     */
    public static DateRange trailingYear(LocalDate upperDate) {
        return leftOpen(upperDate.minusYears(1), upperDate);
    }

    /**
     * Identifies dates in this range.
     *
     * @param date a date to examine.
     *
     * @return {@code true} iff this range contains the input date.
     */
    public boolean contains(LocalDate date) {
        return lowerPredicate.test(date) && upperPredicate.test(date);
    }

    /**
     * Returns a description of this range with parentheses or
     * brackets used as necessary to denote the closed or open
     * boundaries.
     *
     * @return a description of this range.
     */
    public String format() {
        return rangeType.format(lowerDate, upperDate);
    }

    /**
     * Returns the lower date of this range.
     *
     * @return the lower date of this range.
     */
    public LocalDate getLowerDate() {
	return lowerDate;
    }

    /**
     * Returns the lower predicate for this range.
     *
     * <p>The lower predicate evaluates to {@code true} for dates that
     * are either in this range or above it.
     *
     * @return the lower predicate for this range.
     */
    public DateRangePredicate getLowerPredicate() {
        return lowerPredicate;
    }

    /**
     * Returns the upper date of this range.
     *
     * @return the upper date of this range.
     */
    public LocalDate getUpperDate() {
	return upperDate;
    }

    /**
     * Returns the upper predicate for this range.
     *
     * <p>The upper predicate evaluates to {@code true} for dates that
     * are either in this range or below it.
     *
     * @return the upper predicate for this range.
     */
    public DateRangePredicate getUpperPredicate() {
        return upperPredicate;
    }

    /**
     * Returns the type of this range.
     *
     * @return the type of this range.
     */
    public DateRangeType getRangeType() {
	return rangeType;
    }

    /**
     * Validates a floating-point range.
     *
     * @param lowerDate the lower date of the range.
     *
     * @param upperDate the upper date of the range.
     *
     * @throws IllegalArgumentException unless {@code upperDate} is on
     * or after {@code lowerDate}.
     */
    public static void validate(LocalDate lowerDate, LocalDate upperDate) {
        if (upperDate.isBefore(lowerDate))
            throw new IllegalArgumentException("Inconsistent interval dates.");
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DateRange) && equalsRange((DateRange) that);
    }

    private boolean equalsRange(DateRange that) {
        return this.lowerDate.equals(that.lowerDate)
            && this.upperDate.equals(that.upperDate)
            && this.rangeType.equals(that.rangeType);
    }
    
    @Override public String toString() {
        return "DateRange(" + format() + ")";
    }
}
