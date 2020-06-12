
package jam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jam.math.IntUtil;

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
     * Matches a single dot (period) character.
     */
    public static final Pattern DOT = Pattern.compile("\\.");

    /**
     * Matches one or more integers anywhere within a string.
     */
    public static final Pattern INTEGERS = Pattern.compile("[0-9]+");

    /**
     * Matches one or more non-word characters.
     */
    public static final Pattern MULTI_NON_WORD = Pattern.compile("\\W+");

    /**
     * Matches one or more whitespace characters.
     */
    public static final Pattern MULTI_WHITE_SPACE = Pattern.compile("\\s+");

    /**
     * Matches the opening parethesis marker.
     */
    public static final Pattern OPEN_PAREN = Pattern.compile("\\(");

    /**
     * Matches a single pipe ({@code |}) character.
     */
    public static final Pattern PIPE = Pattern.compile(Pattern.quote("|"));

    /**
     * Matches one or more punctuation characters.
     */
    public static final Pattern PUNCTUATION = Pattern.compile("\\p{Punct}+");

    /**
     * Matches one or more punctuation characters between optional
     * white space.
     */
    public static final Pattern PUNCTUATION_WHITE_SPACE = Pattern.compile("\\s*\\p{Punct}+\\s*");

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
     * Counts the number of fields in a delimited string.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @return the number of fields in the input string.
     */
    public static int countFields(Pattern pattern, String string) {
        return pattern.split(string).length;
    }

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
        return parseDouble(split(pattern, string));
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

    private static double[] parseDouble(String[] fields) {
        double[] result = new double[fields.length];

        for (int index = 0; index < fields.length; index++)
            result[index] = Double.parseDouble(fields[index]);

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
        return parseInt(split(pattern, string));
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
            result[index] = IntUtil.parseInt(fields[index]);

        return result;
    }

    /**
     * Replaces every substring matching a given pattern.
     *
     * @param pattern the pattern to match.
     *
     * @param original the original source to search.
     *
     * @param replacement the text that will replace substrings
     * matching the pattern.
     *
     * @return a string with every instance of the pattern replaced.
     */
    public static String replace(Pattern pattern, String original, String replacement) {
        return pattern.matcher(original).replaceAll(replacement);
    }

    /**
     * Splits a string into fields defined by a given pattern and
     * trims leading and trailing white space from each field.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @return an array containing all delimted fields, stripped of
     * leading and trailing white space.
     */
    public static String[] split(Pattern pattern, String string) {
        return trim(pattern.split(string));
    }

    private static String[] trim(String[] fields) {
        for (int k = 0; k < fields.length; ++k)
            fields[k] = fields[k].trim();

        return fields;
    }

    /**
     * Splits a string into a pre-defined number of fields defined by
     * a given pattern and trims leading and trailing white space from
     * each field.
     *
     * @param pattern the delimiting pattern.
     *
     * @param string the delimited string.
     *
     * @param count the expected number of fields.
     *
     * @return an array containing all delimted fields, stripped of
     * leading and trailing white space.
     *
     * @throws IllegalArgumentException unless the number of fields
     * matches the expected value.
     */
    public static String[] split(Pattern pattern, String string, int count) {
        String[] fields = pattern.split(string);

        if (fields.length != count)
            throw new IllegalArgumentException(String.format("Expected [%d] fields but found [%d].", count, fields.length));

        return trim(fields);
    }

    /**
     * Splits strings into fields defined by a given pattern and
     * trims leading and trailing white space from each field.
     *
     * @param pattern the delimiting pattern.
     *
     * @param strings the delimited strings.
     *
     * @return a list of arrays containing all delimted fields,
     * stripped of leading and trailing white space.
     */
    public static List<String[]> split(Pattern pattern, List<String> strings) {
        List<String[]> fields = new ArrayList<String[]>(strings.size());

        for (String string : strings)
            fields.add(split(pattern, string));

        return fields;
    }

    /**
     * Splits strings into a pre-defined number of fields defined by a
     * given pattern and trims leading and trailing white space from
     * each field.
     *
     * @param pattern the delimiting pattern.
     *
     * @param strings the delimited strings.
     *
     * @param count the expected number of fields.
     *
     * @return a list of arrays containing all delimted fields,
     * stripped of leading and trailing white space.
     *
     * @throws IllegalArgumentException unless the number of fields
     * matches the expected value for all input strings.
     */
    public static List<String[]> split(Pattern pattern, List<String> strings, int count) {
        List<String[]> fields = new ArrayList<String[]>(strings.size());

        for (String string : strings)
            fields.add(split(pattern, string, count));

        return fields;
    }

    public static String stripComment(Pattern pattern, String string) {
        return split(pattern, string)[0];
    }
}
