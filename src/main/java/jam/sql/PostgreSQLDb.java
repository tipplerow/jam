
package jam.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jam.app.JamEnv;
import jam.app.JamLogger;
import jam.lang.JamException;
import jam.process.JamProcess;

/**
 * Manages a {@code PostgreSQL} database and its connections.
 */
public final class PostgreSQLDb extends SQLDb {
    private final SQLEndpoint endpoint;

    private static final Map<SQLEndpoint, PostgreSQLDb> instances =
        new HashMap<SQLEndpoint, PostgreSQLDb>();

    private static final String DRIVER_CLASS = "org.postgresql.Driver";

    private PostgreSQLDb(SQLEndpoint endpoint) {
        super(DRIVER_CLASS);
        this.endpoint = endpoint;
    }

    /**
     * The default port number for the server process.
     */
    public static final int DEFAULT_PORT = 5432;

    /**
     * Returns the {@code PostgreSQL} database manager with a fixed
     * endpoint.
     *
     * @param endpoint the endpoint for database connections.
     *
     * @return the {@code PostgreSQL} database manager with the
     * specified endpoint.
     */
    public static synchronized PostgreSQLDb instance(SQLEndpoint endpoint) {
        PostgreSQLDb instance = instances.get(endpoint);

        if (instance == null) {
            instance = new PostgreSQLDb(endpoint);
            instances.put(endpoint, instance);
        }

        return instance;
    }

    /**
     * Returns the PostgreSQL testing database.
     *
     * @return the PostgreSQL testing database.
     */
    public static PostgreSQLDb test() {
        return instance(SQLEndpoint.testPostgreSQL());
    }

    /**
     * Determines whether {@code PostgreSQL} is installed on this
     * machine.
     *
     * @return {@code true} iff the {@code psql} command-line client
     * is on the execution path.
     */
    public static boolean isInstalled() {
        JamProcess process = JamProcess.run("which", "psql");

        return process.success()
            && process.stdout().size() == 1
            && process.stdout().get(0).endsWith("psql");
    }

    /**
     * Copies rows from a delimited file (with no header line)
     * using the {@code psql \copy} command.
     *
     * <p>This override is required in place of the {@code COPY}
     * command on AWS-managed servers where superuser roles cannot
     * be granted).
     *
     * @param tableName the name of the table to populate.
     *
     * @param fileName the name of a delimited file containing the
     * rows to be added (with no header line).
     *
     * @param delimiter the column delimiter in the flat file.
     *
     * @param nullString the SQL {@code NULL} identifier in the flat file.
     *
     * @throws RuntimeException if the update cannot be executed.
     */
    @Override public void bulkCopy(String tableName, String fileName, char delimiter, String nullString) {
        File bulkFile = new File(fileName);

        try {
            JamProcess process =
                JamProcess.create("psql",
                                  "-h", endpoint.getHostname(),
                                  "-p", endpoint.portString(),
                                  "-U", endpoint.getUsername(),
                                  "-d", endpoint.getDatabase(),
                                  "-c", formatBulkCopy(tableName, bulkFile));

            process.setenv("PGPASSWORD", endpoint.getPassword());
            process.run();
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    private static String formatBulkCopy(String tableName, File bulkFile) throws IOException {
        return String.format("\\copy %s from '%s' with DELIMITER '%c' NULL '%s'",
                             tableName, bulkFile.getCanonicalPath(), BulkRecord.DELIMITER_CHAR, BulkRecord.NULL_STRING);
    }

    /**
     * Returns the connection endpoint.
     *
     * @return the connection endpoint.
     */
    public SQLEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Returns the database URL for the driver manager.
     *
     * @return the database URL for the driver manager.
     */
    public String formatURL() {
        int port = endpoint.getPort();
        String hostname = endpoint.getHostname();
        String database = endpoint.getDatabase();

        return String.format("jdbc:postgresql://%s:%d/%s", hostname, port, database);
    }

    /**
     * Returns the {@code POSTGRES} database engine type.
     *
     * @return the {@code POSTGRES} database engine type.
     */
    @Override public SQLEngine getEngineType() {
        return SQLEngine.POSTGRES;
    }

    /**
     * Opens a new database connection.
     *
     * @return a new open database connection.
     *
     * @throws RuntimeException if the connection cannot be opened.
     */
    @Override public Connection openConnection() {
        String url = formatURL();
        JamLogger.info("Connecting [%s]...", url);

        try {
            return DriverManager.getConnection(formatURL(),
                                               endpoint.getUsername(),
                                               endpoint.getPassword());
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }
}
