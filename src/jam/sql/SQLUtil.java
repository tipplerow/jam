
package jam.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Provides utility methods to facilitate SQL operations.
 */
public final class SQLUtil {
    /**
     * Opens a new {@code sqlite} database connection.
     *
     * @param dbFile the path name for the database file.
     *
     * @return an open database connection for the specified URL.
     *
     * @throws RuntimeException unless the connection can be opened.
     */
    public static Connection sqlite(String dbFile) {
        try {
            Class.forName("org.sqlite.JDBC");
            return getConnection("jdbc:sqlite:" + dbFile);
        }
        catch (ClassNotFoundException ex) {
            throw JamException.runtime(ex);
        }
    }

    private static Connection getConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Closes a database connection.
     *
     * @param connection the connection to close.
     */
    public static void close(Connection connection) {
        try {
            connection.close();
        }
        catch (SQLException ex) {
            JamLogger.info("Failed to close database connection: [%s].", ex.getMessage());
        }
    }

    /**
     * Closes a result set.
     *
     * @param resultSet the result set to close.
     */
    public static void close(ResultSet resultSet) {
        try {
            resultSet.close();
        }
        catch (SQLException ex) {
            JamLogger.info("Failed to close result set: [%s].", ex.getMessage());
        }
    }

    /**
     * Counts the number of rows that contain a given integer value.
     *
     * @param connection an open database connection.
     *
     * @param tableName the name of the table to query.
     *
     * @param columnName the name of the integer column.
     *
     * @param value the integer value to count.
     *
     * @return the number of rows containing the specified integer
     * value.
     */
    public static int count(Connection connection, String tableName, String columnName, int value) {
        String queryString =
            String.format("SELECT COUNT(*) FROM %s WHERE %s = %d", tableName, columnName, value);

        return count(connection, queryString);
    }

    /**
     * Counts the number of rows that contain a given string value.
     *
     * @param connection an open database connection.
     *
     * @param tableName the name of the table to query.
     *
     * @param columnName the name of the string column.
     *
     * @param value the string value to count.
     *
     * @return the number of rows containing the specified string
     * value.
     */
    public static int count(Connection connection, String tableName, String columnName, String value) {
        String queryString =
            String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", tableName, columnName, value);
            
        return count(connection, queryString);
    }

    private static int count(Connection connection, String queryString) {
        int count = 0;

        try {
            ResultSet resultSet =
                executeQuery(connection, queryString);

            resultSet.next();
            count = resultSet.getInt(1);
            resultSet.close();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        return count;
    }

    /**
     * Creates a database table if it does not already exist.
     *
     * @param connection an open database connection.
     *
     * @param tableName the name of the new table.
     *
     * @param tableSchema the schema for the new table.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static void createTable(Connection connection, String tableName, String tableSchema) {
        executeUpdate(connection, String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tableName, tableSchema));
    }

    /**
     * Executes a database query.
     *
     * @param connection an open database connection.
     *
     * @param query the query string.
     *
     * @return the result set returned by the database.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static ResultSet executeQuery(Connection connection, String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Executes a database update.
     *
     * @param connection an open database connection.
     *
     * @param update the update string.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static void executeUpdate(Connection connection, String update) {
        try {
            Statement statement =
                connection.createStatement();

            statement.executeUpdate(update);
            statement.close();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }
}
