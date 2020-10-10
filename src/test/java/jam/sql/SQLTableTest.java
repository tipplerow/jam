
package jam.sql;

import java.sql.SQLException;
import java.util.List;

import jam.app.JamEnv;
import jam.collect.MapTableTestBase;
import jam.collect.TestRecord;
import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public final class SQLTableTest extends MapTableTestBase {
    SQLDb postgres = PostgreSQLDb.test();

    @Test public void testBulkCopy() throws SQLException {
        if (PostgreSQLDb.isInstalled())
            runBulkCopyTest(PostgreSQLDb.test());
    }

    private void runBulkCopyTest(SQLDb db) throws SQLException {
        TestTable table = new TestTable(db, "test_bulk_copy");
        table.delete();
        
        String fileName   = FileUtil.join(JamEnv.getRequired("JAM_HOME"), "data", "test", "bulk_insert.psv");
        char   delimiter  = BulkRecord.DELIMITER_CHAR;
        String nullString = BulkRecord.NULL_STRING;

        table.copy(fileName, delimiter, nullString);
        List<TestRecord> records = table.fetch();

        assertEquals(List.of(rec1, rec2, rec3, rec4, rec5), records);
        table.drop();
    }
    /*
    @Test public void testDelete() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_delete");

        runDeleteTest(table);
        table.drop();
    }

    @Test public void testInsert() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_insert");

        runInsertTest(table);
        table.drop();
    }

    @Test public void testUpdate() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_update");

        runUpdateTest(table);
        table.drop();
    }

    @Test public void testUpsert() {
        if (!PostgreSQLDb.isInstalled())
            return;

        TestTable table = new TestTable(postgres, "test_upsert");

        runUpsertTest(table);
        table.drop();
    }
    */
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLTableTest");
    }
}
