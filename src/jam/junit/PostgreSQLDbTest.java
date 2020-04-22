
package jam.junit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jam.app.JamEnv;
import jam.io.FileUtil;
import jam.sql.BulkRecord;
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

    private static final String TEST_TABLE_NAME = "test_table";
    private static final String TEST_TABLE_SCHEMA = "key text PRIMARY KEY, value integer";

    private static final String BULK_IMPORT_FILE =
        FileUtil.join(JamEnv.getRequired("JAM_HOME"), "data", "test", "bulk_insert.psv");

    private void createTestTable(SQLDb db) {
        db.createTable(TEST_TABLE_NAME, TEST_TABLE_SCHEMA);
    }

    private static class TestRecord implements BulkRecord {
        public final String key;
        public final Integer value;

        public TestRecord(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        @Override public String formatBulk() {
            return joinBulk(key, value != null ? value.toString() : NULL_STRING);
        }
    }

    @Test public void testBulkCopy() throws SQLException {
        if (!canConnect)
            return;

        PostgreSQLDb db = createDb();

        db.verbose(true);
        createTestTable(db);

        db.bulkCopy(TEST_TABLE_NAME,
                    List.of(new TestRecord("abc", 1),
                            new TestRecord("def", 2),
                            new TestRecord("ghi", 3),
                            new TestRecord("foo", null),
                            new TestRecord("bar", null)));

        assertBulkImport(db);
    }

    @Test public void testBulkImport1() throws SQLException {
        if (!canConnect)
            return;

        SQLDb db = createDb();

        db.verbose(true);
        createTestTable(db);

        db.bulkImport(TEST_TABLE_NAME, BULK_IMPORT_FILE, BulkRecord.DELIMITER_CHAR, BulkRecord.NULL_STRING);
        assertBulkImport(db);
    }

    private void assertBulkImport(SQLDb db) throws SQLException {
        try (QueryResult queryResult = db.executeQuery(String.format("SELECT * FROM %s", TEST_TABLE_NAME))) {
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
            db.dropTable(TEST_TABLE_NAME);
        }
    }

    @Test public void testBulkImport2() throws SQLException {
        if (!canConnect)
            return;

        SQLDb db = createDb();

        db.verbose(true);
        createTestTable(db);

        db.bulkImport(TEST_TABLE_NAME,
                      List.of(new TestRecord("abc", 1),
                              new TestRecord("def", 2),
                              new TestRecord("ghi", 3),
                              new TestRecord("foo", null),
                              new TestRecord("bar", null)));

        assertBulkImport(db);
    }

    @Test public void testCreateDrop() {
        if (!canConnect)
            return;

        SQLDb db = createDb();

        db.verbose(true);
        assertFalse(db.tableExists(TEST_TABLE_NAME));

        db.createTable(TEST_TABLE_NAME, TEST_TABLE_SCHEMA);
        db.createTable(TEST_TABLE_NAME, TEST_TABLE_SCHEMA);
        assertTrue(db.tableExists(TEST_TABLE_NAME));

        db.dropTable(TEST_TABLE_NAME);
        db.dropTable(TEST_TABLE_NAME);
        assertFalse(db.tableExists(TEST_TABLE_NAME));
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
