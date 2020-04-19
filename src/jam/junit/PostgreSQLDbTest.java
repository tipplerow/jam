
package jam.junit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import jam.app.JamEnv;
import jam.io.FileUtil;
import jam.sql.PostgreSQLDb;
import jam.sql.QueryResult;
import jam.sql.SQLDb;
import jam.sql.SQLEndpoint;

import org.junit.*;
import static org.junit.Assert.*;

public class PostgreSQLDbTest {
    private static boolean canConnect = false;

    private static PostgreSQLDb createDb() {
        if (!PostgreSQLDb.isInstalled())
            return null;

        SQLEndpoint endpoint = resolveEndpoint();

        if (endpoint != null)
            return PostgreSQLDb.instance(endpoint);
        else
            return null;
    }

    private static SQLEndpoint resolveEndpoint() {
        //
        // The unit tests assume that a PostgreSQL server is running
        // on the local host on the standard port number and that a
        // database owned by the user and named after the user has
        // been created for testing.  The database password must be
        // specified in the POSTGRESQL_PASSWORD environment variable...
        //
        try {
            int port = PostgreSQLDb.DEFAULT_PORT;
            String hostname = "localhost";
            String database = JamEnv.getRequired("USER");
            String username = JamEnv.getRequired("USER");
            String password = JamEnv.getRequired("POSTGRESQL_PASSWORD");

            return SQLEndpoint.create(hostname, port, database, username, password);
        }
        catch (Exception ex) {
            return null;
        }
    }

    static {
        try (Connection connection = createDb().openConnection()) {
            canConnect = true;
        }
        catch (Exception ex) {
            canConnect = false;
        }
    }

    @Test public void testBulkImport() throws SQLException {
        if (!canConnect)
            return;

        SQLDb db = createDb();

        String tableName = "test_table";
        String tableSchema = "key text PRIMARY KEY, value integer";
        String fileName = FileUtil.join(JamEnv.getRequired("JAM_HOME"), "data", "test", "bulk_insert.psv");
        String delimiter = "|";
 
        db.verbose(true);
        db.createTable(tableName, tableSchema);
        db.bulkInsert(tableName, fileName, delimiter);

        try (QueryResult queryResult = db.executeQuery(String.format("SELECT * FROM %s", tableName))) {
            ResultSet resultSet = queryResult.getResultSet();

            resultSet.next();
            assertEquals("abc", resultSet.getString(1));
            assertEquals(1, resultSet.getInt(2));

            resultSet.next();
            assertEquals("def", resultSet.getString(1));
            assertEquals(2, resultSet.getInt(2));

            resultSet.next();
            assertEquals("ghi", resultSet.getString(1));
            assertEquals(3, resultSet.getInt(2));

            resultSet.next();
            assertEquals("foo", resultSet.getString(1));
            assertEquals(0, resultSet.getInt(2));

            resultSet.next();
            assertEquals("bar", resultSet.getString(1));
            assertEquals(0, resultSet.getInt(2));
        }
        finally {
            db.dropTable("test_table");
        }
    }

    @Test public void testCreateDrop() {
        if (!canConnect)
            return;

        SQLDb db = createDb();

        db.verbose(true);
        assertFalse(db.tableExists("test_table"));

        db.createTable("test_table", "key text PRIMARY KEY, value integer");
        db.createTable("test_table", "key text PRIMARY KEY, value integer");
        assertTrue(db.tableExists("test_table"));

        db.dropTable("test_table");
        db.dropTable("test_table");
        assertFalse(db.tableExists("test_table"));
    }

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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PostgreSQLDbTest");
    }
}
