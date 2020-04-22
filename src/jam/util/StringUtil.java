
package jam.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides utility methods operating on strings.
 */
public final class StringUtil {
    private StringUtil() {}

    /**
     * The empty string.
     */
    public static final String EMPTY = "";

    /**
     * The backslash string.
     */
    public static final String BACK_SLASH = "\\";

    /**
     * A string with two backslashes.
     */
    public static final String DOUBLE_BACK_SLASH = BACK_SLASH + BACK_SLASH;

    /**
     * One double quotation mark.
     */
    public static final String DOUBLE_QUOTE = "\"";

    /**
     * One single quotation mark.
     */
    public static final String SINGLE_QUOTE = "'";

    /**
     * Removes the last character from a string.
     *
     * @param s the string on which to operate.
     *
     * @return a new string equal to {@code s.substring(0, s.length() - 1)}.
     *
     * @throws RuntimeException if the string is empty.
     */
    public static String chop(String s) {
        return s.substring(0, s.length() - 1);
    }

    /**
     * Encloses a string in double quotation marks.
     *
     * @param s the string to enclose.
     *
     * @return the string enclosed in double quotation marks.
     */
    public static String doubleQuote(String s) {
        return DOUBLE_QUOTE + s + DOUBLE_QUOTE;
    }

    /**
     * Identifies strings contained in double quotation marks.
     *
     * @param s the string to evaluate.
     *
     * @return {@code true} iff the input string starts and ends with
     * double quotation marks.
     */
    public static boolean isDoubleQuoted(String s) {
        return s.startsWith(StringUtil.DOUBLE_QUOTE) && s.endsWith(StringUtil.DOUBLE_QUOTE);
    }

    /**
     * Identifies strings contained in single quotation marks.
     *
     * @param s the string to evaluate.
     *
     * @return {@code true} iff the input string starts and ends with
     * single quotation marks.
     */
    public static boolean isSingleQuoted(String s) {
        return s.startsWith(StringUtil.SINGLE_QUOTE) && s.endsWith(StringUtil.SINGLE_QUOTE);
    }

    /**
     * Splits a single string across multiple lines.
     *
     * @param string the string to split.
     *
     * @param lineLength the maximum length of each line.
     *
     * @return the lines composing the original string, each limited
     * to {@code lineLength} characters.
     */
    public static List<String> multiLine(String string, int lineLength) {
        List<String> lines = new ArrayList<String>();

        while (string.length() > lineLength) {
            lines.add(string.substring(0, lineLength));
            string = string.substring(lineLength);
        }

        lines.add(string);
        return lines;
    }

    /**
     * Removes comment text from a string; comment text includes the
     * delimiter and all following characters.
     *
     * @param s the string on which to operate.
     *
     * @param delim the comment identifier.
     *
     * @return the input string stripped of all comment text.
     */
    public static String removeCommentText(String s, Pattern delim) {
        String[] fields = delim.split(s);

        if (fields.length > 0)
            return fields[0];
        else
            return "";
    }

    /**
     * Removes a fixed prefix from a string (if present).
     *
     * @param string the string on which to operate.
     *
     * @param prefix the prefix to remove.
     *
     * @return either a new string with the prefix removed (if the
     * prefix was present) or the input string (if the prefix was not
     * present).
     */
    public static String removePrefix(String string, String prefix) {
        if (string.startsWith(prefix))
            return string.substring(prefix.length());
        else
            return string;
    }

    /**
     * Removes a fixed suffix from a string (if present).
     *
     * @param string the string on which to operate.
     *
     * @param suffix the suffix to remove.
     *
     * @return either a new string with the suffix removed (if the
     * suffix was present) or the input string (if the suffix was not
     * present).
     */
    public static String removeSuffix(String string, String suffix) {
        if (string.endsWith(suffix))
            return string.substring(0, string.length() - suffix.length());
        else
            return string;
    }

    /**
     * Removes all white space from a string.
     *
     * @param s the string on which to operate.
     *
     * @return the input string stripped of all white space.
     */
    public static String removeWhiteSpace(String s) {
        return RegexUtil.SINGLE_WHITE_SPACE.matcher(s).replaceAll("");
    }

    /**
     * Encloses a string in single quotation marks.
     *
     * @param s the string to enclose.
     *
     * @return the string enclosed in single quotation marks.
     */
    public static String singleQuote(String s) {
        return SINGLE_QUOTE + s + SINGLE_QUOTE;
    }
}
