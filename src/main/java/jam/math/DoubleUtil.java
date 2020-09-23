
package jam.math;

import java.text.DecimalFormatSymbols;
import java.util.Collection;

/**
 * Provides static utility methods operating on floating-point numbers
 * beyond those in {@code java.lang.Math}.
 */
public final class DoubleUtil {
    /**
     * Natural logarithm of two.
     */
    public static final double LOG2 = Math.log(2.0);

    private static final double LOG2_INV = 1.0 / LOG2;

    /**
     * Square root of two.
     */
    public static final double SQRT2 = Math.sqrt(2.0);

    /**
     * Square root of twice pi.
     */
    public static final double SQRT_TWO_PI = Math.sqrt(2.0 * Math.PI);

    /**
     * Euler's constant.
     */
    public static final double EULER = 0.5772156649;

    /**
     * Square root of three.
     */
    public static final double SQRT3 = Math.sqrt(3.0);

    /**
     * One-half of the square root of three.
     */
    public static final double HALF_SQRT3 = 0.5 * Math.sqrt(3.0);

    /**
     * The character used to separate a decimal number into groups of
     * thousands.
     */
    public static final char THOUSANDS_SEPARATOR_CHAR =
        DecimalFormatSymbols.getInstance().getGroupingSeparator();

    /**
     * The string used to separate a decimal number into groups of
     * thousands.
     */
    public static final String THOUSANDS_SEPARATOR_STRING =
        String.valueOf(THOUSANDS_SEPARATOR_CHAR);

