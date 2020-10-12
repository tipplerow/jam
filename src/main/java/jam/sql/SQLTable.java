
package jam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.app.JamLogger;
import jam.collect.JamLists;
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
    /**
     * The manager for the database containing this table.
     */
    protected final SQLDb db;

    /**
     * The schema that defines the database table structure.
     */
    protected final SQLSchema schema;

    /**
     * The open database connection <em>with auto-commit mode
     * disabled</em>.
     */
    protected final Connection connection;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     *
     * @param schema the schema that defines the table structure.
     *
     * @throws RuntimeException unless (1) the schema defines either a
     * primary or composite key, (2) a connection to the database can
     * be established, and (3) the database table can be created if it
     * does not already exist.
     */
    protected SQLTable(SQLDb db, SQLSchema schema) {
        this.db = db;
        this.schema = schema;
        this.connection = openConnection();
    }

    private static final String FIELD_SEPARATOR = ", ";

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

    private Set<K> executeSelectAllKeys(Statement statement) throws SQLException {
        String sql = formatSelectAllKeys();

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            Set<K> keys = new HashSet<K>();

            while (resultSet.next())
                keys.add(getKey(resultSet));

            return keys;
        }
    }

    private List<V> executeSelectAllRecords(Statement statement) throws SQLException {
        String sql = formatSelectAllRecords();

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

    private V executeSelectRecordByKey(PreparedStatement statement, K key) throws SQLException {
        setKey(statement, key, 1);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next())
                return getRecord(resultSet);
            else
                return null;
        }
    }

    private String formatContainsKey() {
        return formatSelect("COUNT(*)", formatWhereKeyParameters());
    }

    private String formatDeleteAll() {
        return String.format("DELETE FROM %s", schema.getTableName());
    }

    private String formatDeleteKey() {
        return String.format("DELETE FROM %s WHERE %s", schema.getTableName(), formatWhereKeyParameters());
    }

    private String formatInsert() {
        return String.format("INSERT INTO %s VALUES(%s)", schema.getTableName(), formatInsertParameters());
    }

    private String formatInsertParameters() {
        LineBuilder builder = new LineBuilder(FIELD_SEPARATOR);

        for (SQLColumn column : schema.getColumns())
            if (column.isSerial())
                builder.append("DEFAULT");
            else
                builder.append("?");

        return builder.toString();
    }

    private String formatSelect(String what) {
        return String.format("SELECT %s FROM %s", what, schema.getTableName());
    }

    private String formatSelect(String what, String where) {
        return String.format("SELECT %s FROM %s WHERE %s", what, schema.getTableName(), where);
    }

    private String formatSelectAllKeys() {
        return formatSelect(SQLColumn.join(schema.getKeyColumns(), FIELD_SEPARATOR));
    }

    private String formatSelectAllRecords() {
        return formatSelect("*");
    }

    private String formatSelectCount() {
        return formatSelect("COUNT(*)");
    }

    private String formatSelectRecordByKey() {
        return formatSelect("*", formatWhereKeyParameters());
    }

    private String formatWhereKeyParameters() {
        return formatWhereParameters(schema.getKeyColumns());
    }

    private static String formatWhereParameters(List<SQLColumn> columns) {
        if (columns.isEmpty())
            throw JamException.runtime("No columns for a WHERE clause.");

        LineBuilder builder = new LineBuilder(" AND ");

        for (SQLColumn column : columns)
            builder.append(String.format("%s = ?", column.getName()));

        return builder.toString();
    }

    private String formatUpdate() {
        return String.format("UPDATE %s SET %s WHERE %s",
                             schema.getTableName(),
                             formatSetParameters(schema.getDataColumns()),
                             formatWhereParameters(schema.getKeyColumns()));
    }

    private String formatSetParameters(List<SQLColumn> columns) {
        if (columns.isEmpty())
            throw JamException.runtime("No columns for a SET clause.");

        LineBuilder builder = new LineBuilder(FIELD_SEPARATOR);

        for (SQLColumn column : columns)
            builder.append(String.format("%s = ?", column.getName()));

        return builder.toString();
    }

    private Connection openConnection() {
        try {
            JamLogger.info("Opening connection for table [%s]...", schema.getTableName());

            Connection connection = db.openConnection();
            connection.setAutoCommit(false);

            // Create the database table unless it already exists...
            db.createTable(schema);

            return connection;
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
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

    private PreparedStatement prepareSelectRecordByKey() throws SQLException {
        return prepareStatement(formatSelectRecordByKey());
    }

    private PreparedStatement prepareUpdate() throws SQLException {
        return prepareStatement(formatUpdate());
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
     * Commits any pending transactions on the database connection
     * for this table.
     *
     * @throws SQLException if the commit is not successful.
     */
    protected void commit() throws SQLException {
        if (!connection.getAutoCommit())
            connection.commit();
    }

    /**
     * Creates a new statement using the common database connection
     * for this table.
     *
     * @return the new SQL statement.
     *
     * @throws SQLException if the statement cannot be created.
     */
    protected Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    /**
     * Creates a new prepared statement using the database connection
     * for this table.
     *
     * @param sql the paramaterized SQL statement.
     *
     * @return a new prepared statement with the specified SQL text.
     *
     * @throws SQLException if the statement cannot be created.
     */
    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Undoes all changes made in the current transaction and releases
     * any database locks held by the connection for this table.
     *
     * @throws SQLException if any errors occur.
     */
    protected void rollback(Connection connection) throws SQLException {
        JamLogger.warn("Rolling back transaction...");

        try {
            connection.rollback();
        }
        catch (SQLException ex) {
            JamLogger.warn("Failed to rollback update transaction!");
            JamLogger.warn(ex);
        }
    }

    /**
     * Extracts the key field from a record.
     *
     * @param record a record to be deleted or stored in this table.
     *
     * @return the key field for the given record.
     */
    public abstract K getKey(V record);

    /**
     * Returns the key stored in the current row of a result set
     * produced by a {@code SELECT} query.
     *
     * @param resultSet an active result set.
     *
     * @return the key stored in the current row of the result set.
     *
     * @throws SQLException if a database error occurs.
     */
    public abstract K getKey(ResultSet resultSet) throws SQLException;

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
     * Closes the database connection held by this table.  The
     * connection cannot be reopened after closing.
     *
     * @throws RuntimeException if the connection cannot be closed.
     */
    public void close() {
        try {
            JamLogger.info("Closing connection for table [%s]...", schema.getTableName());
            connection.close();
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    /**
     * Drops this table from the database.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void drop() {
        close();
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
     * @param keys the key to search for.
     *
     * @return a list where element {@code k} is {@code true} iff
     * this table contains a record with key {@code keys.get(k)}.
     */
    public List<Boolean> contains(List<K> keys) {
        try (PreparedStatement statement = prepareContainsKey()) {
            return executeContainsKeys(statement, keys);
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

    @Override public boolean contains(K key) {
        try (PreparedStatement statement = prepareContainsKey()) {
            return executeContainsKey(statement, key);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public int count() {
        try (Statement statement = createStatement()) {
            return executeSelectCount(statement);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

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
     * Deletes a record from this table.
     *
     * @param record the record to delete.
     *
     * @return {@code true} iff the record was deleted.
     */
    @Override public boolean delete(V record) {
        int result = 0;

        try (PreparedStatement statement = prepareDeleteKey()) {
            setKey(statement, getKey(record), 1);
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
     * Deletes records from this table.
     *
     * @param records the records to delete.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    @Override public void delete(Iterable<V> records) {
        try (PreparedStatement statement = prepareDeleteKey()) {
            for (V record : records) {
                setKey(statement, getKey(record), 1);
                statement.addBatch();
            }

            JamLogger.info("Executing batch delete...");
            statement.executeBatch();
            commit();
            JamLogger.info("Committed!");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

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

    @Override public void insert(Iterable<V> records) {
        try (PreparedStatement statement = prepareInsert()) {
            for (V record : records) {
                setInsert(statement, record);
                statement.addBatch();
            }

            JamLogger.info("Executing batch insert...");
            statement.executeBatch();
            commit();
            JamLogger.info("Committed!");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public Set<K> keys() {
        try (Statement statement = createStatement()) {
            return executeSelectAllKeys(statement);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public List<V> select() {
        try (Statement statement = createStatement()) {
            return executeSelectAllRecords(statement);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public V select(K key) {
        try (PreparedStatement statement = prepareSelectRecordByKey()) {
            return executeSelectRecordByKey(statement, key);
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public List<V> select(Collection<K> keys) {
        try (PreparedStatement statement = prepareSelectRecordByKey()) {
            List<V> records = new ArrayList<V>(keys.size());

            for (K key : keys) {
                V record = executeSelectRecordByKey(statement, key);

                if (record != null)
                    records.add(record);
            }

            return records;
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

    @Override public void store(V record) {
        if (contains(getKey(record)))
            update(record);
        else
            insert(record);
    }

    @Override public void store(Iterable<V> records) {
        //
        // More efficient to process the records in batch
        // statements...
        //
        List<V> recList = JamLists.arrayList(records);
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
            JamLogger.info("Committed!");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }

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

    @Override public void update(Iterable<V> records) {
        try (PreparedStatement statement = prepareUpdate()) {
            for (V record : records) {
                setUpdate(statement, record);
                statement.addBatch();
            }

            JamLogger.info("Executing batch update...");
            statement.executeBatch();
            commit();
            JamLogger.info("Committed!");
        }
        catch (SQLException ex) {
            throw runtimeEx(ex);
        }
    }
}
