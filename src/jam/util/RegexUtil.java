
package jam.util;

import java.util.regex.Pattern;

/**
 * Defines several common regular expressions and implements standard
 * parsing routines.
 */
public final class RegexUtil {
    private RegexUtil() {}

    /**
     * Matches a comma in a CSV file.
     */
    public static final Pattern COMMA = Pattern.compile(",");

    /**
     * Matches the colon character.
     */
    public static final Pattern COLON = Pattern.compile(":");

    /**
     * Matches the closing parethesis marker.
     */
    public static final Pattern CLOSE_PAREN = Pattern.compile("\\)");

    /**
     * Matches the C++ single-line comment marker.
     */
    public static final Pattern CPP_COMMENT = Pattern.compile("//");

    /**
     * Matches one or more whitespace characters.
     */
    public static final Pattern MULTI_WHITE_SPACE = Pattern.compile("\\s+");

    /**
     * Matches the opening parethesis marker.
     */
    public static final Pattern OPEN_PAREN = Pattern.compile("\\(");

    /**
     * Matches the Python and R single-line comment markers.
     */
    public static final Pattern PYTHON_COMMENT = Pattern.compile("#");

    /**
     * Matches the semicolon character.
     */
    public static final Pattern SEMICOLON = Pattern.compile(";");

    /**
     * Matches one whitespace character.
     */
    public static final Pattern SINGLE_WHITE_SPACE = Pattern.compile("\\s");

    /**
     * Matches single TAB characters.
     */
    public static final Pattern TAB = Pattern.compile("\\t");

    /**
     * Parses a delimited string of floating-point values.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @return the floating-point values of the fields defined by the
     * delimiter.
     *
     * @throws NumberFormatException unless all fields are properly
     * formatted floating-point values.
     */
    public static double[] parseDouble(Pattern pattern, String string) {
        return parseDouble(pattern.split(string));
    }

    /**
     * Parses a delimited string of floating-point values.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @param count the expected number of delimited fields.
     *
     * @return the floating-point values of the fields defined by the
     * delimiter.
     *
     * @throws NumberFormatException unless all fields are properly
     * formatted floating-point values.
     *
     * @throws IllegalArgumentException unless the number of fields
     * matches the expected value.
     */
    public static double[] parseDouble(Pattern pattern, String string, int count) {
        return parseDouble(split(pattern, string, count));
    }

    private static String[] split(Pattern pattern, String string, int count) {
        String[] fields = pattern.split(string);

        if (fields.length != count)
            throw new IllegalArgumentException(String.format("Expected [%d] fields but found [%d].", count, fields.length));

        return fields;
    }

    private static double[] parseDouble(String[] fields) {
        double[] result = new double[fields.length];

        for (int index = 0; index < fields.length; index++)
            result[index] = Double.parseDouble(fields[index].trim());

        return result;
    }

    /**
     * Parses a delimited string of integer values.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @return the integer values of the fields defined by the
     * delimiter.
     *
     * @throws NumberFormatException unless all fields are properly
     * formatted integer values.
     */
    public static int[] parseInt(Pattern pattern, String string) {
        return parseInt(pattern.split(string));
    }

    /**
     * Parses a delimited string of integer values.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @param count the expected number of delimited fields.
     *
     * @return the integer values of the fields defined by the
     * delimiter.
     *
     * @throws NumberFormatException unless all fields are properly
     * formatted integer values.
     *
     * @throws IllegalArgumentException unless the number of fields
     * matches the expected value.
     */
    public static int[] parseInt(Pattern pattern, String string, int count) {
        return parseInt(split(pattern, string, count));
    }

    private static int[] parseInt(String[] fields) {
        int[] result = new int[fields.length];

        for (int index = 0; index < fields.length; index++)
            result[index] = Integer.parseInt(fields[index].trim());

        return result;
    }
}
