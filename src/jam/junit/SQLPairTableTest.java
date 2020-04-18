
package jam.junit;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jam.sql.SQLDb;
import jam.sql.SQLiteDb;
import jam.sql.SQLPairRecord;
import jam.sql.SQLPairTable;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLPairTableTest {
    private final SQLDb db;

    private static final File DB_FILE = new File("data/test/sql_pair_table.db");

    public SQLPairTableTest() {
        db = SQLiteDb.instance(DB_FILE);
        DB_FILE.deleteOnExit();
    }

    private static class PairRecord extends SQLPairRecord<Integer, String> {
        public PairRecord(Integer id, String name) {
            super(id, name);
        }
    }

    private static class PairTable extends SQLPairTable<Integer, String, PairRecord> {
        public PairTable(SQLDb db) {
            super(db);
        }

        @Override public String getKey1Name() {
            return "id";
        }

        @Override public String getKey1Type() {
            return "int";
        }

        @Override public String getKey2Name() {
            return "name";
        }

        @Override public String getKey2Type() {
            return "string";
        }

        @Override public PairRecord getRecord(ResultSet resultSet) throws SQLException {
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);

            return new PairRecord(id, name);
        }

        @Override public String getTableName() {
            return "test_table";
        }

        @Override public void setKey1(PreparedStatement statement, int index, Integer id) throws SQLException  {
            statement.setInt(index, id.intValue());
        }

        @Override public void setKey2(PreparedStatement statement, int index, String name) throws SQLException {
            statement.setString(index, name);
        }
    }


    @Test public void testLoadStore() {
        PairTable table = new PairTable(db);

        assertFalse(table.exists());
        table.require();
        assertTrue(table.exists());

        PairRecord rec11 = new PairRecord(1, "1");
        PairRecord rec12 = new PairRecord(1, "2");
        PairRecord rec13 = new PairRecord(1, "3");
        PairRecord rec21 = new PairRecord(2, "1");
        PairRecord rec31 = new PairRecord(3, "1");
        
        assertTrue(table.load().isEmpty());

        assertTrue(table.fetchKey1(1).isEmpty());
        assertTrue(table.fetchKey1(2).isEmpty());
        assertTrue(table.fetchKey1(3).isEmpty());

        assertTrue(table.fetchKey2("1").isEmpty());
        assertTrue(table.fetchKey2("2").isEmpty());
        assertTrue(table.fetchKey2("3").isEmpty());

        assertFalse(table.contains(rec11));
        assertFalse(table.contains(rec12));
        assertFalse(table.contains(rec13));
        assertFalse(table.contains(rec21));
        assertFalse(table.contains(rec31));

        assertFalse(table.containsKey1(1));
        assertFalse(table.containsKey1(2));
        assertFalse(table.containsKey1(3));

        assertFalse(table.containsKey2("1"));
        assertFalse(table.containsKey2("2"));
        assertFalse(table.containsKey2("3"));

        table.store(List.of(rec11, rec12));

        assertEquals(List.of(rec11, rec12), table.load());

        assertEquals(List.of(rec11, rec12), table.fetchKey1(1));
        assertTrue(table.fetchKey1(2).isEmpty());
        assertTrue(table.fetchKey1(3).isEmpty());

        assertEquals(List.of(rec11), table.fetchKey2("1"));
        assertEquals(List.of(rec12), table.fetchKey2("2"));
        assertTrue(table.fetchKey2("3").isEmpty());
        
        assertTrue(table.contains(rec11));
        assertTrue(table.contains(rec12));
        assertFalse(table.contains(rec13));
        assertFalse(table.contains(rec21));
        assertFalse(table.contains(rec31));

        assertTrue(table.containsKey1(1));
        assertFalse(table.containsKey1(2));
        assertFalse(table.containsKey1(3));

        assertTrue(table.containsKey2("1"));
        assertTrue(table.containsKey2("2"));
        assertFalse(table.containsKey2("3"));

        table.store(List.of(rec13, rec21, rec31));

        assertEquals(List.of(rec11, rec12, rec13, rec21, rec31), table.load());

        assertEquals(List.of(rec11, rec12, rec13), table.fetchKey1(1));
        assertEquals(List.of(rec21), table.fetchKey1(2));
        assertEquals(List.of(rec31), table.fetchKey1(3));

        assertEquals(List.of(rec11, rec21, rec31), table.fetchKey2("1"));
        assertEquals(List.of(rec12), table.fetchKey2("2"));
        assertEquals(List.of(rec13), table.fetchKey2("3"));
        
        assertTrue(table.contains(rec11));
        assertTrue(table.contains(rec12));
        assertTrue(table.contains(rec13));
        assertTrue(table.contains(rec21));
        assertTrue(table.contains(rec31));

        assertTrue(table.containsKey1(1));
        assertTrue(table.containsKey1(2));
        assertTrue(table.containsKey1(3));

        assertTrue(table.containsKey2("1"));
        assertTrue(table.containsKey2("2"));
        assertTrue(table.containsKey2("3"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLPairTableTest");
    }
}
