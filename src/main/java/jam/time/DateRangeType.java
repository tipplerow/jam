
package jam.time;

import java.time.LocalDate;

/**
 * Distinguishes open and closed ranges.
 */
public enum DateRangeType {
    /**
     * Both left and right limits are open: {@code (a, b)}.
     */
    OPEN {
        @Override public DateRangePredicate lowerPredicate(LocalDate lowerBound) {
            return DateRangePredicate.GT(lowerBound);
        }

        @Override public DateRangePredicate upperPredicate(LocalDate upperBound) {
            return DateRangePredicate.LT(upperBound);
        }

        @Override public String format(LocalDate lowerBound, LocalDate upperBound) {
            return RangeFormatter.format(LEFT_OPEN_DELIM, lowerBound, upperBound, RIGHT_OPEN_DELIM);
        }
    },

    /**
     * Left limit is open, right is closed: {@code (a, b]}.
     */
    LEFT_OPEN {
        @Override public DateRangePredicate lowerPredicate(LocalDate lowerBound) {
            return DateRangePredicate.GT(lowerBound);
        }

        @Override public DateRangePredicate upperPredicate(LocalDate upperBound) {
            return DateRangePredicate.LE(upperBound);
        }

        @Override public String format(LocalDate lowerBound, LocalDate upperBound) {
            return RangeFormatter.format(LEFT_OPEN_DELIM, lowerBound, upperBound, RIGHT_CLOSED_DELIM);
        }
    },

    /**
     * Left limit is closed, right is open: {@code [a, b)}.
     */
    LEFT_CLOSED {
        @Override public DateRangePredicate lowerPredicate(LocalDate lowerBound) {
            return DateRangePredicate.GE(lowerBound);
        }

        @Override public DateRangePredicate upperPredicate(LocalDate upperBound) {
            return DateRangePredicate.LT(upperBound);
        }

        @Override public String format(LocalDate lowerBound, LocalDate upperBound) {
            return RangeFormatter.format(LEFT_CLOSED_DELIM, lowerBound, upperBound, RIGHT_OPEN_DELIM);
        }
    },

    /**
     * Both left and right limits are closed: {@code [a, b]}.
     */
    CLOSED {
        @Override public DateRangePredicate lowerPredicate(LocalDate lowerBound) {
            return DateRangePredicate.GE(lowerBound);
        }

        @Override public DateRangePredicate upperPredicate(LocalDate upperBound) {
            return DateRangePredicate.LE(upperBound);
        }

        @Override public String format(LocalDate lowerBound, LocalDate upperBound) {
            return RangeFormatter.format(LEFT_CLOSED_DELIM, lowerBound, upperBound, RIGHT_CLOSED_DELIM);
        }
    };

    /**
     * Delimiter for ranges open on the left boundary.
     */
    public static final char LEFT_OPEN_DELIM   = '(';

    /**
     * Delimiter for ranges closed on the left boundary.
     */
    public static final char LEFT_CLOSED_DELIM = '[';

    /**
     * Delimiter for ranges open on the right boundary.
     */
    public static final char RIGHT_OPEN_DELIM   = ')';

    /**
     * Delimiter for ranges closed on the right boundary.
     */
    public static final char RIGHT_CLOSED_DELIM = ']';

    /**
     * String that separates the lower and upper bound.
     */
    public static final String SEPARATOR = ", ";

    /**
     * Returns a predicate to determine whether a real value satisfies
     * the lower bound for a range of this type.
     *
     * @param lowerBound location of the lower (left) boundary.
     *
     * @return a predicate to determine whether a real value satisfies
     * the given lower bound.
     */
    public abstract DateRangePredicate lowerPredicate(LocalDate lowerBound);

    /**
     * Returns a predicate to determine whether a real value satisfies
     * the upper bound for a range of this type.
     *
     * @param upperBound location of the upper (right) boundary.
     *
     * @return a predicate to determine whether a real value satisfies
     * the given upper bound.
     */
    public abstract DateRangePredicate upperPredicate(LocalDate upperBound);

    /**
     * Formats boundary limits according to this range type.
     *
     * @param lowerBound location of the lower (left) boundary.
     *
     * @param upperBound location of the upper (right) boundary.
     *
     * @return the bounds formatted appropriately.
     */
    public abstract String format(LocalDate lowerBound, LocalDate upperBound);

    /**
     * Returns the range type for a string representation of a {@code
     * DoubleRange}.
     *
     * <p>The first character of the string must be either {@code [}
     * (closed) or {@code (} (open); the last character must be either
     * {@code ]} (closed) or {@code )} (open).
     *
     * @param s the string to parse.
     *
     * @return the range type for the given string representation.
     *
     * @throws IllegalArgumentException unless the input string
     * contains valid left and right range indicators.
     */
    public static DateRangeType parse(String s) {
        s = s.trim();

        char left  = s.charAt(0);
        char right = s.charAt(s.length() - 1);

        if (left == LEFT_OPEN_DELIM && right == RIGHT_OPEN_DELIM)
            return OPEN;

        if (left == LEFT_OPEN_DELIM && right == RIGHT_CLOSED_DELIM)
            return LEFT_OPEN;

        if (left == LEFT_CLOSED_DELIM && right == RIGHT_OPEN_DELIM)
            return LEFT_CLOSED;

        if (left == LEFT_CLOSED_DELIM && right == RIGHT_CLOSED_DELIM)
            return CLOSED;

        throw new IllegalArgumentException("Invalid LocalDate range specification.");
    }
}

final class RangeFormatter {
    //
    // The static "format" method cannot be defined within the
    // DateRangeType class and then called from the instance
    // format() methods, so this helper class is a work-around...
    //
    static String format(char lowerDelim, LocalDate lowerBound, LocalDate upperBound, char upperDelim) {
        StringBuilder builder = new StringBuilder();

        builder.append(lowerDelim);
        builder.append(lowerBound);
        builder.append(DateRangeType.SEPARATOR);
        builder.append(upperBound);
        builder.append(upperDelim);

        return builder.toString();
    }
}
