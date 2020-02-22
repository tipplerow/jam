
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Manages a persistent database table of records.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLTable<K, V> {
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
     * Returns the names of the table columns (ordered from left to right).
     *
     * <p>The key must be in the first (left-most) column.
     *
     * @return the names of the table columns (ordered from left to right).
     */
    public abstract List<String> getColumnNames();

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
     * Returns the name of the database table.
     *
     * @return the name of the database table.
     */
    public abstract String getTableName();

    /**
     * Returns the schema required to create the table.
     *
     * @return the schema required to create the table.
     */
    public abstract String getTableSchema();

    /**
     * Assigns the fields of a record to the parameters of a prepared
     * {@code INSERT} statement.
     *
     * <p>The prepared statement is created by the following SQL text:
     * {@code INSERT INTO table VALUES(?, ?, ..., ?)}, where {@code
     * table} is the name of this table and the number of parameters
     * (question marks) matches the number of columns.
     *
     * @param statement a prepared statement that has been created
     * using the SQL text above.
     *
     * @param record the record to be inserted into the table.
     *
     * @throws SQLException if any of the individual field assignments
     * fail.
     */
    public abstract void prepareInsertStatement(PreparedStatement statement, V record) throws SQLException;

    /**
     * Assigns the record key to a prepared {@code SELECT WHERE} statement.
     *
     * <p>The prepared statement is created by the following SQL text:
     * {@code SELECT * FROM table WHERE key = ?}, where {@code table}
     * is the name of this table and {@code key} is the name of the
     * key column.
     *
     * <p>This default implementation assumes that the table key is a
     * SQL string and that it is stored as {@code key.toString()}.
     *
     * @param statement a prepared statement that has been created
     * using the SQL text above.
     *
     * @param key the key of the record to be fetched.
     *
     * @throws SQLException if the assignment fails.
     */
    public void prepareSelectStatement(PreparedStatement statement, K key) throws SQLException {
        statement.setString(1, key.toString());
    }

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
    protected static double getDouble(ResultSet resultSet, int columnIndex) throws SQLException {
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
    protected static double getDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        return getDouble(resultSet.getObject(columnLabel));
    }

    private static double getDouble(Object obj) {
        if (obj != null)
            return ((Double) obj).doubleValue();
        else
            return Double.NaN;
    }

    /**
     * Checks for the existence of a record key.
     *
     * @param key the key of interest.
     *
     * @return {@code true} iff this table contains a record with the
     * specfied key.
     */
    public boolean contains(K key) {
        if (!exists())
            return false;

        try (Connection connection = db.openConnection()) {
            return contains(connection.prepareStatement(formatCountWhereStatement()), key);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private String formatCountWhereStatement() {
        return String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", getTableName(), getKeyName());
    }

    private boolean contains(PreparedStatement statement, K key) throws SQLException {
        prepareSelectStatement(statement, key);

        try (ResultSet resultSet = statement.executeQuery()) {
            return db.getCount(resultSet) > 0;
        }
    }

    /**
     * Creates this table in the database unless it already exists.
     *
     * @throws RuntimeException if the table cannot be created.
     */
    public synchronized void create() {
        db.createTable(getTableName(), getTableSchema());
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
        if (!exists())
            return null;

        try (Connection connection = db.openConnection()) {
            return fetch(connection.prepareStatement(formatSelectWhereStatement()), key);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private String formatSelectWhereStatement() {
        return String.format("SELECT * FROM %s WHERE %s = ?", getTableName(), getKeyName());
    }

    private V fetch(PreparedStatement statement, K key) throws SQLException {
        prepareSelectStatement(statement, key);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
                return getRow(resultSet);
            else
                return null;
        }
    }

    /**
     * Retrieve record by their keys.
     *
     * @param keys the keys of interest.
     *
     * @return a list of records with the specified keys, in the order
     * returned by the collection iterator.  If any keys are not found,
     * the corresponding elements will be {@code null}.
     */
    public List<V> fetch(Collection<K> keys) {
        if (!exists())
            return new ArrayList<V>();

        List<V> records = new ArrayList<V>(keys.size());

        try (Connection connection = db.openConnection()) {
            PreparedStatement statement = connection.prepareStatement(formatSelectWhereStatement());

            for (K key : keys)
                records.add(fetch(statement, key));

            statement.close();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        return records;
    }

    /**
     * Returns the name of the key column.
     *
     * @return the name of the key column.
     */
    public String getKeyName() {
        return getColumnNames().get(0);
    }

    /**
     * Loads all rows in the database table.
     *
     * @return all rows contained in the database table.
     *
     * @throws RuntimeException if any errors occur.
     */
    public synchronized Map<K, V> load() {
        if (!exists())
            return new HashMap<K, V>();

        try (Connection connection = db.openConnection(false)) {
            return load(connection.createStatement());
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private Map<K, V> load(Statement statement) {
        try (ResultSet resultSet = db.executeQuery(statement, selectAllQuery())) {
            return load(resultSet);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private String selectAllQuery() {
        return String.format("SELECT %s FROM %s", String.join(", ", getColumnNames()), getTableName());
    }

    private Map<K, V> load(ResultSet resultSet) {
        Map<K, V> records = new HashMap<K, V>();

        try {
            while (resultSet.next()) {
                V record = getRow(resultSet);
                records.put(getKey(record), record);
            }
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        return records;
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
    public synchronized void store(Collection<V> records) {
        if (!exists()) {
            JamLogger.info("Creating table [%s]...", getTableName());
            create();
        }

        JamLogger.info("Adding [%s] records to table [%s]...", records.size(), getTableName());

        try (Connection connection = db.openConnection(false)) {
            storeRecords(connection, records);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }

        JamLogger.info("Added [%s] records to table [%s].", records.size(), getTableName());
    }

    private void storeRecords(Connection connection, Collection<V> records) throws SQLException {
        String insertCommand = formatInsertCommand();
        JamLogger.info(insertCommand);

        try (PreparedStatement statement = connection.prepareStatement(insertCommand)) {
            storeRecords(statement, records);
        }
    }

    private String formatInsertCommand() {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ");
        builder.append(getTableName());
        builder.append(" VALUES(?");

        for (int k = 1; k < getColumnNames().size(); ++k)
            builder.append(", ?");

        builder.append(")");
        return builder.toString();
    }

    private void storeRecords(PreparedStatement statement, Collection<V> records) throws SQLException {
        Connection connection = statement.getConnection();

        try {
            for (V record : records)
                storeRecord(statement, record);

            connection.commit();
        }
        catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void storeRecord(PreparedStatement statement, V record) throws SQLException {
        prepareInsertStatement(statement, record);
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
}
