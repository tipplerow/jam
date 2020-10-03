
package jam.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamEnv;
import jam.app.JamLogger;
import jam.lang.JamException;
import jam.util.StringUtil;

/**
 * Manages a SQL database and its connections.
 *
 * <p><b>Exception handling.</b> All public methods in this class catch
 * {@code SQLException}s and re-throw them as {@code RuntimeException}s.
 */
public abstract class SQLDb {
    private boolean verbose = false;
    private Connection connection = null;

    /**
     * Creates a new SQL database manager.
     *
     * @param driverClass the name of the driver class to load.
     *
     * @throws RuntimeException if the driver class cannot be loaded.
     */
    protected SQLDb(String driverClass) {
        loadDriver(driverClass);
    }

    private static void loadDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        }
        catch (ClassNotFoundException ex) {
            throw JamException.runtime(ex);
        }
    }

    private synchronized Connection getConnection() {
        if (connection == null)
            connection = openConnection();

        return connection;
    }

    /**
     * Returns the enumerated database engine type.
     *
     * @return the enumerated database engine type.
     */
    public abstract SQLEngine getEngineType();

    /**
     * Opens a new database connection.
     *
     * @return a new open database connection.
     *
     * @throws RuntimeException if the connection cannot be opened.
     */
    public abstract Connection openConnection();

    /**
     * Imports a collection of records into a database table by
     * writing the records to a flat file and using a bulk copy
     * command.
     *
     * @param tableName the name of the table to update.
     *
     * @param records the records to import.
     *
     * @throws RuntimeException if the bulk copy fails.
     */
    public void bulkCopy(String tableName, Collection<? extends BulkRecord> records) {
        File bulkFile = null;

        try {
            bulkFile = File.createTempFile("bulk_", ".psv", JamEnv.tmpdir());
            BulkRecord.writeBulkFile(bulkFile, records);
            bulkCopy(tableName, bulkFile.getCanonicalPath(), BulkRecord.DELIMITER_CHAR, BulkRecord.NULL_STRING);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
        finally {
            if (bulkFile != null)
                bulkFile.delete();
        }
    }

    /**
     * Copies rows from a delimited file (with no header line).
     *
     * @param tableName the name of the table to populate.
     *
     * @param fileName the name of a delimited file containing the
     * rows to be added (with no header line).
     *
     * @param delimiter the column delimiter in the flat file.
     *
     * @param nullString the SQL {@code NULL} identifier in the flat file.
     *
     * @throws RuntimeException if the update cannot be executed.
     */
    public void bulkCopy(String tableName, String fileName, char delimiter, String nullString) {
        executeUpdate(formatBulkCopy(tableName, fileName, delimiter, nullString));
    }

    private static String formatBulkCopy(String tableName, String fileName, char delimiter, String nullString) {
        return String.format("COPY %s FROM '%s' WITH DELIMITER '%c' NULL '%s'",
                             tableName, fileName, delimiter, nullString);
    }

    /**
     * Creates an enumerated database type.
     *
     * @param <E> the enumerated class.
     *
     * @param typeName the name for the enum type in the database (not
     * necessarily the name of the Java enum class).
     *
     * @param enumClass the class object for the enum to create.
     */
    public <E extends Enum<E>> void createEnum(String typeName, Class<E> enumClass) {
        //
        // There is no "create if not exists" for types, so catch the
        // exception that will occur if the enum already exists...
        //
        try {
            executeUpdate(formatCreateEnum(typeName, enumClass));
        }
        catch (Exception ex) {
            if (!ex.getMessage().endsWith("already exists"))
                throw JamException.runtime(ex);
        }
    }

    private static <E extends Enum<E>> String formatCreateEnum(String typeName, Class<E> enumClass) {
        List<String> quotedNames = new ArrayList<String>();

        for (E value : enumClass.getEnumConstants())
            quotedNames.add(StringUtil.singleQuote(value.name()));

        return String.format("CREATE TYPE %s AS ENUM (%s)", typeName, String.join(", ", quotedNames));
    }

    /**
     * Creates the table and indexes described by a schema (unless the
     * table already exists).
     *
     * @param schema the schema for the new database table.
     *
     * @throws RuntimeException if any database errors occur.
     */
    public void createTable(SQLSchema schema) {
        executeUpdate(schema.formatCreateTable(getEngineType()));

        for (String createIndexCommand : schema.formatCreateIndex())
            executeUpdate(createIndexCommand);
    }

    /**
     * Deletes a database table if it exists.
     *
     * @param tableName the name of the database table to delete.
     *
     * @throws RuntimeException if the table cannot be deleted.
     */
    public void dropTable(String tableName) {
        executeUpdate(formatDropTable(tableName));
    }

    private static String formatDropTable(String tableName) {
        return String.format("DROP TABLE IF EXISTS %s", tableName);
    }

    /**
     * Executes and commits an atomic update command.
     *
     * @param sql the SQL update to execute.
     *
     * @return the SQL result code.
     *
     * @throws RuntimeException if the update cannot be executed.
     */
    public int executeUpdate(String sql) {
        logUpdate(sql);

        try (Statement statement = getConnection().createStatement()) {
            return statement.executeUpdate(sql);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Turns verbose message logging on or off.
     *
     * @param verbose whether to log messages in a verbose manner.
     */
    public void verbose(boolean verbose) {
        this.verbose = verbose;
    }

    private void logQuery(String message) {
        if (verbose)
            JamLogger.info(message);
    }

    private void logUpdate(String message) {
        if (verbose)
            JamLogger.info(message);
    }
}
