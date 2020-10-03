
package jam.sql;

import java.sql.SQLException;
import java.util.List;

import jam.app.JamEnv;
import jam.io.FileUtil;

import org.junit.*;
import static org.junit.Assert.*;

public final class PostgreSQLDbTest {
    private final PostgreSQLDb db;

    public PostgreSQLDbTest() {
        this.db = createDb();
    }

    private static PostgreSQLDb createDb() {
        if (PostgreSQLDb.isInstalled())
            return PostgreSQLDb.test();
        else
            return null;
    }
    /*
    @Test public void testCreateDropTable() {
        if (db == null)
            return;

        TestTable table = new TestTable(db, "test_create_drop");
        table.drop();
    }
    */
    @Test public void testBulkCopy() throws SQLException {
        if (db == null)
            return;

        TestTable table = new TestTable(db, "test_bulk_copy");
        table.delete();
        
        String fileName   = FileUtil.join(JamEnv.getRequired("JAM_HOME"), "data", "test", "bulk_insert.psv");
        char   delimiter  = BulkRecord.DELIMITER_CHAR;
        String nullString = BulkRecord.NULL_STRING;

        table.copy(fileName, delimiter, nullString);
        List<TestRecord> records = table.select();

        assertEquals(List.of(TestRecord.REC1,
                             TestRecord.REC2,
                             TestRecord.REC3,
                             TestRecord.REC4,
                             TestRecord.REC5), records);
        table.drop();
    }
    /*
    private static enum Foo {
        ABC, DEF, GHI;
    }

    @Test public void testCreateEnum() {
        if (!canConnect)
            return;

        PostgreSQLDb db = createDb();
        db.createEnum("my_enum", Foo.class);
        db.executeUpdate("DROP TYPE my_enum");
    }

    @Test public void testUserExists() {
        if (!canConnect)
            return;

        PostgreSQLDb db = createDb();

        assertTrue(db.userExists(JamEnv.getRequired("USER")));
        assertFalse(db.userExists("no such user"));
    }
    */
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.PostgreSQLDbTest");
    }
}
