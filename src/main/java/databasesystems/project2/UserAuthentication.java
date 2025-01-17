package databasesystems.project2;

import java.sql.*;

public class UserAuthentication {
    private final Connection connection;

    // SQL query to retrieve username and role from the database
    private static final String AUTH_QUERY = "SELECT username, role FROM users WHERE username = ? AND password = ?";

    // Add constructor that accepts Connection
    public UserAuthentication(Connection connection) {
        this.connection = connection;
    }

    public User login(String username, String password) throws SQLException {
        try (Connection connection = MySQLConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(AUTH_QUERY)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role"); // Get the user's role (buyer or seller)
                return new User(username, password, role); // Create a new User object
            } else {
                throw new SQLException("Invalid username or password");
            }
        }
    }
}
