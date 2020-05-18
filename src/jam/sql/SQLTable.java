
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.util.EnumUtil;

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
     * Retrieves a {@code double} value from a result set, converting
     * database {@code NULL} values to {@code Double.NaN} (instead of
     * {@code 0.0} like the default behavior of {@code SQLite}).
     *
     * @param resultSet an open result set.
     *
     * @param columnIndex the column index (the first is 1, second is 2, ...).
     *
     * @return the {@code double} value in the specified column of the
     * result set.
     *
     * @throws SQLException if the column index is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public static double getDouble(ResultSet resultSet, int columnIndex) throws SQLException {
        return getDouble(resultSet.getObject(columnIndex));
    }

    /**
     * Retrieves a {@code double} value from a result set, converting
     * database {@code NULL} values to {@code Double.NaN} (instead of
     * {@code 0.0}).
     *
     * @param resultSet an open result set.
     *
     * @param columnLabel the column label.
     *
     * @return the {@code double} value in the specified column of the
     * result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public static double getDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        return getDouble(resultSet.getObject(columnLabel));
    }

    private static double getDouble(Object obj) {
        if (obj != null)
            return ((Double) obj).doubleValue();
        else
            return Double.NaN;
    }

    /**
     * Retrieves an enumerated value from a result set.
     *
     * @param <E> the runtime enum type.
     *
     * @param resultSet an open result set.
     *
     * @param enumClass the desired runtime enum class.
     *
     * @param columnLabel the column label.
     *
     * @return the enumerated value.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public static <E extends Enum<E>> E getEnum(ResultSet resultSet,
                                                Class<E>  enumClass,
                                                String    columnLabel) throws SQLException {
        return EnumUtil.valueOf(enumClass, getString(resultSet, columnLabel));
    }

    /**
     * Retrieves a {@code String} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnLabel the column label.
     *
     * @return the {@code String} value in the specified column of the
     * result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public static String getString(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    /**
     * Retrieves a {@code LocalDateTime} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnLabel the column label.
     *
     * @return the {@code LocalDateTime} value in the specified column
     * of the result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public static LocalDateTime getTimeStamp(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getObject(columnLabel, LocalDateTime.class);
    }

    /**
     * Returns a {@code RuntimeException} to throw when passed an
     * invalid column name.
     *
     * @param badColumn the invalid column name.
     *
     * @return the {@code RuntimeException} to be thrown.
     */
    public static RuntimeException invalidColumn(String badColumn) {
        return JamException.runtime("Invalid column: [%s].", badColumn);
    }

    /**
     * Assigns a possibly {@code null} enumerated value as a parameter
     * in a prepared statement.
     *
     * @param <E> the enumerated class.
     *
     * @param statement the prepared statement to populate.
     *
     * @param index the index of the parameter in the statement (the
     * first is 1, second is 2, ...).
     *
     * @param value the possibly {@code null} enum value to assign.
     *
     * @throws SQLException if the statement is closed, the index is
     * invalid, or if a database error occurs.
     */
    public static <E extends Enum<E>> void setEnum(PreparedStatement statement, int index, E value) throws SQLException {
        if (value != null)
            statement.setObject(index, value, Types.OTHER);
        else
            statement.setNull(index, Types.OTHER);
    }

    /**
     * Assigns a possibly {@code null} string value as a parameter in
     * a prepared statement.
     *
     * @param statement the prepared statement to populate.
     *
     * @param index the index of the parameter in the statement (the
     * first is 1, second is 2, ...).
     *
     * @param object the possibly {@code null} object to assign.
     *
     * @throws SQLException if the statement is closed, the index is
     * invalid, or if a database error occurs.
     */
    public static void setKeyedObject(PreparedStatement statement, int index, KeyedObject<String> object) throws SQLException {
        if (object != null)
            setString(statement, index, object.getKey());
        else
            statement.setNull(index, Types.VARCHAR);
    }

    /**
     * Assigns a possibly {@code null} string value as a parameter in
     * a prepared statement.
     *
     * @param statement the prepared statement to populate.
     *
     * @param index the index of the parameter in the statement (the
     * first is 1, second is 2, ...).
     *
     * @param value the possibly {@code null} string value to assign.
     *
     * @throws SQLException if the statement is closed, the index is
     * invalid, or if a database error occurs.
     */
    public static void setString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value != null)
            statement.setString(index, value);
        else
            statement.setNull(index, Types.VARCHAR);
    }

    /**
     * Assigns a possibly {@code null} time stamp as a parameter in a
     * prepared statement.
     *
     * @param statement the prepared statement to populate.
     *
     * @param index the index of the parameter in the statement (the
     * first is 1, second is 2, ...).
     *
     * @param value the possibly {@code null} time stamp value to assign.
     *
     * @throws SQLException if the statement is closed, the index is
     * invalid, or if a database error occurs.
     */
    public static void setTimeStamp(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value != null)
            statement.setObject(index, value);
        else
            statement.setNull(index, Types.OTHER);
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
     * Creates this table in the database unless it already exists.
     *
     * @throws RuntimeException if the table does not already exist
     * and cannot be created.
     */
    public synchronized void require() {
        getSchema().createTable(db);
    }
}
