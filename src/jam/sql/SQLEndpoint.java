
package jam.sql;

import java.util.List;

/**
 * Encapsulates the information required to create a database
 * connection.
 */
public final class SQLEndpoint {
    private final int port;
    private final String hostname;
    private final String database;
    private final String username;
    private final String password;

    private SQLEndpoint(String hostname, int port, String database, String username, String password) {
        this.port = port;
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a new endpoint with fixed parameters.
     *
     * @param hostname the name of the database server.
     *
     * @param port the port number for database connections.
     *
     * @param database the name of the initial database.
     *
     * @param username the name of the connecting user.
     *
     * @param password the password for the connecting user.
     *
     * @return an endpoing with the specified parameters.
     */
    public static SQLEndpoint create(String hostname,
                                     int    port,
                                     String database,
                                     String username,
                                     String password) {
        return new SQLEndpoint(hostname, port, database, username, password);
    }

    /**
     * Returns the name of the database server.
     *
     * @return the name of the database server.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Returns the port number for database connections.
     *
     * @return the port number for database connections.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the name of the initial database.
     *
     * @return the name of the initial database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Returns the name of the connecting user.
     *
     * @return the name of the connecting user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password for the connecting user.
     *
     * @return the password for the connecting user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the port number formatted as a string.
     *
     * @return the port number formatted as a string.
     */
    public String portString() {
        return Integer.toString(port);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof SQLEndpoint) && equalsEndpoint((SQLEndpoint) obj);
    }

    private boolean equalsEndpoint(SQLEndpoint that) {
        return this.port == that.port
            && this.hostname.equals(that.hostname)
            && this.database.equals(that.database)
            && this.username.equals(that.username)
            && this.password.equals(that.password);
    }

    @Override public int hashCode() {
        //
        // Password should match up with user, so no need to include
        // it in the hash code...
        //
        return port + List.of(hostname, database, username).hashCode();
    }

    @Override public String toString() {
        //
        // Do not display the password...
        //
        return String.format("SQLEndpoint(%s, %d, %s, %s)", hostname, port, database, username);
    }
}
