
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamLogger;
import jam.collect.JamTable;
import jam.lang.JamException;
import jam.report.LineBuilder;
import jam.stream.JamStreams;

/**
 * Provides a base class to manage database tables.
 *
 * @param <K> the runtime type for the table key.
 *
 * @param <V> the runtime type for the table records.
 */
public abstract class SQLTable<K, V> implements JamTable<K, V>, TableProcessor {
    private Connection connection = null;

    /**
     * The manager for the database containing this table.
     */
    protected final SQLDb db;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     *
     * @throws RuntimeException unless the table contains at least one
     * key column.
     */
    protected SQLTable(SQLDb db) {
        this.db = db;
    }

    private String formatDelete(String columnName) {
        return String.format("DELETE FROM %s WHERE %s = ?", getTableName(), columnName);
    }

    private String formatDeleteAll() {
        return String.format("DELETE FROM %s", getTableName());
    }

    private String formatDeleteKey() {
        return String.format("DELETE FROM %s %s", getTableName(), formatWhere(getKeyColumns()));
    }

    private String formatInsert() {
        return String.format("INSERT INTO %s VALUES(%s)", getTableName(), formatInsertParameters());
    }

    private String formatInsertParameters() {
        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : getColumns())
            if (column.isSerial())
                builder.append("DEFAULT");
            else
                builder.append("?");

