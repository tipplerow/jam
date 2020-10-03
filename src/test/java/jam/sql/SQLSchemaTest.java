
package jam.sql;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLSchemaTest {
    private static final File DB_FILE = new File("data/test/schema_test.db");

    private static final SQLiteDb DB = SQLiteDb.instance(DB_FILE);

    @AfterClass public static void tearDownClass() {
        //DB_FILE.delete();
    }

    @Test public void test1() {
        SQLColumn column1 = SQLColumn.ofInteger("pmid").primaryKey();
        SQLColumn column2 = SQLColumn.ofVarChar("doi", 255).unique().notNull();
        SQLColumn column3 = SQLColumn.ofVarChar("country", 63).withIndex();

        SQLSchema schema = SQLSchema.create("test_table1", column1, column2, column3);
        DB.createTable(schema);
    }

    @Test public void test2() {
        SQLColumn column1 = SQLColumn.ofInteger("pmid").compositeKey();
        SQLColumn column2 = SQLColumn.ofVarChar("keyword", 255).compositeKey();

        SQLSchema schema = SQLSchema.create("test_table2", column1, column2);
        DB.createTable(schema);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLSchemaTest");
    }
}