    /**
     * Determines whether a string represents a floating-point value.
     *
     * @param s the string to evaluate.
     *
     * @return {@code true} iff the input string may be converted to a
     * floating-point value by {@code parseDouble(String)}.
     */
    public static boolean isDouble(String s) {
        try {
            parseDouble(s);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * Identifies finite (not infinite or {@code NaN}) values.
     *
     * @param x a value to examine.
     *
     * @return {@code true} iff the value is neither infinite nor {@code NaN}.
     */
    public static boolean isFinite(double x) {
        return !Double.isNaN(x) && !Double.isInfinite(x);
    }

    /**
     * Identifies floating-point values that are equal to {@code int}
     * values (within a small tolerance).
     *
     * @param x the number to examine.
     *
     * @return {@code true} iff the input value is equal to an 
     * {@code int} value.
     */
    public static boolean isInt(double x) {
        return isRound(x) && DoubleRange.INTEGERS.contains(x);
    }

    /**
     * Identifies floating-point values that are equal to {@code long}
     * values (within a small tolerance).
     *
     * @param x the number to examine.
     *
     * @return {@code true} iff the input value is equal to a
     * {@code long} value.
     */
    public static boolean isLong(double x) {
        return isRound(x) && DoubleRange.LONGS.contains(x);
    }

    /**
     * Identifies floating-point values that are round (integral)
     * values.
     *
     * @param x the number to examine.
     *
     * @return {@code true} iff the input value is a round (integral)
     * number.
     */
    public static boolean isRound(double x) {
        return DoubleComparator.DEFAULT.EQ(x, round(x, 0));
    }

    /**
     * Identifies floating-point values that have been assigned
     * non-missing values.
     *
     * @param x the value to examine.
     *
     * @return {@code true} iff the input value is set.
     */
    public static boolean isSet(double x) {
        return !Double.isNaN(x);
    }

    /**
     * Identifies unset or missing floating-point values.
     *
     * @param x the value to examine.
     *
     * @return {@code true} iff the input value is unset.
     */
    public static boolean isUnset(double x) {
        return Double.isNaN(x);
    }

    /**
     * Computes the logarithm with an arbitrary base.
     *
     * @param x the value to take the logarithm of.
     *
     * @param base the base of the logarithm to calculate.
     *
     * @return the base-{@code base} logarithm of {@code x}.
     */
    public static double log(double x, double base) {
        return Math.log(x) / Math.log(base);
    }

    /**
     * Computes base-2 logarithms.
     *
     * @param x the numeric argument.
     *
     * @return the base-2 logarithm of the input value.
     */
    public static double log2(double x) {
        return LOG2_INV * Math.log(x);
    }

    /**
     * Rounds a floating-point value to a nearest unit value (not
     * necessarily a power of ten).  For example:
     * <pre>
     *   nearest(1.33, 0.25) == 1.25
     *   nearest(1.40, 0.25) == 1.50
     * </pre>
     *
     * @param x the value to round.
     *
     * @param unit the unit (step) size.
     *
     * @return the rounded value.
     *
     * @throws IllegalArgumentException unless the unit is positive.
     */
    public static double nearest(double x, double unit) {
        if (unit <= 0.0)
            throw new IllegalArgumentException("Unit must be positive.");

        return x - Math.IEEEremainder(x, unit);
    }

    /**
     * Parses a floating-point value given as a string; the special
     * value {@code NA} is converted to {@code Double.NaN}.
     *
     * @param s the string to parse.
     *
     * @return the corresponding floating-point value.
     *
     * @throws NumberFormatException if the string is not {@code NA}
     * or a properly formatted floating-point value.
     */
    public static double parseDouble(String s) {
        if (s.equals("NA"))
            return Double.NaN;

        try {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException ex) {
            return Double.parseDouble(removeThousandsSeparator(s));
        }
    }

    private static String removeThousandsSeparator(String s) {
        return s.replace(THOUSANDS_SEPARATOR_STRING, "");
    }

    /**
     * Execute floating-point division with two integral values.
     *
     * @param numer the numerator.
     *
     * @param denom the denominator.
     *
     * @return the floating-point ratio of the arguments after
     * converting them to {@code double} values.
     */
    public static double ratio(long numer, long denom) {
	return ((double) numer) / ((double) denom);
    }

    /**
     * Replaces missing ({@code Double.NaN}) values with a default
     * value.
     *
     * @param value the value to test and potentially replace.
     *
     * @param replacement the value which will replace missing 
     * ({@code Double.NaN}) inputs.
     *
     * @return {@code Double.isNaN(value) ? replacement : value}.
     */
    public static double replaceNaN(double value, double replacement) {
        return Double.isNaN(value) ? replacement : value;
    }

    /**
     * Rounds a floating-point value to a specific number of digits.
     *
     * <p>Negative digits result in rounding on the left side of the
     * decimal point, e.g., {@code round(123456.0, -2) == 123400.0}.
     *
     * @param x the number to round.
     * @param digits the number of digits to round to.
     *
     * @return the rounded value.
     */
    public static double round(double x, int digits) {
        return nearest(x, Math.pow(10.0, -digits));
    }

    /**
     * Squares a numerical value.
     *
     * @param x a numerical value.
     *
     * @return {@code x * x}.
     */
    public static double square(double x) {
        return x * x;
    }

    /**
     * Removes the fractional part of a floating-point value (rounds
     * toward zero).
     *
     * <p>Note that this function is strictly odd ({@code truncate(-x)
     * = -truncate(x)}), while the standard library floor and ceiling
     * functions {@code Math.floor} and {@code Math.ceil} are not.
     *
     * @param x a floating-point value to truncate.
     *
     * @return the {@code double} value that is the first mathematical
     * integer encountered while shrinking the argument toward zero.
     */
    public static double truncate(double x) {
        if (x < 0.0)
            return Math.ceil(x);
        else
            return Math.floor(x);
    }

    /**
     * Converts a {@code Double} object collection into an array of
     * primitive {@code double} values.
     *
     * @param collection the collection to convert.
     *
     * @return an array of {@code double} values assigned in the order
     * returned by the collection iterator.
     */
    public static double[] toArray(Collection<Double> collection) {
        int index = 0;
        double[] result = new double[collection.size()];

        for (double value : collection)
            result[index++] = value;

        return result;
    }

    /**
     * Returns {@code Double.NaN} as a universal indicator of an unset
     * floating-point value.
     *
     * @return {@code Double.NaN} as a universal indicator of an unset
     * floating-point value.
     */
    public static double unset() {
        return Double.NaN;
    }
}
