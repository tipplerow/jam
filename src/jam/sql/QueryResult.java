
package jam.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jam.lang.JamException;

/**
 * Encapsulates a {@code ResultSet} along with the {@code Statement}
 * and {@code Connection} that created it so that all resources may be
 * released after the result set has been processed.
 */
public final class QueryResult implements AutoCloseable {
    private final boolean closeConn;
    private final ResultSet resultSet;

    private QueryResult(ResultSet resultSet, boolean closeConn) {
        this.closeConn = closeConn;
        this.resultSet = resultSet;
    }

    public static QueryResult create(Connection connection, String queryString, boolean closeConn) {
        Statement statement = null;

        try {
            statement = connection.createStatement();
            return create(statement, queryString, closeConn);
        }
        catch (SQLException ex) {
            SQLUtil.close(statement);
            throw JamException.runtime(ex);
        }
    }

    private static QueryResult create(Statement statement, String queryString, boolean closeConn) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery(queryString);
            return new QueryResult(resultSet, closeConn);
        }
        catch (SQLException ex) {
            SQLUtil.close(resultSet);
            SQLUtil.close(statement);

            throw JamException.runtime(ex);
        }
    }

    public Connection getConnection() {
        try {
            return getStatement().getConnection();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public Statement getStatement() {
        try {
            return getResultSet().getStatement();
        }
        catch (SQLException ex) {
            throw JamException.runtime(ex);
        }
    }

    @Override public void close() {
        //
        // Always close the (privately created) statement and result
        // set; only close the connection if the user indicated so...
        //
        SQLUtil.close(getResultSet());
        SQLUtil.close(getStatement());

        if (closeConn)
            SQLUtil.close(getConnection());
    }
}
