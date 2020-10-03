
package jam.sql;

import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLStoreTest extends SQLTestBase {
    public SQLStoreTest() {
        super("data/test/sql_store_test.db");
    }

    @Test public void testGet() {
        TestTable table = new TestTable(db);
        TestStore store = new TestStore(table);

        assertTrue(db.tableExists(table.getTableName()));
        assertTrue(table.load().isEmpty());

        table.store(List.of(rec1, rec2));
        assertRecords(List.of(rec1, rec2), table.load());

        // Retrieve pre-computed records...
        assertRecords(List.of(rec1, rec2), store.hash(List.of(key1, key2)));

        // Compute on demand and store...
        assertRecords(List.of(rec1, rec2, rec3), store.hash(List.of(key1, key2, key3)));
        assertRecords(List.of(rec1, rec2, rec3), table.load());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLStoreTest");
    }
}
