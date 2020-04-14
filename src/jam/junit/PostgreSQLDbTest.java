
package jam.junit;

import java.sql.Connection;

import jam.app.JamEnv;
import jam.sql.PostgreSQLDb;
import jam.sql.SQLDb;
import jam.sql.SQLEndpoint;

import org.junit.*;
import static org.junit.Assert.*;

public class PostgreSQLDbTest {

    private static SQLDb createDb() {
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

    private static boolean canConnect() {
        try (Connection connection = createDb().openConnection()) {
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Test public void testCreateTable() {
        if (!canConnect())
            return;

        SQLDb db = createDb();

        db.verbose(true);
        assertFalse(db.tableExists("test_table"));

        db.createTable("test_table", "key text PRIMARY KEY, value integer");
        assertTrue(db.tableExists("test_table"));

        db.executeUpdate("drop table test_table");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PostgreSQLDbTest");
    }
}








