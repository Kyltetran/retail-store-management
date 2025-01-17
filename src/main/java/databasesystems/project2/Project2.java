package databasesystems.project2;

import java.sql.Connection;
import java.sql.SQLException;

public class Project2 {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = MySQLConnection.getConnection();
            if (connection != null) {
                System.out.println("Database connection is ready to be used.");
                UserAuthentication auth = new UserAuthentication(connection);
                LoginScreen loginScreen = new LoginScreen();
                new LoginController(loginScreen, auth);
            } else {
                System.out.println("Failed to establish database connection.");
                System.exit(1);
            }

            // Add a shutdown hook to properly close the connection
            final Connection finalConnection = connection;
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if (finalConnection != null && !finalConnection.isClosed()) {
                            finalConnection.close();
                            System.out.println("Database connection closed.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