        return builder.toString();
    }

    private String formatSelect(String columnName) {
        return String.format("SELECT * FROM %s WHERE %s = ?", getTableName(), columnName);
    }

    private String formatSelectAll() {
        return String.format("SELECT * FROM %s", getTableName());
    }

    private String formatSelectCount() {
        return String.format("SELECT COUNT(*) FROM %s", getTableName());
    }

    private String formatSelectCount(String columnName, String columnValue) {
        return String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", getTableName(), columnName, columnValue);
    }

    private String formatSelectKey() {
        return String.format("SELECT * FROM %s %s", getTableName(), formatWhere(getKeyColumns()));
    }

    private static String formatWhere(List<SQLColumn> columns) {
        LineBuilder builder = new LineBuilder(" AND ");

        for (SQLColumn column : columns)
            builder.append(String.format("%s = ?", column.getName()));

        return "WHERE " + builder.toString();
    }

    /**
     * Commits any outstanding transactions on the open database
     * connection.
     *
     * @throws SQLException unless the connection is open and the
     * commit succeeds.
     */
    protected void commit() throws SQLException {
        getConnection().commit();
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
     * Creates a new statement using the open database connection.
     *
     * @return the new statement.
     *
     * @throws SQLException if any database errors occur.
     */
    protected Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    /**
     * Prepares the insert statement using the open database
     * connection.
     *
     * @return the new prepared statement.
     *
     * @throws SQLException if any database errors occur.
     */
    protected PreparedStatement prepareInsert() throws SQLException {
        return prepareStatement(formatInsert());
    }

    /**
     * Prepares the select-by-key statement using the open database
     * connection.
     *
     * @return the new prepared statement.
     *
     * @throws SQLException if any database errors occur.
     */
    protected PreparedStatement prepareSelectKey() throws SQLException {
        return prepareStatement(formatSelectKey());
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
     * @return the column meta-data (ordered from left to right).
     */
    public abstract List<SQLColumn> getColumns();

    /**
     * Returns the record stored in the current row of a result set
     * produced by a {@code SELECT} query.
     *
     * @param resultSet an active result set.
     *
     * @return the record stored in the current row of the result set.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract V getRecord(ResultSet resultSet) throws SQLException;

    /**
     * Returns the name of the database table.
     *
     * @return the name of the database table.
     */
    public abstract String getTableName();

    /**
     * Assigns each field in a record key to its corresponding column
     * in a prepared statement (to be used in select queries).
     *
     * @param statement a prepared statement with one parameter for
     * each key column in this table [in the order defined by the
     * method {@code getColumns()}].
     *
     * @param record the record to assign to the statement.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public abstract void setKey(PreparedStatement statement, K key) throws SQLException;

    /**
     * Assigns each field in a record to its corresponding column in a
     * prepared statement (to be used to insert the record into this
     * table).
     *
     * @param statement a prepared statement with one parameter for
     * each column in this table [in the order defined by the method
     * {@code getColumns()}].
     *
     * @param record the record to assign to the statement.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public abstract void setRecord(PreparedStatement statement, V row) throws SQLException;

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
     * Deletes rows for which a particular column matches a target
     * value.
     *
     * @param columnName the name of the column to match on.
     *
     * @param targetValues the column values to match.
     *
     * @throws RuntimeException if this table does not contain a
     * column with the specified name or if any SQL errors occur.
     */
    /*
    public void delete(String columnName, Collection<String> targetValues) {
        String sql = formatDelete(columnName);

        try (PreparedStatement statement = prepareStatement(sql)) {
            for (String target : targetValues) {
                statement.setString(1, target);
                statement.addBatch();
            }

            statement.executeBatch();
            commit();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }
    */

    /**
     * Determines whether this table exists in the database.
     *
     * @return {@code true} iff this table exists in the database.
     */
    public synchronized boolean exists() {
        return db.tableExists(getTableName());
    }

    /**
     * Returns the unit-offset index (left-most column = 1) of the
     * column with a given name.
     *
     * @param columnName the name of the indexed column.
     *
     * @return the unit-offset index (left-most column = 1) of the
     * column with the specified name (or {@code 0} if there is no
     * matching column).
     */
    public int getColumnIndex(String columnName) {
        for (int index = 1; index <= countColumns(); ++index)
            if (getColumnName(index).equals(columnName))
                return index;

        return 0;
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
     * Returns the result of a {@code SELECT COUNT(*)} query and
     * closes the result set.
     *
     * @return the result of a {@code SELECT COUNT(*)} query.
     */
    public int getCount(ResultSet resultSet) throws SQLException {
        try {
            resultSet.next();
            return resultSet.getInt(1);
        }
        finally {
            resultSet.close();
        }
    }

    /**
     * Returns the meta-data for the key columns in this table.
     *
     * @return the meta-data for the key columns in this table.
     *
     * @throws RuntimeException unless this table contains at least
     * one key column.
     */
    public List<SQLColumn> getKeyColumns() {
        List<SQLColumn> keyColumns =
            JamStreams.filter(getColumns(), column -> column.isKey());

        if (keyColumns.isEmpty())
            throw JamException.runtime("No key columns for table [%s].", getTableName());
        else
            return keyColumns;
    }

    /**
     * Returns all records remaining in a result set produced by a
     * {@code SELECT} query and closes the result set.
     *
     * @param resultSet an active result set, which is closed upon
     * completion.
     *
     * @return a list containing all remaining records stored in the
     * specified result set.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public List<V> getRecords(ResultSet resultSet) throws SQLException {
        List<V> rows = new ArrayList<V>();

        try {
            while (resultSet.next())
                rows.add(getRecord(resultSet));

            return rows;
        }
        finally {
            resultSet.close();
        }
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
     * Returns the single record stored in a result set produced by
     * a {@code SELECT} query and closes the result set.
     *
     * @param resultSet an active result set.
     *
     * @return the single record stored in the result set (or
     * {@code null} if the result set is empty).
     *
     * @throws SQLException if any SQL errors occur.
     */
    public V getSingle(ResultSet resultSet) throws SQLException {
        try {
            if (resultSet.next())
                return getRecord(resultSet);
            else
                return null;
        }
        finally {
            resultSet.close();
        }
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
     * Selects rows for which a particular column matches a target
     * value.
     *
     * @param columnName the name of the column to match on.
     *
     * @param targetValues the column values to match.
     *
     * @return all rows matching the the specified target values.
     *
     * @throws RuntimeException if this table does not contain a
     * column with the specified name or if any SQL errors occur.
     */
    /*
    public List<V> select(String columnName, Collection<String> targetValues) {
        String sql = formatSelect(columnName);

        try (PreparedStatement statement = prepareStatement(sql)) {
            List<V> rows = new ArrayList<V>();

            for (String target : targetValues) {
                statement.setString(1, target);
                
                ResultSet resultSet = statement.executeQuery();
                V record = getSingle(resultSet);

                if (record != null)
                    rows.add(record);
            }

            return rows;
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }
    */

    /**
     * Returns the number of rows containing a target value in a named
     * column.
     *
     * @param columnName the column name to examine.
     *
     * @param targetValue the target value to match.
     *
     * @return the number of rows containing the specified target
     * value in the named column.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    /*
    public int selectCount(String columnName, String targetValue) {
        return selectCount(formatSelectCount(columnName, targetValue));
    }

    */

    /**
     * Returns the number of rows in this table.
     *
     * @return the number of rows in this table.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public int count() {
        String sql = formatSelectCount();

        try (Statement statement = createStatement()) {
            return getCount(statement.executeQuery(sql));
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Deletes all rows from this table.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void delete() {
        try (Statement statement = createStatement()) {
            statement.executeUpdate(formatDeleteAll());
            commit();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Deletes the record indexed by a given key (a no-op if there is
     * no matching record).
     *
     * @param key the key of the record to delete.
     *
     * @return {@code true} iff the matching record was deleted.
     */
    @Override public boolean delete(K key) {
        int result = 0;
        String sql = formatDeleteKey();

        try (PreparedStatement statement = prepareStatement(sql)) {
            setKey(statement, key);
            result = statement.executeUpdate();
            commit();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }

        return result == 1;
    }

    /**
     * Deletes the records indexed by collection of keys.
     *
     * @param keys the keys of the records to delete.
     *
     * @return a list containing the records matching the specified key.
     * The matching records are returned in the same order as their keys
     * appear in the input collection, except that {@code null} values
     * are omitted.
     */
    @Override public void delete(Collection<K> keys) {
        String sql = formatDeleteKey();

        try (PreparedStatement statement = prepareStatement(sql)) {
            for (K key : keys) {
                setKey(statement, key);
                statement.addBatch();
            }

            statement.executeBatch();
            commit();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Inserts a record into this table.
     *
     * @param record the record to insert.
     *
     * @return {@code true} iff the insert succeeded.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public boolean insert(V record) {
        int result = 0;

        try (PreparedStatement statement = prepareInsert()) {
            setRecord(statement, record);
            result = statement.executeUpdate();
            commit();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }

        return result == 1;
    }

    /**
     * Inserts records into this table.
     *
     * @param records the records to insert.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void insert(Collection<V> records) {
        try (PreparedStatement statement = prepareInsert()) {
            for (V record : records) {
                setRecord(statement, record);
                statement.addBatch();
            }

            JamLogger.info("Executing batch insert...");
            statement.executeBatch();
            commit();
            JamLogger.info("Committed batch insert.");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Selects all rows from this table.
     *
     * @return a list containing every row in this table.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public List<V> select() {
        String sql = formatSelectAll();

        try (Statement statement = createStatement()) {
            return getRecords(statement.executeQuery(sql));
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Returns the record indexed by a given key.
     *
     * @param key the key of the record to select.
     *
     * @return the record with the specified key (or
     * {@code null} if there is no matching record).
     */
    @Override public V select(K key) {
        try (PreparedStatement statement = prepareSelectKey()) {
            setKey(statement, key);
            return getSingle(statement.executeQuery());
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Returns the records indexed by collection of keys.
     *
     * @param keys the keys of the records to select.
     *
     * @return a list containing the records matching the specified key.
     * The matching records are returned in the same order as their keys
     * appear in the input collection, except that {@code null} values
     * are omitted.
     */
    @Override public List<V> select(Collection<K> keys) {
        List<V> records = new ArrayList<V>(keys.size());

        try (PreparedStatement statement = prepareSelectKey()) {
            for (K key : keys) {
                setKey(statement, key);
                V record = getSingle(statement.executeQuery());

                if (record != null)
                    records.add(record);
            }
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }

        return records;
    }
}
