
package jam.flat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jam.io.Delimiter;
import jam.lang.KeyedObject;

/**
 * A database record with a single primary key that may be written to
 * a delimited flat file.
 *
 * @param <K> the runtime type of the primary key.
 */
public interface FlatRecord<K> {
    /**
     * The delimiter to use for flat files.
     */
    public static final Delimiter DELIMITER = Delimiter.PIPE;

    /**
     * The string used in flat files to identify {@code null} fields.
     */
    public static final String NULL_STRING = "(null)";

    /**
     * Formats the fields of this record as strings to be exported to
     * a flat file (with delimiter characters properly escaped).
     *
     * @return a list containing the formatted fields of this record.
     */
    public abstract List<String> formatFields();

    /**
     * Returns the primary key for this record.
     *
     * @return the primary key for this record.
     */
    public abstract K getPrimaryKey();

    /**
     * Formats this record as a delimited string for export to a flat
     * file.
     *
     * @return a delimited string representation of this record.
     */
    public default String format() {
        return String.join(DELIMITER.string(), formatFields());
    }

    /**
     * Formats a {@code boolean} field value.
     *
     * @param value the value to format.
     *
     * @return a string representation of the boolean value.
     */
    public default String format(boolean value) {
        return value ? "true" : "false";
    }

    /**
     * Formats a {@code double} field value.
     *
     * @param value the value to format.
     *
     * @return a string representation of a finite double-precision
     * value (or the {@code NULL_STRING} for non-finite values).
     */
    public default String format(double value) {
        return Double.isFinite(value) ? Double.toString(value) : NULL_STRING;
    }

    /**
     * Formats an {@code int} field value.
     *
     * @param value the value to format.
     *
     * @return a string representation of the integer value.
     */
    public default String format(int value) {
        return Integer.toString(value);
    }

    /**
     * Formats an enumerated value.
     *
     * @param <E> the enumerated type.
     *
     * @param obj the enumerated value to format.
     *
     * @return the Java name of the object.
     */
    public default <E extends Enum<E>> String format(E obj) {
        return (obj != null) ? obj.name() : NULL_STRING;
    }

    /**
     * Formats a possibly {@code null} keyed object.
     *
     * @param obj the (possibly {@code null}) keyed object.
     *
     * @return the string value of the key, if the object is not
     * {@code null}, otherwise the {@code NULL_STRING}.
     */
    public default String format(KeyedObject<String> obj) {
        return (obj != null) ? format(obj.getKey()) : NULL_STRING;
    }

    /**
     * Formats a possibly {@code null} date field.
     *
     * @param value the (possibly {@code null}) date.
     *
     * @return the string representation of the date, if it is not
     * {@code null}; otherwise the {@code NULL_STRING}.
     */
    public default String format(LocalDate value) {
        return (value != null) ? value.toString() : NULL_STRING;
    }

    /**
     * Formats a possibly {@code null} time stamp field.
     *
     * @param value the (possibly {@code null}) time stamp value.
     *
     * @return the string representation of the time stamp, if it is
     * not {@code null}; otherwise the {@code NULL_STRING}.
     */
    public default String format(LocalDateTime value) {
        return (value != null) ? value.toString() : NULL_STRING;
    }

    /**
     * Formats a possibly {@code null} string field.
     *
     * @param str the (possibly {@code null}) string value.
     *
     * @return the string value itself, if it is not {@code null};
     * otherwise the {@code NULL_STRING}.
     */
    public default String format(String str) {
        return (str != null) ? DELIMITER.escape(str) : NULL_STRING;
    }

    /**
     * Determines whether a string field represents a {@code null}
     * value.
     *
     * @param field the string field to examine.
     *
     * @return {@code true} iff the given string field represents a
     * {@code null} value.
     */
    public static boolean isNull(String field) {
        return field == null || field.equals(NULL_STRING);
    }

    /**
     * Parses a local date field.
     *
     * @param field the field to parse.
     *
     * @return the local date value of the given field.
     */
    public static LocalDate parseDate(String field) {
        if (isNull(field))
            return null;
        else
            return LocalDate.parse(field);
    }

    /**
     * Parses a double-precision field.
     *
     * @param field the field to parse.
     *
     * @return the double-precision value of the given field (or
     * {@code Double.NaN} for the {@code (null)} string).
     */
    public static double parseDouble(String field) {
        if (isNull(field))
            return Double.NaN;
        else
            return Double.parseDouble(field);
    }

    /**
     * Parses an integer field.
     *
     * @param field the field to parse.
     *
     * @return the integer value of the given field ({@code 0} for the
     * {@code (null)} string).
     */
    public static int parseInt(String field) {
        if (isNull(field))
            return 0;
        else
            return Integer.parseInt(field);
    }

    /**
     * Parses a string field.
     *
     * @param field the field to parse.
     *
     * @return the string value of the given field ({@code null} for
     * the {@code (null)} string).
     */
    public static String parseString(String field) {
        if (isNull(field))
            return null;
        else
            return field;
    }

    /**
     * Splits a line in a flat file into its individual fields.
     *
     * @param line the line to split.
     *
     * @param count the number of fields to expect.
     *
     * @return an array containing the record fields (with leading and
     * trailing white space removed).
     *
     * @throws RuntimeException unless the number of fields matches
     * the expected count.
     */
    public static String[] split(String line, int count) {
        return DELIMITER.split(line, count);
    }
}
