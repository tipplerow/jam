
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Manages a persistent database table of records with a single
 * primary key column.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLKeyTable<K, V> extends SQLTable<V> {
    private PreparedStatement fetchStatement = null;
    private PreparedStatement storeStatement = null;
    private PreparedStatement removeStatement = null;
    private PreparedStatement updateStatement = null;
    private PreparedStatement containsStatement = null;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     */
    protected SQLKeyTable(SQLDb db) {
        super(db);
    }

    /**
     * Returns the key for a given record.
     *
     * @param record a record to be indexed.
     *
     * @return the key for the specified record.
     */
    public abstract K getKey(V record);

    /**
     * Returns the key stored in the current row of the result set
     * returned from a {@code SELECT} query.
     *
     * @param resultSet an active {@code SELECT} result set.
     *
     * @param columnLabel the name of the column containing the key.
     *
     * @return the key stored in the specified column of the current
     * row of the result set.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract K getKey(ResultSet resultSet, String columnLabel) throws SQLException;

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
            return getRecord(resultSet);
        else
            return null;
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
            V record = getRecord(resultSet);
            records.put(getKey(record), record);
        }

        return records;
    }

    /**
     * Loads all keys from the database table.
     *
     * @return all keys contained in the database table.
     *
     * @throws RuntimeException if any errors occur.
     */
    public synchronized Set<K> loadKeys() {
        JamLogger.info("Loading table keys [%s]...", getTableName());
        Set<K> keys = null;

        try {
            keys = loadKeys(getConnection());
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }

        JamLogger.info("Loaded [%d] keys from table [%s]...", keys.size(), getTableName());
        return keys;
    }

    private Set<K> loadKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return loadKeys(statement);
        }
    }

    private Set<K> loadKeys(Statement statement) throws SQLException {
        String queryString = formatLoadKeysQuery();

        try (ResultSet resultSet = db.executeQuery(statement, queryString)) {
            return loadKeys(resultSet);
        }
    }

    private String formatLoadKeysQuery() {
        return String.format("SELECT %s FROM %s", getKeyName(), getTableName());
    }

    private Set<K> loadKeys(ResultSet resultSet) throws SQLException {
        Set<K> keys = new HashSet<K>();
        String label = getKeyName();

        while (resultSet.next())
            keys.add(getKey(resultSet, label));

        return keys;
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
