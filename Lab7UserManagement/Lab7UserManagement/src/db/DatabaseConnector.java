package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection helper.
 * Change USER / PASSWORD to match your own MySQL installation.
 */
public class DatabaseConnector {

    private static final String URL =
            "jdbc:mysql://localhost:3306/lab7db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "my-secret-pw";   // <-- your MySQL password

    private DatabaseConnector() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Add mysql-connector-j-x.x.x.jar to the classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Startup check so the app can show a readable message instead of a stack trace. */
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
