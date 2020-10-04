
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

    @Test public void testBulkCopy() throws SQLException {
        //
        // This is now tested in SQLTableTest...
        //
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
    */
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.PostgreSQLDbTest");
    }
}
