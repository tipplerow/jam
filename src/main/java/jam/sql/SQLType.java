
package jam.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jam.lang.DomainDouble;
import jam.lang.KeyedObject;

/**
 * Represents and operates on SQL data types.
 */
public abstract class SQLType {
    /**
     * The SQL {@code BOOLEAN} type.
     */
    public static final SQLType BOOLEAN = new BooleanType();

    /**
     * The SQL {@code DATE} type.
     */
    public static final SQLType DATE = new DateType();

    /**
     * The SQL {@code DOUBLE PRECISION} type.
     */
    public static final SQLType DOUBLE = new DoubleType();

    /**
     * The SQL {@code INTEGER} type.
     */
    public static final SQLType INTEGER = new IntegerType();

    /**
     * The {@code SERIAL} or {@code AUTO_INCREMENT} type.
     */
    public static final SQLType SERIAL = new SerialType();

    /**
     * The SQL {@code TIMESTAMP} type.
     */
    public static final SQLType TIME_STAMP = new TimeStampType();

    /**
     * Creates a SQL {@code VARCHAR} type with a maximum length.
     *
     * @param length the maximum length of the character field.
     *
     * @return a SQL {@code VARCHAR} type with the specified maximum
     * length.
     */
    public static final SQLType varChar(int length) {
        return new VarCharType(length);
    }

    /**
     * Returns the schema type string that a database engine uses
     * to represent this type.
     *
     * @param engine a database engine type.
     *
     * @return the schema type string that the specified database
     * engine uses to represent this type.
     */
    public abstract String schemaType(SQLEngine engine);

    /**
     * Assigns a value to a parameter in a prepared statement.
     *
     * @param statement the prepared statement to set.
     *
     * @param index the index of the parameter in the statement (the
     * first parameter is 1, the second is 2, ...).
     *
     * @param value the parameter value to assign.
     *
     * @throws SQLException if any SQL errors occur.
     *
     * @throws ClassCastException unless the parameter value has a
     * runtime type that is consistent with this type.
     */
    public abstract void set(PreparedStatement statement, int index, Object value) throws SQLException;

    /**
     * Returns the integer code defined in {@code java.sql.Types} that
     * best represents this type.
     *
     * @return the integer code defined in {@code java.sql.Types} that
     * best represents this type.
     */
    public abstract int typeCode();

    /**
     * Assigns a possibly non-finite value to a parameter in a
     * prepared statement; non-finite values are replaced with
     * SQL {@code NULL}.
     *
     * @param statement the prepared statement to set.
     *
     * @param index the index of the parameter in the statement (the
     * first parameter is 1, the second is 2, ...).
     *
     * @param value the parameter value to assign.
     *
     * @throws SQLException if any SQL errors occur.
     */
    protected void setDouble(PreparedStatement statement, int index, double value) throws SQLException {
        if (Double.isFinite(value))
            statement.setDouble(index, value);
        else
            statement.setNull(index, Types.DOUBLE);
    }

    /**
     * Assigns a possibly {@code null} value to a parameter in a
     * prepared statement.
     *
     * @param statement the prepared statement to set.
     *
     * @param index the index of the parameter in the statement (the
     * first parameter is 1, the second is 2, ...).
     *
     * @param value the parameter value to assign.
     *
     * @throws SQLException if any SQL errors occur.
     */
    protected void setObject(PreparedStatement statement, int index, Object value) throws SQLException {
        if (value != null)
            statement.setObject(index, value);
        else
            statement.setNull(index, typeCode());
    }

    private static final class BooleanType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "BOOLEAN";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                statement.setBoolean(index, ((Boolean) value).booleanValue());
            else
                statement.setNull(index, Types.BOOLEAN);
        }

        @Override public int typeCode() {
            return Types.BOOLEAN;
        }
    }

    private static final class DateType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "DATE";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            setObject(statement, index, value);
        }

        @Override public int typeCode() {
            return Types.OTHER;
        }
    }

    private static final class DomainDoubleType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "DOUBLE PRECISION";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                setDouble(statement, index, ((DomainDouble) value).doubleValue());
            else
                statement.setNull(index, Types.DOUBLE);
        }

        @Override public int typeCode() {
            return Types.DOUBLE;
        }
    }

    private static final class DoubleType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "DOUBLE PRECISION";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                setDouble(statement, index, ((Double) value).doubleValue());
            else
                statement.setNull(index, Types.DOUBLE);
        }

        @Override public int typeCode() {
            return Types.DOUBLE;
        }
    }

    private static final class IntegerType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "INTEGER";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                statement.setInt(index, ((Integer) value).intValue());
            else
                statement.setNull(index, Types.INTEGER);
        }

        @Override public int typeCode() {
            return Types.INTEGER;
        }
    }

    private static final class KeyedObjectType extends VarCharType {
        private KeyedObjectType(int length) {
            super(length);
        }

        @SuppressWarnings("unchecked")
        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                statement.setString(index, ((KeyedObject<String>) value).getKey());
            else
                statement.setNull(index, Types.VARCHAR);
        }
    }

    private static final class SerialType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            switch (engine) {
            case POSTGRES:
                return "SERIAL";

            default:
                return "AUTO_INCREMENT";
            }
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            throw new SQLException("Cannot set a serial field.");
        }

        @Override public int typeCode() {
            return Types.OTHER;
        }
    }

    private static class VarCharType extends SQLType {
        private final int length;

        private VarCharType(int length) {
            this.length = length;
        }

        @Override public String schemaType(SQLEngine engine) {
            return String.format("VARCHAR(%d)", length);
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value != null)
                statement.setString(index, (String) value);
            else
                statement.setNull(index, Types.VARCHAR);
        }

        @Override public int typeCode() {
            return Types.VARCHAR;
        }
    }

    private static final class TimeStampType extends SQLType {
        @Override public String schemaType(SQLEngine engine) {
            return "TIMESTAMP";
        }

        @Override public void set(PreparedStatement statement, int index, Object value) throws SQLException {
            setObject(statement, index, value);
        }

        @Override public int typeCode() {
            return Types.OTHER;
        }
    }
}
