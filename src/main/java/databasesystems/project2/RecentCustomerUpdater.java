package databasesystems.project2;

import redis.clients.jedis.Jedis;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RecentCustomerUpdater {

    // Method to update recent customers from MySQL to Redis
    public void updateRecentCustomersFromDB() {
        // Get MySQL connection
        Connection connection = null;

        try {
            connection = MySQLConnection.getConnection();

            // SQL query to get the most recent customers and their details
            String query = "SELECT c.CustomerID, c.CustomerName, c.Address, c.PhoneNumber, c.Email " +
                    "FROM Orders o " +
                    "JOIN Customer c ON o.CustomerID = c.CustomerID " +
                    "ORDER BY o.OrderDate DESC LIMIT 50"; // Assuming recent customers are based on Orders

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet resultSet = stmt.executeQuery();

                // Get Redis client
                Jedis jedis = RedisConnection.getJedis();

                // Clear existing recent customers in Redis (optional)
                jedis.del("recent_customers");

                // Add recent customer details to Redis
                while (resultSet.next()) {
                    String customerId = resultSet.getString("CustomerID");
                    String customerName = resultSet.getString("CustomerName");
                    String address = resultSet.getString("Address");
                    String phoneNumber = resultSet.getString("PhoneNumber");
                    String email = resultSet.getString("Email");

                    // Create a map to hold customer details
                    Map<String, String> customerDetails = new HashMap<>();
                    customerDetails.put("CustomerName", customerName);
                    customerDetails.put("Address", address);
                    customerDetails.put("PhoneNumber", phoneNumber);
                    customerDetails.put("Email", email);

                    // Store the customer details in Redis as a hash
                    jedis.hset("recent_customer:" + customerId, customerDetails);

                    // Add the customer ID to the list of recent customers
                    jedis.lpush("recent_customers", customerId);
                }

                // Trim the list to keep only the 50 most recent customers
                jedis.ltrim("recent_customers", 0, 49);

                System.out.println("Recent customers updated in Redis.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close(); // Ensure the connection is closed
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
