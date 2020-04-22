
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.report.LineBuilder;

/**
 * Manages a persistent database table of records.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLTable<K, V> {
    private Connection connection = null;

    private PreparedStatement fetchStatement = null;
    private PreparedStatement storeStatement = null;
    private PreparedStatement removeStatement = null;
    private PreparedStatement updateStatement = null;
    private PreparedStatement containsStatement = null;

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

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = db.openConnection();
            connection.setAutoCommit(false);
        }

        return connection;
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
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
     * Returns the column meta-data (ordered from left to right).
     *
     * <p>The key must be in the first (left-most) column.
     *
     * @return the column meta-data (ordered from left to right).
     */
    public abstract List<SQLColumn> getColumns();

    /**
     * Returns the key for a given record.
     *
     * @param record a record to be indexed.
     *
     * @return the key for the specified record.
     */
    public abstract K getKey(V record);

    /**
     * Returns the record stored in the current row of the result set
     * returned from a {@code SELECT} query.
     *
     * @param resultSet an active {@code SELECT} result set.
     *
     * @return the record stored in the current row of the result set.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract V getRow(ResultSet resultSet) throws SQLException;

    /**
     * Returns the table schema.
     *
     * @return the table schema.
     */
    public SQLSchema getSchema() {
        return SQLSchema.create(getTableName(), getColumns());
    }

    /**
     * Returns the name of the database table.
     *
     * @return the name of the database table.
     */
    public abstract String getTableName();

    /**
     * Assigns a record field to a parameter in a prepared statement.
     *
     * @param statement an open prepared statement.
     *
     * @param index the index of the parameter in the statement.
     *
     * @param record the record to be assigned.
     *
     * @param colName the name of the column to be assigned.
     *
     * @throws SQLException if the assignment fails.
     */
    public abstract void prepareColumn(PreparedStatement statement, int index, V record, String colName) throws SQLException;

    /**
     * Assigns the record key to a prepared statement.
     *
     * @param statement an open prepared statement.
     *
     * @param index the parameter index of the key in the statement.
     *
     * @param key the key of the record to assign.
     *
     * @throws SQLException if the assignment fails.
     */
    public abstract void prepareKey(PreparedStatement statement, int index, K key) throws SQLException;

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
    public static void setKeyedObject(PreparedStatement statement, int index,
                                      KeyedObject<String> object) throws SQLException {
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
     * Checks for the existence of a record key.
     *
     * @param key the key of interest.
     *
     * @return {@code true} iff this table contains a record with the
     * specfied key.
     *
     * @throws RuntimeException unless the table exists.
     */
    public synchronized boolean contains(K key) {
        try {
            return contains(getContainsStatement(), key);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getContainsStatement() throws SQLException {
        if (containsStatement == null)
            containsStatement = prepareStatement(formatContainsQuery());

        return containsStatement;
    }

    private String formatContainsQuery() {
        return String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", getTableName(), getKeyName());
    }

    private boolean contains(PreparedStatement statement, K key) throws SQLException {
        prepareKey(statement, 1, key);

        try (ResultSet resultSet = statement.executeQuery()) {
            return contains(resultSet);
        }
    }

    private boolean contains(ResultSet resultSet) {
        return db.getCount(resultSet) > 0;
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
     * Retrieves a record by its key.
     *
     * @param key the key of interest.
     *
     * @return the record with the specified key, or {@code null} if
     * there is no matching key in this table.
     */
    public V fetch(K key) {
        return fetch(List.of(key)).get(0);
    }

    /**
     * Retrieves records by their keys.
     *
     * @param keys the keys of interest.
     *
     * @return a list of records with the specified keys, in the order
     * returned by the collection iterator.  If any keys are not found,
     * the corresponding elements will be {@code null}.
     */
    public List<V> fetch(Collection<K> keys) {
        if (keys.isEmpty())
            return List.of();

        try {
            return fetch(getFetchStatement(), keys);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private synchronized PreparedStatement getFetchStatement() throws SQLException {
        if (fetchStatement == null)
            fetchStatement = prepareStatement(formatFetchQuery());

        return fetchStatement;
    }

    private String formatFetchQuery() {
        return String.format("SELECT * FROM %s WHERE %s = ?", getTableName(), getKeyName());
    }

    private List<V> fetch(PreparedStatement statement, Collection<K> keys) throws SQLException {
        List<V> records = new ArrayList<V>(keys.size());

        for (K key : keys)
            records.add(fetch(statement, key));

        return records;
    }

    private V fetch(PreparedStatement statement, K key) throws SQLException {
        prepareKey(statement, 1, key);

        try (ResultSet resultSet = statement.executeQuery()) {
            return fetch(resultSet);
        }
    }

    private V fetch(ResultSet resultSet) throws SQLException {
        if (resultSet.next())
            return getRow(resultSet);
        else
            return null;
    }

    /**
     * Returns the name of an indexed column <em>with the first column
     * having index 1</em>.
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
     * Returns the name of the key column.
     *
     * @return the name of the key column.
     */
    public String getKeyName() {
        return getColumns().get(0).getName();
    }

    /**
     * Loads all rows in the database table.
     *
     * @return all rows contained in the database table.
     *
     * @throws RuntimeException if any errors occur.
     */
    public synchronized Map<K, V> load() {
        JamLogger.info("Loading table [%s]...", getTableName());
        Map<K, V> records = null;

        try {
            records = load(getConnection());
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        JamLogger.info("Loaded [%d] records from table [%s]...", records.size(), getTableName());
        return records;
    }

    private Map<K, V> load(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return load(statement);
        }
    }

    private Map<K, V> load(Statement statement) throws SQLException {
        String queryString = formatLoadQuery();

        try (ResultSet resultSet = db.executeQuery(statement, queryString)) {
            return load(resultSet);
        }
    }

    private String formatLoadQuery() {
        return String.format("SELECT %s FROM %s", String.join(", ", getColumnNames()), getTableName());
    }

    private Map<K, V> load(ResultSet resultSet) throws SQLException {
        Map<K, V> records = new HashMap<K, V>();

        while (resultSet.next()) {
            V record = getRow(resultSet);
            records.put(getKey(record), record);
        }

        return records;
    }

    /**
     * Removes a record from this table (if it is present); a no-op if
     * the key is not present.
     *
     * @param key the key of the record to remove.
     */
    public void remove(K key) {
        remove(List.of(key));
    }

    /**
     * Removes records from this table.
     *
     * @param keys the keys of the records to remove.
     */
    public void remove(Collection<K> keys) {
        if (keys.isEmpty())
            return;

        try {
            remove(getRemoveStatement(), keys);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private synchronized PreparedStatement getRemoveStatement() throws SQLException {
        if (removeStatement == null)
            removeStatement = prepareStatement(formatDeleteStatement());

        return removeStatement;
    }

    private String formatDeleteStatement() {
        return String.format("DELETE FROM %s WHERE %s = ?", getTableName(), getKeyName());
    }

    private synchronized void remove(PreparedStatement statement, Collection<K> keys) throws SQLException {
        Connection connection = statement.getConnection();

        try {
            for (K key : keys)
                remove(statement, key);

            connection.commit();
        }
        catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void remove(PreparedStatement statement, K key) throws SQLException {
        prepareKey(statement, 1, key);
        statement.executeUpdate();
    }

    private void rollback(Connection connection) throws SQLException {
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
     * Creates this table in the database unless it already exists.
     *
     * @throws RuntimeException if the table does not already exist
     * and cannot be created.
     */
    public synchronized void require() {
        getSchema().createTable(db);
    }

    /**
     * Stores a new unique record in the database table.
     *
     * @param record the record to store, which must have a unique
     * primary keys
     *
     * @throws RuntimeException if the record has a duplicate key or
     * if any SQL errors occur.
     */
    public void store(V record) {
        store(List.of(record));
    }

    /**
     * Stores new unique records in the database table.
     *
     * @param records the records to store, which must have unique
     * primary keys.
     *
     * @throws RuntimeException if any records have duplicate keys or
     * if any SQL errors occur.
     */
    public void store(Collection<V> records) {
        if (records.isEmpty())
            return;

        try {
            store(getStoreStatement(), records);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private synchronized PreparedStatement getStoreStatement() throws SQLException {
        if (storeStatement == null)
            storeStatement = prepareStatement(formatInsertCommand());

        return storeStatement;
    }

    private String formatInsertCommand() {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ");
        builder.append(getTableName());
        builder.append(" VALUES(?");

        for (int k = 1; k < countColumns(); ++k)
            builder.append(", ?");

        builder.append(")");
        return builder.toString();
    }

    private synchronized void store(PreparedStatement statement, Collection<V> records) throws SQLException {
        Connection connection = statement.getConnection();

        try {
            for (V record : records)
                store(statement, record);

            connection.commit();
        }
        catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void store(PreparedStatement statement, V record) throws SQLException {
        prepareInsert(statement, record);
        statement.executeUpdate();
    }

    private void prepareInsert(PreparedStatement statement, V record) throws SQLException {
        for (int index = 1; index <= countColumns(); ++index)
            prepareColumn(statement, index, record, getColumnName(index));
    }

    /**
     * Updates a record in the database table (if it exists).
     *
     * @param record the record to update.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void update(V record) {
        update(List.of(record));
    }

    /**
     * Updates records in the database table.
     *
     * @param records the records to update.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void update(Collection<V> records) {
        if (records.isEmpty())
            return;

        try {
            update(getUpdateStatement(), records);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private synchronized PreparedStatement getUpdateStatement() throws SQLException {
        if (updateStatement == null)
            updateStatement = prepareStatement(formatUpdateCommand());

        return updateStatement;
    }

    private String formatUpdateCommand() {
        //
        // Recall that the primary key must be the first column, and
        // there must be at least two columns...
        //
        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ");
        builder.append(getTableName());
        builder.append(" SET ");
        builder.append(getColumnName(2));
        builder.append(" = ?");

        for (int index = 3; index <= countColumns(); ++index) {
            builder.append(", ");
            builder.append(getColumnName(index));
            builder.append(" = ?");
        }

        builder.append(" WHERE ");
        builder.append(getKeyName());
        builder.append(" = ?");

        return builder.toString();
    }

    private synchronized void update(PreparedStatement statement, Collection<V> records) throws SQLException {
        Connection connection = statement.getConnection();

        try {
            for (V record : records)
                update(statement, record);

            connection.commit();
        }
        catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void update(PreparedStatement statement, V record) throws SQLException {
        prepareUpdate(statement, record);
        statement.executeUpdate();
    }

    private void prepareUpdate(PreparedStatement statement, V record) throws SQLException {
        for (int index = 2; index <= countColumns(); ++index)
            prepareColumn(statement, index - 1, record, getColumnName(index));

        prepareColumn(statement, countColumns(), record, getColumnName(1));
    }
}
