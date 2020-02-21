
package jam.junit;

import java.io.File;

import jam.sql.SQLDb;
import jam.sql.SQLiteDb;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLDbTest {
    public static final String FILE_NAME = "data/test/sql_db_test.db";

    @Before public void tearDown() {
        deleteDbFile();
    }

    @AfterClass public static void tearDownClass() {
        deleteDbFile();
    }

    private static void deleteDbFile() {
        File dbFile = new File(FILE_NAME);

        if (dbFile.exists())
            dbFile.delete();
    }

    @Test public void testCreateTable() {
        SQLDb db = SQLiteDb.instance(FILE_NAME);

        db.verbose(true);
        assertFalse(db.tableExists("test_table"));

        db.createTable("test_table", "key string PRIMARY KEY, value double");
        assertTrue(db.tableExists("test_table"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLDbTest");
    }
}








