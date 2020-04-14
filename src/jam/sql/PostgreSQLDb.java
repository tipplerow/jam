
package jam.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import jam.app.JamLogger;
import jam.lang.JamException;
import jam.process.ProcessRunner;

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
    public static PostgreSQLDb instance(SQLEndpoint endpoint) {
        PostgreSQLDb instance = instances.get(endpoint);

        if (instance == null) {
            instance = new PostgreSQLDb(endpoint);
            instances.put(endpoint, instance);
        }

        return instance;
    }

    /**
     * Determines whether {@code PostgreSQL} is installed on this
     * machine.
     *
     * @return {@code true} iff the {@code psql} command-line client
     * is on the execution path.
     */
    public static boolean isInstalled() {
        List<String> output = ProcessRunner.run("which", "psql");
        return output.size() == 1 && output.get(0).endsWith("psql");
    }

    /**
     * Creates a new database user.
     *
     * @param username the name of the user to create.
     *
     * @param password the login password for the user.
     *
     * @throws RuntimeException if the user already exists.
     */
    public void createUser(String username, String password) {
        String updateStr = createUserUpdate(username, password);
        executeUpdate(updateStr);
    }

    private static String createUserUpdate(String username, String password) {
        return String.format("create role %s login password '%s'", username, password);
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
     * Determines whether a database user exists.
     *
     * @param username the name of the user in question.
     *
     * @return {@code true} iff a user with the given name exists.
     */
    public boolean userExists(String username) {
        String queryStr = countUserQuery(username);

        try (QueryResult queryResult = executeQuery(queryStr)) {
            return getCount(queryResult.getResultSet()) == 1;
        }
    }

    private static String countUserQuery(String username) {
        return String.format("select count(*) from pg_roles where rolname = '%s'", username);
    }

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

    @Override protected String countTableNamesQuery(String tableName) {
        return String.format("select count(*)"
                             + " from information_schema.tables"
                             + " where table_schema = 'public' and table_name = '%s'", tableName);
    }
}
