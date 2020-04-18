
package jam.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Manages a SQL database and its connections.
 *
 * <p><b>Exception handling.</b> All public methods in this class catch
 * {@code SQLException}s and re-throw them as {@code RuntimeException}s.
 */
public abstract class SQLDb {
    private boolean verbose = false;

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

    /**
     * Opens a new database connection.
     *
     * @return a new open database connection.
     *
     * @throws RuntimeException if the connection cannot be opened.
     */
    public abstract Connection openConnection();

    /**
     * Formats the query required to count the number of tables with
     * a given name (used to determine whether the table exists).
     *
     * @param tableName the name of the database table.
     *
     * @return the query required to count the number of tables with
     * the specified name.
     */
    protected abstract String countTableNamesQuery(String tableName);

    /**
     * Commits pending transactions on a connection (unless the
     * connection is in auto-commit mode).
     *
     * @param connection an open database connection.
     *
     * @throws RuntimeException if the pending transactions cannot be
     * committed.
     */
    public static void commit(Connection connection) {
        try {
            if (!connection.getAutoCommit())
                connection.commit();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Creates an index on an existing database table.
     *
     * @param tableName the name of the existing table.
     *
     * @param columnName the name of the column to be indexed.
     */
    public void createIndex(String tableName, String columnName) {
        executeUpdate(formatCreateIndex(tableName, columnName));
    }

    private static String formatCreateIndex(String tableName, String columnName) {
        return String.format("CREATE INDEX IF NOT EXISTS %s_idx ON %s(%s)", columnName, tableName, columnName);
    }

    /**
     * Creates a new database table unless it already exists.
     *
     * @param tableName the name of the database table.
     *
     * @param tableSchema the schema for the new database table.
     *
     * @throws RuntimeException if the table cannot be created.
     */
    public void createTable(String tableName, String tableSchema) {
        executeUpdate(createTableUpdate(tableName, tableSchema));
    }

    private static String createTableUpdate(String tableName, String tableSchema) {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tableName, tableSchema);
    }

    /**
     * Deletes a database table if it exists.
     *
     * @param tableName the name of the database table to delete.
     *
     * @throws RuntimeException if the table cannot be deleted.
     */
    public void dropTable(String tableName) {
        executeUpdate(dropTableUpdate(tableName));
    }

    private static String dropTableUpdate(String tableName) {
        return String.format("DROP TABLE IF EXISTS %s", tableName);
    }

    /**
     * Executes a query using a new database connection.
     *
     * <p>The new connection, statement, and result set should be
     * closed after the query result has been processed by calling
     * {@code queryResult.close()}.
     *
     * @param queryStr the SQL query to execute.
     *
     * @return the result set containing the query results.
     *
     * @throws RuntimeException if the query cannot be executed.
     */
    public QueryResult executeQuery(String queryStr) {
        return QueryResult.create(openConnection(), queryStr, true);
    }

    /**
     * Executes a query using an existing open statement.
     *
     * <p>The result set should be closed after it has been processed.
     *
     * @param statement an open statement.
     *
     * @param queryStr the SQL query to execute.
     *
     * @return the result set containing the query results.
     *
     * @throws RuntimeException if the query cannot be executed.
     */
    public ResultSet executeQuery(Statement statement, String queryStr) {
        logQuery(queryStr);

        try {
            return statement.executeQuery(queryStr);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Executes and commits an atomic update command using a new
     * database connection (which is closed before returning).
     *
     * @param updateStr the SQL update to execute.
     *
     * @return the SQL result code.
     *
     * @throws RuntimeException if the update cannot be executed.
     */
    public int executeUpdate(String updateStr) {
        try (Connection connection = openConnection()) {
            return executeUpdate(connection.createStatement(), updateStr, true);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Executes an update command using an open statement.
     *
     * @param statement an open statement.
     *
     * @param updateStr the SQL update to execute.
     *
     * @param commit whether to commit the update.
     *
     * @return the SQL result code.
     *
     * @throws RuntimeException if the update cannot be executed.
     */
    public int executeUpdate(Statement statement, String updateStr, boolean commit) {
        logUpdate(updateStr);

        try {
            int result = statement.executeUpdate(updateStr);

            if (commit)
                commit(statement.getConnection());

            return result;
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Returns the result of a {@code SELECT COUNT} query.
     *
     * @param resultSet the result set returned by the query.
     *
     * @return the result of the {@code SELECT COUNT} query.
     *
     * @throws RuntimeException if the result set inspection fails.
     */
    public static int getCount(ResultSet resultSet) {
        int result = 0;

        try {
            if (!resultSet.next())
                throw JamException.runtime("No result set rows.");

            result = resultSet.getInt(1);

            if (resultSet.next())
                throw JamException.runtime("Multiple result set rows.");
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        return result;
    }

    /**
     * Opens a new database connection and sets the auto-commit flag.
     *
     * @param autoCommit the auto-commit flag.
     *
     * @return a new open database connection.
     *
     * @throws RuntimeException if the connection cannot be opened.
     */
    public Connection openConnection(boolean autoCommit) {
        Connection connection = openConnection();

        try {
            connection.setAutoCommit(autoCommit);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        return connection;
    }

    /**
     * Determines whether a database table already exists.
     *
     * @param tableName the name of the database table.
     *
     * @return {@code true} iff the table already exists.
     */
    public boolean tableExists(String tableName) {
        String queryStr = countTableNamesQuery(tableName);

        try (QueryResult queryResult = executeQuery(queryStr)) {
            return tableExists(queryResult.getResultSet());
        }
    }

    private static boolean tableExists(ResultSet resultSet) {
        return getCount(resultSet) == 1;
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
