
package jam.util;

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
     * One double quotation mark.
     */
    public static final String DOUBLE_QUOTE = "\"";

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
     * Removes all white space from a string.
     *
     * @param s the string on which to operate.
     *
     * @return the input string stripped of all white space.
     */
    public static String removeWhiteSpace(String s) {
        return RegexUtil.SINGLE_WHITE_SPACE.matcher(s).replaceAll("");
    }
}
