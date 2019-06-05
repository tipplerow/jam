
package jam.rna;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;

/**
 * Represents the expression of an RNA transcript.
 */
public final class Expression extends DomainDouble {
    /**
     * Valid range for expression values.
     */
    public static final DoubleRange RANGE = DoubleRange.NON_NEGATIVE;

    /**
     * A globally sharable instance representing zero expression.
     */
    public static final Expression ZERO = valueOf(0.0);

    private Expression(double value) {
        super(value, RANGE);
    }

    /**
     * Validates an expression value.
     *
     * @param value the expression to validate.
     *
     * @throws IllegalArgumentException if the expression is negative.
     */
    public static void validate(double value) {
        RANGE.validate(value);
    }

    /**
     * Marks a {@code double} value as a {@code Expression}.
     *
     * @param value the expression value.
     *
     * @return a {@code Expression} object having the specified
     * expression value.
     *
     * @throws IllegalArgumentException if the expression is
     * negative.
     */
    public static Expression valueOf(double value) {
        return new Expression(value);
    }

    /**
     * Returns an expression object corresponding to a string
     * representation.
     *
     * @param s a string representation of the expression value.
     *
     * @return an expression object with the value represented by the
     * input string.
     */
    public static Expression valueOf(String s) {
        return valueOf(Double.parseDouble(s));
    }
}
