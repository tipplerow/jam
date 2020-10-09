
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import jam.app.JamLogger;
import jam.collect.MapTable;
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
public abstract class SQLTable<K, V> implements MapTable<K, V>, TableProcessor {
    private Connection connection = null;

    /**
     * The manager for the database containing this table.
     */
    protected final SQLDb db;

    /**
     * The schema that defines the database table structure.
     */
    protected final SQLSchema schema;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     *
     * @param schema the schema that defines the table structure.
     *
     * @throws RuntimeException unless the table contains at least one
     * key column.
     */
    protected SQLTable(SQLDb db, SQLSchema schema) {
        this.db = db;
        this.schema = schema;
    }

    private void commit() throws SQLException {
        Connection connection = getConnection();
        
        if (!connection.getAutoCommit())
            connection.commit();
    }

    private Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    private boolean executeContainsKey(PreparedStatement statement, K key) throws SQLException {
        setKey(statement, key, 1);

        try (ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1) > 0;
        }
    }

    private List<Boolean> executeContainsKeys(PreparedStatement statement, Collection<K> keys) throws SQLException {
        List<Boolean> contains = new ArrayList<Boolean>(keys.size());

        for (K key : keys)
            contains.add(executeContainsKey(statement, key));

        return contains;
    }

    private <T> void executeBatch(PreparedStatement statement, Collection<T> collection, Consumer<? super T> setter) throws SQLException {
        for (T object : collection) {
            setter.accept(object);
            statement.addBatch();
        }

        statement.executeBatch();
        commit();
    }

    private List<V> executeSelectAll(Statement statement) throws SQLException {
        String sql = formatSelectAll();

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            List<V> records = new ArrayList<V>();

            while (resultSet.next())
                records.add(getRecord(resultSet));

            return records;
        }
    }

    private int executeSelectCount(Statement statement) throws SQLException {
        String sql = formatSelectCount();

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private V executeSelectKey(PreparedStatement statement, K key) throws SQLException {
        setKey(statement, key, 1);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
                return getRecord(resultSet);
            else
                return null;
        }
    }

    private String formatContainsKey() {
        return String.format("SELECT COUNT(*) FROM %s %s", schema.getTableName(), formatWhere(schema.getKeyColumns()));
    }

    private String formatDeleteAll() {
        return String.format("DELETE FROM %s", schema.getTableName());
    }

    private String formatDeleteKey() {
        return String.format("DELETE FROM %s %s", schema.getTableName(), formatWhere(schema.getKeyColumns()));
    }

    private String formatInsert() {
        return String.format("INSERT INTO %s VALUES(%s)", schema.getTableName(), formatInsertParameters());
    }

    private String formatInsertParameters() {
        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : schema.getColumns())
            if (column.isSerial())
                builder.append("DEFAULT");
            else
                builder.append("?");

        return builder.toString();
    }

    private String formatSelectAll() {
        return String.format("SELECT * FROM %s", schema.getTableName());
    }

    private String formatSelectCount() {
        return String.format("SELECT COUNT(*) FROM %s", schema.getTableName());
    }

    private String formatSelectKey() {
        return String.format("SELECT * FROM %s %s", schema.getTableName(), formatWhere(schema.getKeyColumns()));
    }

    private static String formatWhere(List<SQLColumn> columns) {
        if (columns.isEmpty())
            throw JamException.runtime("No columns for a WHERE clause.");

        LineBuilder builder = new LineBuilder(" AND ");

        for (SQLColumn column : columns)
            builder.append(String.format("%s = ?", column.getName()));

        return "WHERE " + builder.toString();
    }

    private String formatUpdate() {
        return String.format("UPDATE %s %s %s",
                             schema.getTableName(),
                             formatSet(schema.getDataColumns()),
                             formatWhere(schema.getKeyColumns()));
    }

    private String formatSet(List<SQLColumn> columns) {
        if (columns.isEmpty())
            throw JamException.runtime("No columns for a SET clause.");

        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : columns)
            builder.append(String.format("%s = ?", column.getName()));

        return "SET " + builder.toString();
    }

    private PreparedStatement prepareContainsKey() throws SQLException {
        return prepareStatement(formatContainsKey());
    }

    private PreparedStatement prepareDeleteKey() throws SQLException {
        return prepareStatement(formatDeleteKey());
    }

    private PreparedStatement prepareInsert() throws SQLException {
        return prepareStatement(formatInsert());
    }

    private PreparedStatement prepareSelectKey() throws SQLException {
        return prepareStatement(formatSelectKey());
    }

    private PreparedStatement prepareUpdate() throws SQLException {
        return prepareStatement(formatUpdate());
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    private void rollback(Connection connection) throws SQLException {
        JamLogger.warn("Rolling back transaction...");

        try {
            connection.rollback();
        }
        catch (SQLException ex) {
            JamLogger.warn("Failed to rollback update transaction!");
            JamLogger.warn(ex);
        }
    }

    private void setInsert(PreparedStatement statement, V record) throws SQLException {
        setKey(statement, getKey(record), 1);
        setData(statement, record, 1 + schema.countKeyColumns());
    }

    private void setUpdate(PreparedStatement statement, V record) throws SQLException {
        setData(statement, record, 1);
        setKey(statement, getKey(record), 1 + schema.countDataColumns());
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
            JamLogger.info("Opening connection for table [%s]...", schema.getTableName());
            db.createTable(schema); // Create the database table unless it already exists...
            connection = db.openConnection();
            connection.setAutoCommit(false);
        }

        return connection;
    }

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
     * Assigns a record key to a prepared statement (to be used to
     * select and delete matching records).
     *
     * <p>The key fields should be assigned to parameter positions
     * {@code index, index + 1, ..., index + N}, where {@code N} is
     * the number of key fields in the record (usually just 1, but
     * may be 2 or more for composite keys or join tables).
     *
     * @param statement a prepared statement with one parameter for
     * each key column in this table [in the order defined by the
     * method {@code SQLSchema::getKeyColumns()}].
     *
     * @param key the record key to assign to the statement.
     *
     * @param index the index of the first key field in the prepared
     * statement.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public abstract void setKey(PreparedStatement statement, K key, int index) throws SQLException;

    /**
     * Assigns each data field in a record to its corresponding column
     * in a prepared statement (to be used to insert or update the
     * record into this table).
     *
     * <p>The key fields should be assigned to parameter positions
     * {@code index, index + 1, ..., index + N}, where {@code N} is
     * the number of data fields in the record.
     *
     * @param statement a prepared statement with one parameter for
     * each data column in this table [in the order defined by the
     * method {@code SQLSchema::getDataColumns()}].
     *
     * @param record the record to assign to the statement.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public abstract void setData(PreparedStatement statement, V record, int index) throws SQLException;

    /**
     * Drops this table from the database.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void drop() {
        try {
            if (connection != null) {
                JamLogger.info("Closing connection for table [%s]...", schema.getTableName());
                connection.close();
                connection = null;
            }
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }

        db.dropTable(schema.getTableName());
    }

    /**
     * Returns the database that contains this table.
     *
     * @return the database that contains this table.
     */
    public SQLDb getDB() {
        return db;
    }

    /**
     * Returns the schema that defines the structure of this table.
     *
     * @return the schema that defines the structure of this table.
     */
    public SQLSchema getSchema() {
        return schema;
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param key the key to search for.
     *
     * @return {@code true} iff this table contains a record with the
     * specified key.
     */
    @Override public boolean contains(K key) {
        try (PreparedStatement statement = prepareContainsKey()) {
            return executeContainsKey(statement, key);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Identifies keys contained in this table.
     *
     * @param keys the key to search for.
     *
     * @return a list where element {@code k} is {@code true} iff
     * this table contains a record with key {@code keys.get(k)}.
     */
    @Override public List<Boolean> contains(List<K> keys) {
        try (PreparedStatement statement = prepareContainsKey()) {
            return executeContainsKeys(statement, keys);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Returns the number of rows in this table.
     *
     * @return the number of rows in this table.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public int count() {
        try (Statement statement = createStatement()) {
            return executeSelectCount(statement);
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

        try (PreparedStatement statement = prepareDeleteKey()) {
            setKey(statement, key, 1);
            result = statement.executeUpdate();
            commit();
        }
        catch (SQLException ex) {
            JamLogger.warn(ex);
            return false;
        }

        return (result == 1);
    }

    /**
     * Deletes the records indexed by a collection of keys.
     *
     * @param keys the keys of the records to delete.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void delete(Collection<K> keys) {
        try (PreparedStatement statement = prepareDeleteKey()) {
            for (K key : keys) {
                setKey(statement, key, 1);
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
     * Imports a collection of bulk records into this table by writing
     * the records to a flat file and executing a bulk copy.
     *
     * @param records the records to import.
     *
     * @throws RuntimeException if the bulk copy fails.
     */
    public void copy(Collection<? extends BulkRecord> records) {
        db.bulkCopy(schema.getTableName(), records);
    }

    /**
     * Copies rows from a delimited file (with no header line).
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
    public void copy(String fileName, char delimiter, String nullString) {
        db.bulkCopy(schema.getTableName(), fileName, delimiter, nullString);
    }

    /**
     * Inserts a record into this table.
     *
     * @param record the record to insert.
     *
     * @return {@code true} iff the insert succeeded.
     */
    @Override public boolean insert(V record) {
        int result = 0;

        try (PreparedStatement statement = prepareInsert()) {
            setInsert(statement, record);
            result = statement.executeUpdate();
            commit();
        }
        catch (SQLException ex) {
            JamLogger.warn(ex);
            return false;
        }

        return (result == 1);
    }

    /**
     * Inserts records into this table.
     *
     * @param records the records to insert.
     *
     * @throws RuntimeException if any inserts fail.
     */
    @Override public void insert(Collection<V> records) {
        try (PreparedStatement statement = prepareInsert()) {
            for (V record : records) {
                setInsert(statement, record);
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
        try (Statement statement = createStatement()) {
            return executeSelectAll(statement);
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
            return executeSelectKey(statement, key);
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
        try (PreparedStatement statement = prepareSelectKey()) {
            List<V> records = new ArrayList<V>(keys.size());

            for (K key : keys) {
                V record = executeSelectKey(statement, key);

                if (record != null)
                    records.add(record);
            }

            return records;
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Updates an existing record in this table; this operation will
     * fail unless this table already contains a record with the same
     * key.
     *
     * @param record the record to update.
     *
     * @return {@code true} iff the record was successfully updated.
     */
    @Override public boolean update(V record) {
        int result = 0;

        try (PreparedStatement statement = prepareUpdate()) {
            setUpdate(statement, record);
            result = statement.executeUpdate();
            commit();
        }
        catch (SQLException ex) {
            JamLogger.warn(ex);
            return false;
        }

        return (result == 1);
    }

    /**
     * Updates existing records in this table.
     *
     * @param records the records to update.
     *
     * @throw RuntimeException if any of the updates fail.
     */
    @Override public void update(Collection<V> records) {
        try (PreparedStatement statement = prepareUpdate()) {
            for (V record : records) {
                setUpdate(statement, record);
                statement.addBatch();
            }

            JamLogger.info("Executing batch update...");
            statement.executeBatch();
            commit();
            JamLogger.info("Committed batch update.");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Inserts a new record or updates an existing record in this
     * table; this operation should always succeed.
     *
     * @param record the record to insert or update.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void upsert(V record) {
        if (contains(getKey(record)))
            update(record);
        else
            insert(record);
    }

    /**
     * Inserts new records or updates existing records in this table.
     *
     * @param records the records to insert or update.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void upsert(Collection<V> records) {
        //
        // More efficient to process the records in batch
        // statements...
        //
        List<V> recList = new ArrayList<V>(records);
        List<K> keyList = JamStreams.apply(recList, rec -> getKey(rec));
        List<Boolean> contains = contains(keyList);

        try (PreparedStatement insertStatement = prepareInsert();
             PreparedStatement updateStatement = prepareUpdate()) {

            for (int recIndex = 0; recIndex < recList.size(); ++recIndex) {
                if (contains.get(recIndex)) {
                    setUpdate(updateStatement, recList.get(recIndex));
                    updateStatement.addBatch();
                }
                else {
                    setInsert(insertStatement, recList.get(recIndex));
                    insertStatement.addBatch();
                }
            }

            JamLogger.info("Executing batch insert/update...");
            insertStatement.executeBatch();
            updateStatement.executeBatch();
            commit();
            JamLogger.info("Committed batch insert/update.");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }
}
