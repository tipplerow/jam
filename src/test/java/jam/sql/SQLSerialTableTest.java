
package jam.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jam.lang.JamException;

import org.junit.*;
import static org.junit.Assert.*;

final class SQLSerialTestRecord {
    public String name;
    public int age;

    public SQLSerialTestRecord(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof SQLSerialTestRecord) && equalsRecord((SQLSerialTestRecord) obj);
    }

    private boolean equalsRecord(SQLSerialTestRecord that) {
        return this.name.equals(that.name) && this.age == that.age;
    }
}

final class SQLSerialTestTable extends SQLSerialTable<SQLSerialTestRecord> {
    private static SQLSerialTestTable instance;

    private SQLSerialTestTable() {
        super(PostgreSQLDb.test());
    }

    public static synchronized SQLSerialTestTable instance() {
        if (instance == null)
            instance = new SQLSerialTestTable();

        instance.require();
        return instance;
    }

    @Override public List<SQLColumn> getDataColumns() {
        return List.of(SQLColumn.create("name", "text").unique(),
                       SQLColumn.create("age", "integer"));
    }

    @Override public SQLSerialTestRecord getRow(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        int age = resultSet.getInt("age");

        return new SQLSerialTestRecord(name, age);
    }

    @Override public String getTableName() {
        return "serial_test";
    }

    @Override public void prepareColumn(PreparedStatement statement, int index, SQLSerialTestRecord record, String colName) throws SQLException {
        switch (colName) {
        case "name":
            statement.setString(1, record.name);
            break;

        case "age":
            statement.setInt(2, record.age);
            break;

        default:
            throw JamException.runtime("Invalid column name: [%s].", colName);
        }
    }
}

public class SQLSerialTableTest {
    @Test public void testAll() throws SQLException {
        SQLSerialTestTable table = SQLSerialTestTable.instance();
        table.clear();

        SQLSerialTestRecord rec1 = new SQLSerialTestRecord("Sam", 22);
        SQLSerialTestRecord rec2 = new SQLSerialTestRecord("Anne", 33);
        SQLSerialTestRecord rec3 = new SQLSerialTestRecord("John", 22);
        SQLSerialTestRecord rec4 = new SQLSerialTestRecord("Joan", 44);

        table.insert(rec1);
        assertEquals(List.of(rec1), table.select());

        table.insert(rec2);
        assertEquals(List.of(rec1, rec2), table.select());

        table.insert(List.of(rec3, rec4));
        assertEquals(List.of(rec1, rec2, rec3, rec4), table.select());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLSerialTableTest");
    }
}
