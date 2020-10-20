
package jam.io;

import java.util.List;
import java.util.regex.Pattern;

import jam.lang.JamException;

/**
 * Enscapsulates the form and function of a flat-file delimiter.
 */
public final class Delimiter {
    private final String delimString;
    private final String escapeString;
    private final Pattern delimPattern;

    private Delimiter(String delimString) {
        this.delimString = delimString;
        this.escapeString = escapeString(delimString);
        this.delimPattern = delimPattern(delimString);
    }

    private Delimiter(String delimString,
                      String escapeString,
                      Pattern delimPattern) {
        this.delimString = delimString;
        this.escapeString = escapeString;
        this.delimPattern = delimPattern;
    }

    private static String escapeString(String delimString) {
        return "\\" + delimString;
    }

    private static Pattern delimPattern(String delimString) {
        return Pattern.compile("(?<!\\\\)" + Pattern.quote(delimString));
    }

    /**
     * The comma delimiter for CSV files.
     */
    public static Delimiter COMMA = new Delimiter(",");

    /**
     * A hyphen delimiter.
     */
    public static Delimiter HYPHEN = new Delimiter("-");

    /**
     * The pipe delimiter for PSV files.
     */
    public static Delimiter PIPE = new Delimiter("|");

    /**
     * The tab delimiter for TSV and TXT files.
     */
    public static Delimiter TAB = new Delimiter("\t", null, Pattern.compile("\\t"));

    /**
     * The white-space delimiter.
     */
    public static Delimiter WHITE_SPACE = new Delimiter(" ", null, Pattern.compile("\\s+"));

    /**
     * Adds a backslash before all non-white space delimiter
     * characters in a field string.
     *
     * @param field the field to escape.
     *
     * @return the escaped field.
     */
    public String escape(String field) {
        if (escapeString != null)
            return field.replace(delimString, escapeString);
        else
            return field;
    }

    /**
     * Joins a sequence of fields into a single delimited string.
     *
     * @param fields the fields to join.
     *
     * @return a delimited string containing the specified fields.
     */
    public String join(String... fields) {
        return join(List.of(fields));
    }

    /**
     * Joins a sequence of fields into a single delimited string.
     *
     * @param fields the fields to join.
     *
     * @return a delimited string containing the specified fields.
     */
    public String join(List<String> fields) {
        if (fields.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append(escape(fields.get(0)));

        for (int index = 1; index < fields.size(); ++index) {
            builder.append(delimString);
            builder.append(escape(fields.get(index)));
        }

        return builder.toString();
    }

    /**
     * Extracts the individual fields from a delimited string.
     *
     * @param line the line to split.
     *
     * @return the fields contained in the delimited string (with
     * leading and trailing white space removed).
     */
    public String[] split(String line) {
        String[] fields = delimPattern.split(line, -1);

        for (int index = 0; index < fields.length; ++index)
            fields[index] = fields[index].trim();

        if (escapeString != null)
            for (int index = 0; index < fields.length; ++index)
                fields[index] = unescape(fields[index]);

        return fields;
    }

    /**
     * Extracts the individual fields from a delimited string.
     *
     * @param line the line to split.
     *
     * @param count the number of fields to expect.
     *
     * @return the fields contained in the delimited string (with
     * leading and trailing white space removed).
     *
     * @throws RuntimeException unless the number of fields matches
     * the expected count.
     */
    public String[] split(String line, int count) {
        String[] fields = split(line);

        if (fields.length != count)
            throw JamException.runtime("Invalid line [%s]: expected [%d] fields but found [%d].",
                                       line, count, fields.length);

        return fields;
    }

    /**
     * Returns the literal delimiter string.
     *
     * @return the literal delimiter string.
     */
    public String string() {
        return delimString;
    }

    /**
     * Removes all backslash characters that precede delimiters in an
     * escaped field.
     *
     * @param field the field to <em>unescape</em>.
     *
     * @return the unescaped field.
     */
    public String unescape(String field) {
        if (escapeString != null)
            return field.replace(escapeString, delimString);
        else
            return field;
    }
}
