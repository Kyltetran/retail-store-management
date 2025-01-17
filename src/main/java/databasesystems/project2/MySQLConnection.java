package databasesystems.project2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Project2";
    private static final String USER = "root";
    private static final String PASSWORD = "Nhuquynh,123";
    private static Connection connection = null;

    // Static block for initialization
    static {
        try {
            initializeConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection during initialization: " + e.getMessage());
            // Don't crash the app, but log the error
            e.printStackTrace();
        }
    }

    // Method to initialize the connection
    private static void initializeConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database connection successfully established.");
    }

    // Method to get the connection, reconnecting if necessary
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeConnection(); // Reconnect if the connection is closed or null
        }
        return connection;
    }

    // Method to check if the connection is valid
    public static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2); // Check validity with
                                                                                          // 2-second timeout
        } catch (SQLException e) {
            System.err.println("Failed to validate the connection: " + e.getMessage());
            return false;
        }
    }

    // Graceful shutdown of the connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close the database connection: " + e.getMessage());
        }
    }
}
