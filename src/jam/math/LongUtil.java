
package jam.math;

/**
 * Provides static utility methods operating on {@code long} integers.
 */
public final class LongUtil {
    /**
     * Parses a string representation of a long value.
     *
     * <p>In addition to all formats accepted by the built-in 
     * {@code Long.parseLong}, this method supports scientific
     * notation (e.g., {@code 1.23E9}) as long as the value is
     * an exact long value (has no fractional part).
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

            if (DoubleUtil.isRound(result))
                return (long) result;
            else
                throw ex;
        }
    }
}
