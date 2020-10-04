
package jam.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jam.collect.TestRecord;

import org.junit.*;
import static org.junit.Assert.*;

final class TestTable extends SQLTable<String, TestRecord> {
    TestTable(SQLDb db, String name) {
        super(db, schema(name));
    }

    static SQLColumn KEY_COLUMN  = SQLType.TEXT.column("key").primaryKey();
    static SQLColumn IVAL_COLUMN = SQLType.INTEGER.column("ival");
    static SQLColumn DVAL_COLUMN = SQLType.DOUBLE.column("dval");
    static SQLColumn DATE_COLUMN = SQLType.DATE.column("date");
    static SQLColumn TIME_COLUMN = SQLType.TIME_STAMP.column("time");

    static SQLSchema schema(String name) {
        return SQLSchema.create(name,
                                KEY_COLUMN,
                                IVAL_COLUMN,
                                DVAL_COLUMN,
                                DATE_COLUMN,
                                TIME_COLUMN);
    }

    @Override public String getKey(TestRecord record) {
        return record.key;
    }

    @Override public TestRecord getRecord(ResultSet resultSet) throws SQLException {
        String key = getString(resultSet, 1);
        int    ival = getInt(resultSet, 2, 0);
        double dval = getDouble(resultSet, 3);
        LocalDate date = getDate(resultSet, 4);
        LocalDateTime time = getTimeStamp(resultSet, 5);

        return new TestRecord(key, ival, dval, date, time);
    }

    @Override public void setKey(PreparedStatement statement, String key, int index) throws SQLException {
        KEY_COLUMN.set(statement, index, key);
    }

    @Override public void setData(PreparedStatement statement, TestRecord record, int index) throws SQLException {
        IVAL_COLUMN.set(statement, index,     record.ival);
        DVAL_COLUMN.set(statement, index + 1, record.dval);
        DATE_COLUMN.set(statement, index + 2, record.date);
        TIME_COLUMN.set(statement, index + 3, record.time);
    }
}
