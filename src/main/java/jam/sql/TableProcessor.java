
package jam.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jam.lang.DomainDouble;
import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.util.EnumUtil;

/**
 * Provides helper methods to extract objects from result sets and
 * assign objects to prepared statements.
 */
public interface TableProcessor {
    /**
     * Retrieves a {@code LocalDate} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnIndex the column index (the first is 1, second is 2, ...).
     *
     * @return the {@code LocalDate} value in the specified column
     * of the result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public default LocalDate getDate(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex, LocalDate.class);
    }

    /**
     * Retrieves a {@code LocalDate} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnLabel the column label.
     *
     * @return the {@code LocalDate} value in the specified column
     * of the result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public default LocalDate getDate(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getObject(columnLabel, LocalDate.class);
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
    public default double getDouble(ResultSet resultSet, int columnIndex) throws SQLException {
        Object obj = resultSet.getObject(columnIndex);

        if (obj != null)
            return ((Double) obj).doubleValue();
        else
            return Double.NaN;
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
    public default double getDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        return getDouble(resultSet, resultSet.findColumn(columnLabel));
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
    public default <E extends Enum<E>> E getEnum(ResultSet resultSet,
                                                 Class<E>  enumClass,
                                                 String    columnLabel) throws SQLException {
        return EnumUtil.valueOf(enumClass, getString(resultSet, columnLabel));
    }

    /**
     * Retrieves a {@code String} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnIndex the column index (the first is 1, second is 2, ...).
     *
     * @return the {@code String} value in the specified column of the
     * result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public default String getString(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
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
    public default String getString(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    /**
     * Retrieves a {@code LocalDateTime} value from a result set.
     *
     * @param resultSet an open result set.
     *
     * @param columnIndex the column index (the first is 1, second is 2, ...).
     *
     * @return the {@code LocalDateTime} value in the specified column
     * of the result set.
     *
     * @throws SQLException if the column label is not valid; if a
     * database error occurs; or if called on a closed result set.
     */
    public default LocalDateTime getTimeStamp(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex, LocalDateTime.class);
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
    public default LocalDateTime getTimeStamp(ResultSet resultSet, String columnLabel) throws SQLException {
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
    public default RuntimeException invalidColumn(String badColumn) {
        return JamException.runtime("Invalid column: [%s].", badColumn);
    }

    /**
     * Wraps a {@code SQLException} in a {@code RuntimeException}.
     *
     * @param ex the {@code SQLException} to wrap.
     *
     * @return the {@code RuntimeException} with the specified
     * {@code SQLException} as the cause.
     */
    public default RuntimeException runtimeEx(SQLException ex) {
        return JamException.runtime(ex);
    }

    /**
     * Assigns a possibly {@code null} date as a parameter in a
     * prepared statement.
     *
     * @param statement the prepared statement to populate.
     *
     * @param index the index of the parameter in the statement (the
     * first is 1, second is 2, ...).
     *
     * @param value the possibly {@code null} date to assign.
     *
     * @throws SQLException if the statement is closed, the index is
     * invalid, or if a database error occurs.
     */
    public default void setDate(PreparedStatement statement, int index, LocalDate value) throws SQLException {
        if (value != null)
            statement.setObject(index, value);
        else
            statement.setNull(index, Types.OTHER);
    }

    /**
     * Assigns a {@code DomainDouble} value to a parameter in a
     * prepared statement.
     *
     * @param statement an open prepared statement.
     *
     * @param index the (1-based) index of the parameter to assign.
     *
     * @param value the (possibly {@code null}) value to assign.
     *
     * @throws SQLException if any errors occur.
     */
    public default void setDomainDouble(PreparedStatement statement, int index, DomainDouble value) throws SQLException {
        if (value != null)
            statement.setDouble(index, value.doubleValue());
        else
            statement.setNull(index, Types.DOUBLE);
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
    public default <E extends Enum<E>> void setEnum(PreparedStatement statement, int index, E value) throws SQLException {
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
    public default void setKeyedObject(PreparedStatement statement, int index, KeyedObject<String> object) throws SQLException {
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
    public default void setString(PreparedStatement statement, int index, String value) throws SQLException {
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
    public default void setTimeStamp(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value != null)
            statement.setObject(index, value);
        else
            statement.setNull(index, Types.OTHER);
    }
}
