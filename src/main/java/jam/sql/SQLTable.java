
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import jam.app.JamLogger;

/**
 * Provides the base class for database table managers.
 */
public abstract class SQLTable {
    private Connection connection = null;

    /**
     * The manager for the database containing this table.
     */
    protected final SQLDb db;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     */
    protected SQLTable(SQLDb db) {
        this.db = db;
    }

    /**
     * Returns the open database connection for this table <em>with
     * auto-commit mode disabled</em>.
     *
     * @return the open database connection for this table.
     *
     * @throws SQLException if a connection cannot be opened.
     */
    protected synchronized Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = db.openConnection();
            connection.setAutoCommit(false);
        }

        return connection;
    }

    /**
     * Prepares a statement using the open database connection.
     *
     * @param sql the statement text.
     *
     * @return the new prepared statement.
     *
     * @throws SQLException if any database errors occur.
     */
    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    /**
     * Rolls back the latest database transaction.
     *
     * @param connection the open database connection.
     *
     * @throws SQLException if the rollback fails.
     */
    protected void rollback(Connection connection) throws SQLException {
        JamLogger.warn("Failed to update database table!");

        try {
            connection.rollback();
        }
        catch (SQLException ex) {
            JamLogger.warn("Failed to rollback update transaction!");
            JamLogger.warn(ex);
        }
    }

    /**
     * Returns the column meta-data (ordered from left to right).
     *
     * <p>The key must be in the first (left-most) column.
     *
     * @return the column meta-data (ordered from left to right).
     */
    public abstract List<SQLColumn> getColumns();

    /**
     * Returns the name of the database table.
     *
     * @return the name of the database table.
     */
    public abstract String getTableName();

    /**
     * Deletes all records from this table.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public void clear() {
        db().executeUpdate(formatClear());
    }

    private String formatClear() {
        return String.format("DELETE FROM %s", getTableName());
    }

    /**
     * Returns the number of columns in this table.
     *
     * @return the number of columns in this table.
     */
    public int countColumns() {
        return getColumns().size();
    }

    /**
     * Returns the database that contains this table.
     *
     * @return the database that contains this table.
     */
    public SQLDb db() {
        return db;
    }

    /**
     * Determines whether this table exists in the database.
     *
     * @return {@code true} iff this table exists in the database.
     */
    public synchronized boolean exists() {
        return db.tableExists(getTableName());
    }

    /**
     * Returns the name of an indexed column <em>with the first column
     * having index 1</em> (as in a {@code ResultSet}).
     *
     * @param index the unit-offset index (first column = 1).
     *
     * @return the name of the indexed column.
     */
    public String getColumnName(int index) {
        return getColumns().get(index - 1).getName();
    }

    /**
     * Returns the names of the columns (in order from left to right).
     *
     * @return the names of the columns (in order from left to right).
     */
    public List<String> getColumnNames() {
        return SQLColumn.getNames(getColumns());
    }

    /**
     * Returns the table schema.
     *
     * @return the table schema.
     */
    public SQLSchema getSchema() {
        return SQLSchema.create(getTableName(), getColumns());
    }

    /**
     * Executes a SQL query against this table.
     *
     * <p>To avoid resource leaks, the query result must be closed
     * after the result set has been processed.
     *
     * @param queryStr the SQL query to execute.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public QueryResult query(String queryStr) throws SQLException {
        return QueryResult.create(getConnection(), queryStr, false);
    }

    /**
     * Creates this table in the database unless it already exists.
     *
     * @throws RuntimeException if the table does not already exist
     * and cannot be created.
     */
    public synchronized void require() {
        getSchema().createTable(db);
    }

    /**
     * Returns a result set containing all of the records in this
     * table.
     *
     * <p>To avoid resource leaks, the query result must be closed
     * after the result set has been processed.
     *
     * @return a result set containing all of the records in this
     * table.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public QueryResult selectAll() throws SQLException {
        return query(formatSelectAll());
    }

    private String formatSelectAll() {
        return String.format("SELECT * FROM %s", getTableName());
    }
}
