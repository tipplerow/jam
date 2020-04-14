
package jam.junit;

import jam.sql.SQLDb;
import jam.sql.SQLiteDb;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLDbTest extends SQLTestBase {
    public SQLDbTest() {
        super("data/test/sql_db_test.db");
    }

    @Test public void testCreateDropTable() {
        db.verbose(true);
        assertFalse(db.tableExists("test_table"));

        db.createTable("test_table", "key string PRIMARY KEY, value double");
        db.createTable("test_table", "key string PRIMARY KEY, value double");
        assertTrue(db.tableExists("test_table"));

        db.dropTable("test_table");
        db.dropTable("test_table");
        assertFalse(db.tableExists("test_table"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLDbTest");
    }
}








