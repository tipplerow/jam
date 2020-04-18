
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.lang.KeyedObject;

/**
 * Manages a persistent database table of <em>pair records</em>.
 *
 * <p>A pair record represents a many-to-many relationship.  It
 * contains two values, each of which may be a key for other data
 * items.  An article keyword list is a good example.  One article
 * contains multiple keywords, and one keyword appears in multiple
 * articles.  We might want to use either the article or the keyword
 * as the key in a search.
 *
 * @param <V> the concrete pair record type.
 *
 * @param <K1> the runtime type of the first key.
 *
 * @param <K2> the runtime type of the second key.
 */
public abstract class SQLPairTable<K1, K2, V extends SQLPairRecord<K1, K2>> {
    private Connection connection = null;

    private PreparedStatement storeStatement = null;
    private PreparedStatement fetchKey1Statement = null;
    private PreparedStatement fetchKey2Statement = null;
    private PreparedStatement containsBothStatement = null;
    private PreparedStatement containsKey1Statement = null;
    private PreparedStatement containsKey2Statement = null;

    /**
     * The manager for the database containing this table.
     */
    protected final SQLDb db;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     */
    protected SQLPairTable(SQLDb db) {
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
     * Returns the name of the key in the first column.
     *
     * @return the name of the key in the first column.
     */
    public abstract String getKey1Name();

    /**
     * Returns the data type of the first key.
     *
     * @return the data type of the first key.
     */
    public abstract String getKey1Type();

    /**
     * Returns the name of the key in the second column.
     *
     * @return the name of the key in the second column.
     */
    public abstract String getKey2Name();

    /**
     * Returns the data type of the second key.
     *
     * @return the data type of the second key.
     */
    public abstract String getKey2Type();

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
    public abstract V getRecord(ResultSet resultSet) throws SQLException;

    /**
     * Returns the name of the database table.
     *
     * @return the name of the database table.
     */
    public abstract String getTableName();

    /**
     * Assigns the first key to a prepared statement.
     *
     * @param statement an open prepared statement.
     *
     * @param index the location of the first key in the statement
     * (not necessarily first).
     *
     * @param key1 the key value to assign.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract void setKey1(PreparedStatement statement, int index, K1 key1) throws SQLException;

    /**
     * Assigns the second key to a prepared statement.
     *
     * @param statement an open prepared statement.
     *
     * @param index the location of the second key in the statement
     * (not necessarily second).
     *
     * @param key2 the key value to assign.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract void setKey2(PreparedStatement statement, int index, K2 key2) throws SQLException;

    /**
     * Determines whether this table exists in the database.
     *
     * @return {@code true} iff this table exists in the database.
     */
    public synchronized boolean exists() {
        return db.tableExists(getTableName());
    }

    /**
     * Returns the column meta-data for the first key.
     *
     * @return the column meta-data for the first key.
     */
    public SQLColumn getKey1Column() {
        return SQLColumn.create(getKey1Name(), getKey1Type()).notNull();
    }

    /**
     * Returns the column meta-data for the second key.
     *
     * @return the column meta-data for the second key.
     */
    public SQLColumn getKey2Column() {
        return SQLColumn.create(getKey2Name(), getKey2Type()).notNull();
    }

    /**
     * Returns the schema for the database table.
     *
     * @return the schema for the database table.
     */
    public String getTableSchema() {
        String key1 = getKey1Name();
        String key2 = getKey2Name();

        String column1 = getKey1Column().join();
        String column2 = getKey2Column().join();

        String primaryKey =
            String.format("CONSTRAINT %s_%s PRIMARY KEY(%s, %s)", key1, key2, key1, key2);

        return String.join(", ", column1, column2, primaryKey);
    }

    /**
     * Creates this table in the database unless it already exists.
     *
     * @throws RuntimeException if the table does not already exist
     * and cannot be created.
     */
    public synchronized void require() {
        if (!db.tableExists(getTableName())) {
            db.createTable(getTableName(), getTableSchema());
            db.createIndex(getTableName(), getKey1Name());
            db.createIndex(getTableName(), getKey2Name());
        }
    }

    /**
     * Checks for the existence of a many-to-many relationship.
     *
     * @param record the record to query.
     *
     * @return {@code true} iff this table contains the specified
     * record.
     *
     * @throws RuntimeException unless the table exists.
     */
    public synchronized boolean contains(V record) {
        return contains(record.getKey1(), record.getKey2());
    }

    /**
     * Checks for the existence of a many-to-many relationship.
     *
     * @param key1 the first key of interest.
     *
     * @param key2 the second key of interest.
     *
     * @return {@code true} iff this table contains a record with
     * the specfied keys.
     *
     * @throws RuntimeException unless the table exists.
     */
    public synchronized boolean contains(K1 key1, K2 key2) {
        try {
            return containsBoth(getContainsBothStatement(), key1, key2);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getContainsBothStatement() throws SQLException {
        if (containsBothStatement == null)
            containsBothStatement = prepareStatement(formatContainsBothQuery());

        return containsBothStatement;
    }

    private String formatContainsBothQuery() {
        return String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ?",
                             getTableName(), getKey1Name(), getKey2Name());
    }

    private boolean containsBoth(PreparedStatement statement, K1 key1, K2 key2) throws SQLException {
        setKey1(statement, 1, key1);
        setKey2(statement, 2, key2);

        try (ResultSet resultSet = statement.executeQuery()) {
            return contains(resultSet);
        }
    }

    private boolean contains(ResultSet resultSet) {
        return db.getCount(resultSet) > 0;
    }

    /**
     * Checks for the existence of the first key.
     *
     * @param key1 the first key of interest.
     *
     * @return {@code true} iff this table contains one or more
     * records with the specfied first key.
     *
     * @throws RuntimeException unless the table exists.
     */
    public synchronized boolean containsKey1(K1 key1) {
        try {
            return containsKey1(getContainsKey1Statement(), key1);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getContainsKey1Statement() throws SQLException {
        if (containsKey1Statement == null)
            containsKey1Statement = prepareStatement(formatContainsKey1Query());

        return containsKey1Statement;
    }

    private String formatContainsKey1Query() {
        return formatContainsOneQuery(getKey1Name());
    }

    private String formatContainsOneQuery(String keyName) {
        return String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", getTableName(), keyName);
    }

    private boolean containsKey1(PreparedStatement statement, K1 key1) throws SQLException {
        setKey1(statement, 1, key1);

        try (ResultSet resultSet = statement.executeQuery()) {
            return contains(resultSet);
        }
    }

    /**
     * Checks for the existence of the second key.
     *
     * @param key2 the second key of interest.
     *
     * @return {@code true} iff this table contains one or more
     * records with the specfied second key.
     *
     * @throws RuntimeException unless the table exists.
     */
    public synchronized boolean containsKey2(K2 key2) {
        try {
            return containsKey2(getContainsKey2Statement(), key2);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getContainsKey2Statement() throws SQLException {
        if (containsKey2Statement == null)
            containsKey2Statement = prepareStatement(formatContainsKey2Query());

        return containsKey2Statement;
    }

    private String formatContainsKey2Query() {
        return formatContainsOneQuery(getKey2Name());
    }

    private boolean containsKey2(PreparedStatement statement, K2 key2) throws SQLException {
        setKey2(statement, 1, key2);

        try (ResultSet resultSet = statement.executeQuery()) {
            return contains(resultSet);
        }
    }

    /**
     * Retrieves all many-to-many relationship containing the first
     * key.
     *
     * @param key1 the key of interest.
     *
     * @return all records containing the specified first key (empty
     * if there are no matching records).
     */
    public synchronized List<V> fetchKey1(K1 key1) {
        try {
            return fetchKey1(getFetchKey1Statement(), key1);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getFetchKey1Statement() throws SQLException {
        if (fetchKey1Statement == null)
            fetchKey1Statement = prepareStatement(formatFetchKey1Query());

        return fetchKey1Statement;
    }

    private String formatFetchKey1Query() {
        return formatFetchOneQuery(getKey1Name());
    }

    private String formatFetchOneQuery(String keyName) {
        return String.format("SELECT * FROM %s WHERE %s = ?", getTableName(), keyName);
    }

    private List<V> fetchKey1(PreparedStatement statement, K1 key1) throws SQLException {
        setKey1(statement, 1, key1);
        return fetch(statement);
    }

    private List<V> fetch(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return fetch(resultSet);
        }
    }

    private List<V> fetch(ResultSet resultSet) throws SQLException {
        List<V> records = new ArrayList<V>();

        while (resultSet.next())
            records.add(getRecord(resultSet));

        return records;
    }

    /**
     * Retrieves all many-to-many relationship containing the second
     * key.
     *
     * @param key2 the key of interest.
     *
     * @return all records containing the specified second key (empty
     * if there are no matching records).
     */
    public synchronized List<V> fetchKey2(K2 key2) {
        try {
            return fetchKey2(getFetchKey2Statement(), key2);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getFetchKey2Statement() throws SQLException {
        if (fetchKey2Statement == null)
            fetchKey2Statement = prepareStatement(formatFetchKey2Query());

        return fetchKey2Statement;
    }

    private String formatFetchKey2Query() {
        return formatFetchOneQuery(getKey2Name());
    }

    private List<V> fetchKey2(PreparedStatement statement, K2 key2) throws SQLException {
        setKey2(statement, 1, key2);
        return fetch(statement);
    }

    /**
     * Loads all rows in the database table.
     *
     * @return all rows contained in the database table.
     *
     * @throws RuntimeException if any errors occur.
     */
    public synchronized List<V> load() {
        try {
            return load(getConnection());
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private List<V> load(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return load(statement);
        }
    }

    private List<V> load(Statement statement) throws SQLException {
        String queryString = formatLoadQuery();

        try (ResultSet resultSet = db.executeQuery(statement, queryString)) {
            return fetch(resultSet);
        }
    }

    private String formatLoadQuery() {
        return String.format("SELECT * FROM %s", getTableName());
    }

    /**
     * Stores a new unique record in the database table.
     *
     * @param record the record to store, which must represent a new
     * many-to-many relationship (have a unique key pair).
     *
     * @throws RuntimeException if the record has a duplicate key pair
     * or if any SQL errors occur.
     */
    public void store(V record) {
        store(List.of(record));
    }

    /**
     * Stores new unique records in the database table.
     *
     * @param records the records to store, which must represent
     * new many-to-many relationships (have unique key pairs).
     *
     * @throws RuntimeException if any records have duplicate key
     * pairs or if any SQL errors occur.
     */
    public synchronized void store(Collection<V> records) {
        try {
            store(getStoreStatement(), records);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    private PreparedStatement getStoreStatement() throws SQLException {
        if (storeStatement == null)
            storeStatement = prepareStatement(formatInsertCommand());

        return storeStatement;
    }

    private String formatInsertCommand() {
        return String.format("INSERT INTO %s VALUES(?, ?)", getTableName());
    }

    private void store(PreparedStatement statement, Collection<V> records) throws SQLException {
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
        setKey1(statement, 1, record.getKey1());
        setKey2(statement, 2, record.getKey2());

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
