
package jam.junit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.*;
import static org.junit.Assert.*;

public class SqliteTest {
    private static final File DB_FILE = new File("sample.db");

    @Test public void testFoo() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");

            ResultSet rs = statement.executeQuery("select * from person");

            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        }
        catch(SQLException e) {
            // if the error message is "out of memory", 
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally  {
            try {
                if (connection != null)
                    connection.close();

                DB_FILE.delete();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SqliteTest");
    }
}
