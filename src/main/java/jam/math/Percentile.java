
package jam.math;

import jam.lang.DomainDouble;
import jam.vector.VectorView;

/**
 * Represents a percentile rank and encapsulates the validation of
 * percentile values.
 */
public final class Percentile extends DomainDouble implements Comparable<Percentile> {
    /**
     * Valid range for percentiles.
     */
    public static final DoubleRange RANGE = DoubleRange.PERCENTILE;

    /**
     * A globally sharable instance representing the zeroth
     * percentile.
     */
    public static final Percentile ZEROTH = valueOf(0.0);

    /**
     * A globally sharable instance representing the one-hundredth
     * percentile.
     */
    public static final Percentile HUNDREDTH = valueOf(100.0);

    /**
     * Creates a new percentile rank.
     *
     * @param value the percentile rank.
     *
     * @throws RuntimeException unless the percentile is valid.
     */
    public Percentile(double value) {
        super(value, RANGE);
    }

    /**
     * Parses the string representation of a percentile rank.
     *
     * @param s the string representation of a percentile. 
     *
     * @return a new percentile with the value specfied by the input
     * string.
     *
     * @throws IllegalArgumentException unless the string is a valid
     * percentile.
     */
    public static Percentile parse(String s) {
        return valueOf(Double.parseDouble(s));
    }

    /**
     * Validates a percentile rank.
     *
     * @param value the percentile to validate.
     *
     * @throws IllegalArgumentException unless the percentile lies in
     * the valid range {@code [0.0, 100.0]}.
     */
    public static void validate(double value) {
        if (!RANGE.contains(value))
            throw new IllegalArgumentException("Invalid percentile.");
    }

    /**
     * Marks a {@code double} value as a {@code Percentile}.
     *
     * @param value the percentile value.
     *
     * @return a {@code Percentile} object having the specified
     * percentile value.
     *
     * @throws IllegalArgumentException if the percentile is
     * negative.
     */
    public static Percentile valueOf(double value) {
        return new Percentile(value);
    }

    /**
     * Converts an array of {@code double} values into percentiles.
     *
     * @param values the values to convert.
     *
     * @return an array of corresponding {@code Percentile} objects.
     */
    public static Percentile[] valueOf(double... values) {
        return valueOf(VectorView.wrap(values));
    }

    /**
     * Converts a vector view into an array of percentiles.
     *
     * @param values the values to convert.
     *
     * @return an array of corresponding {@code Percentile} objects.
     */
    public static Percentile[] valueOf(VectorView values) {
        Percentile[] result = new Percentile[values.length()];

        for (int index = 0; index < values.length(); index++)
            result[index] = valueOf(values.getDouble(index));

        return result;
    }

    @Override public int compareTo(Percentile that) {
        return compare(this, that);
    }
}
