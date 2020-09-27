
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
import jam.lang.JamException;

/**
 * Manages a persistent database table that uses an automatically
 * generated sequential (auto-increment or serial) primary key.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLSerialTable<V> extends SQLTable {
    private PreparedStatement insertStatement = null;

    /**
     * Creates a new table with a fixed database manager.
     *
     * @param db the manager for the database containing the table.
     */
    protected SQLSerialTable(PostgreSQLDb db) {
        super(db);
    }

    /**
     * The name of the serial primary key column.
     */
    public static final String KEY_NAME = "serial_key";

    /**
     * The meta-data for the serial primary key column.
     */
    public static final SQLColumn KEY_COLUMN = SQLColumn.serial(KEY_NAME);

    /**
     * Returns the column meta-data for the data columns (excluding
     * the serial primary key column, ordered from left to right).
     *
     * @return the column meta-data for the data columns.
     */
    public abstract List<SQLColumn> getDataColumns();

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
     * Returns the remaining records stored in a result set returned
     * from a {@code SELECT} query.
     *
     * @param resultSet an active {@code SELECT} result set.
     *
     * @return the remaining records stored in the result set.
     *
     * @throws SQLException if a database error occurs.
     */
    public List<V> getRows(ResultSet resultSet) throws SQLException {
        List<V> rows = new ArrayList<V>();

        while (resultSet.next())
            rows.add(getRow(resultSet));

        return rows;
    }

    /**
     * Inserts a record into the database table.
     *
     * @param record the record to insert.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void insert(V record) {
        insert(List.of(record));
    }

    /**
     * Inserts records into the database table.
     *
     * @param records the records to insert.
     *
     * @throws RuntimeException if any SQL errors occur.
     */
    public void insert(Collection<V> records) {
        if (records.isEmpty())
            return;

        try {
            insert(getInsertStatement(), records);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Returns all of the records contained in this table.
     *
     * @return all of the records contained in this table.
     */
    public List<V> select() throws SQLException {
        QueryResult result = selectAll();

        try {
            return getRows(result.getResultSet());
        }
        finally {
            result.close();
        }
    }

    /**
     * Returns all of the records contained in this table that match a
     * SQL query string.
     *
     * @param query a SQL query string for this table.
     *
     * @return all of the records contained in this table that match the
     * specified SQL query.
     *
     * @throws SQLException if any SQL errors occur.
     */
    public List<V> select(String query) throws SQLException {
        throw new SQLException();
    }

    private synchronized PreparedStatement getInsertStatement() throws SQLException {
        if (insertStatement == null)
            insertStatement = prepareStatement(formatInsertCommand());

        return insertStatement;
    }

    private String formatInsertCommand() {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ");
        builder.append(getTableName());
        builder.append(" VALUES(DEFAULT");

        for (int k = 2; k <= countColumns(); ++k)
            builder.append(", ?");

        builder.append(")");
        return builder.toString();
    }

    private synchronized void insert(PreparedStatement statement, Collection<V> records) throws SQLException {
        Connection connection = statement.getConnection();

        try {
            for (V record : records)
                insert(statement, record);

            connection.commit();
        }
        catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void insert(PreparedStatement statement, V record) throws SQLException {
        prepareInsert(statement, record);
        statement.executeUpdate();
    }

    private void prepareInsert(PreparedStatement statement, V record) throws SQLException {
        //
        // The first column is the serial primary key, populated with
        // the DEFAULT keyword...
        //
        for (int index = 2; index <= countColumns(); ++index)
            prepareColumn(statement, index, record, getColumnName(index));
    }

    @Override public List<SQLColumn> getColumns() {
        List<SQLColumn> columns = new ArrayList<SQLColumn>();

        columns.add(KEY_COLUMN);
        columns.addAll(getDataColumns());

        return columns;
    }
}
