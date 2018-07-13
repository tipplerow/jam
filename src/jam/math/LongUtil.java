
package jam.math;

/**
 * Provides static utility methods operating on {@code long} integers.
 */
public final class LongUtil {
    /**
     * Narrows a {@code long} value to an {@code int}.
     *
     * @param longValue the long value to narrow.
     *
     * @return the {@code int} value that is equivalent to the input
     * long value.
     *
     * @throws IllegalArgumentException if the long value falls
     * outside of the valid integer range.
     */
    public static int intValue(long longValue) {
        if (Integer.MIN_VALUE <= longValue && longValue <= Integer.MAX_VALUE)
            return (int) longValue;
        else
            throw new IllegalArgumentException("Long magnitude is too large to convert to int.");
    }

    /**
     * Parses a string representation of a long value.
     *
     * <p>In addition to all formats accepted by the built-in 
     * {@code Long.parseLong}, this method supports scientific
     * notation (e.g., {@code 1.23E9}) provided that the value
     * is an exact long value (has no fractional part).
     *
     * @param string the string to parse.
     *
     * @return the long value represented by the given string.
     *
     * @throws RuntimeException unless the string is properly
     * formatted.
     */
    public static long parseLong(String string) {
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException ex) {
            double result = DoubleUtil.parseDouble(string);

            if (DoubleUtil.isLong(result))
                return (long) result;
            else
                throw ex;
        }
    }
}
