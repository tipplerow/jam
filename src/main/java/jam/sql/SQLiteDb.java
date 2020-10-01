
package jam.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jam.app.JamLogger;
import jam.io.FileUtil;
import jam.lang.JamException;

/**
 * Manages a SQLite database and its connections.
 */
public final class SQLiteDb extends SQLDb {
    private final String dbURL;
    private final String dbFile;

    // One instance per file...
    private static final Map<String, SQLiteDb> instances =
        new HashMap<String, SQLiteDb>();

    private static final String DRIVER_CLASS = "org.sqlite.JDBC";

    private SQLiteDb(String dbFile) {
        super(DRIVER_CLASS);

        this.dbFile = dbFile;
        this.dbURL  = formatURL(dbFile);
    }

    private static String formatURL(String dbFile) {
        return "jdbc:sqlite:" + dbFile;
    }

    /**
     * Returns the {@code SQLite} database manager for a given file.
     *
     * @param dbFile the persistent database file.
     *
     * @return the {@code SQLite} database manager for the specified
     * persistent database file.
     */
    public static SQLiteDb instance(File dbFile) {
        return instance(FileUtil.getCanonicalPath(dbFile));
    }

    /**
     * Returns the {@code SQLite} database manager for a given file.
     *
     * @param dbFile the name of the persistent database file.
     *
     * @return the {@code SQLite} database manager for the specified
     * persistent database file.
     */
    public static synchronized SQLiteDb instance(String dbFile) {
        SQLiteDb instance = instances.get(dbFile);

        if (instance == null) {
            instance = new SQLiteDb(dbFile);
            instances.put(dbFile, instance);
        }

        return instance;
    }

    /**
     * Returns the name of the persistenet database file.
     *
     * @return the name of the persistent database file.
     */
    public String getDbFile() {
        return dbFile;
    }

    /**
     * Returns the database URL for the driver manager.
     *
     * @return the database URL for the driver manager.
     */
    public String getDbURL() {
        return dbURL;
    }

    @Override public SQLEngine getEngineType() {
        return SQLEngine.SQLITE;
    }

    @Override public Connection openConnection() {
        try {
            return DriverManager.getConnection(dbURL);
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    @Override protected String countTableNamesQuery(String tableName) {
        return String.format("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = '%s'", tableName);
    }
}
