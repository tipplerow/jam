
package jam.sql;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.lang.KeyedObject;
import jam.util.StringUtil;

/**
 * A database record that may be written to a delimited flat file to
 * allow a bulk import (or {@code PostgreSQL COPY}).
 */
public interface BulkRecord {
    /**
     * The delimiter to use in bulk flat files.
     */
    public static final char DELIMITER_CHAR = '|';

    /**
     * The delimiter to use in bulk flat files.
     */
    public static final String DELIMITER_STRING = String.valueOf(DELIMITER_CHAR);

    /**
     * The string that is substituted for delimiters in bulk flat
     * files.
     */
    public static final String DELIMITER_SUBSTITUTE = StringUtil.BACK_SLASH + DELIMITER_STRING;

    /**
     * The string used in bulk imports to identify {@code NULL}
     * columns.
     */
    public static final String NULL_STRING = "(null)";

    /**
     * Writes bulk records to the flat file that will be imported to a
     * database table.
     *
     * @param bulkFile the path to the file to write.
     *
     * @param records the records to include in the bulk file.
     */
    public static void writeBulkFile(File bulkFile, Collection<? extends BulkRecord> records) {
        try (PrintWriter writer = IOUtil.openWriter(bulkFile)) {
            JamLogger.info("Writing bulk import file [%s]...", bulkFile);
            records.stream().forEach(record -> writer.println(record.formatBulk()));
        }
    }

    /**
     * Ensures that the string representation of a field is valid by
     * replacing delimiter characters with the substitute character.
     *
     * @param field the string representation of a field.
     *
     * @return a cleaned version of the string.
     */
    public default String cleanField(String field) {
        //
        // Add the "escape" backslash before backslashes and delimiter
        // characters...
        //
        field = field.replace(StringUtil.BACK_SLASH, StringUtil.DOUBLE_BACK_SLASH);
        field = field.replace(DELIMITER_STRING, DELIMITER_SUBSTITUTE);

        return field;
    }

    /**
     * Formats this record as a delimited string suitable for a bulk
     * import (or {@code PostgreSQL COPY FROM} command).
     *
     * @return a delimited string representation of this record.
     */
    public abstract String formatBulk();

    /**
     * Formats a {@code boolean} column value.
     *
     * @param value the value to format.
     *
     * @return a string representation of the boolean value that will
     * be understood by the database server.
     */
    public default String formatBulk(boolean value) {
        return value ? "true" : "false";
    }

    /**
     * Formats a possibly {@code null} keyed object.
     *
     * @param obj the (possibly {@code null}) keyed object.
     *
     * @return the string value of the key, if the object is not
     * {@code null}, otherwise the {@code NULL_STRING}.
     */
    public default String formatBulk(KeyedObject<String> obj) {
        return (obj != null) ? cleanField(obj.getKey()) : NULL_STRING;
    }

    /**
     * Formats a possibly {@code null} string column.
     *
     * @param str the (possibly {@code null}) string value.
     *
     * @return the string value itself, if it is not {@code null};
     * otherwise the {@code NULL_STRING}.
     */
    public default String formatBulk(String str) {
        return (str != null) ? cleanField(str) : NULL_STRING;
    }

    /**
     * Joins the column string representations into a formatted row.
     *
     * @param columns the individually formatted columns.
     *
     * @return the delimited string representation for the row.
     */
    public default String joinBulk(String... columns) {
        return String.join(DELIMITER_STRING, columns);
    }
}
