package databasesystems.project2;

import redis.clients.jedis.Jedis;
import java.sql.*;

public class BestSellingProductUpdater {

    // Method to update best-selling products from MySQL to Redis
    public void updateBestSellingProductsFromDB() {
        // Get MySQL connection
        Connection connection = null;
        try {
            connection = MySQLConnection.getConnection();

            // SQL query to get top-selling products from OrderDetail and Product tables
            String query = "SELECT p.ProductName, SUM(od.Quantity) AS sales_count " +
                    "FROM OrderDetail od " +
                    "JOIN Product p ON od.ProductID = p.ProductID " +
                    "GROUP BY p.ProductName " +
                    "ORDER BY sales_count DESC " +
                    "LIMIT 10";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                // Execute the query and get the result
                ResultSet resultSet = stmt.executeQuery();

                // Get Redis client from the RedisConnection class
                Jedis jedis = RedisConnection.getJedis();

                // Optionally, clear existing data in Redis (if needed)
                jedis.del("best_selling_products");

                // Loop through the result set and add products to Redis sorted set
                while (resultSet.next()) {
                    String productName = resultSet.getString("ProductName");
                    int salesCount = resultSet.getInt("sales_count");

                    // Add to Redis sorted set with the sales count as the score
                    jedis.zadd("best_selling_products", salesCount, productName);
                }

                System.out.println("Best-selling products updated in Redis.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
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
