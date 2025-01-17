package databasesystems.project2;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ManagerDashboardController {

    private ManagerDashboardView view;
    private Jedis jedis;
    private BestSellingProductUpdater bestSellingProductUpdater;
    private RecentCustomerUpdater recentCustomerUpdater;

    public ManagerDashboardController(ManagerDashboardView view) {
        // Initialize the Jedis connection
        this.jedis = RedisConnection.getJedis();
        this.view = view;

        // Initialize BestSellingProductUpdater
        this.bestSellingProductUpdater = new BestSellingProductUpdater();
        this.recentCustomerUpdater = new RecentCustomerUpdater();

        // Call method to update the best-selling products from DB to Redis
        updateBestSellingProductsForDashboard();
        updateRecentCustomersForDashboard();

        this.view.addBackToLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin(); // Call the method to return to the login screen
            }
        });
    }

    private void backToLogin() {
        view.dispose(); // Close the current buyer dashboard
        Connection connection = null;
        try {
            connection = MySQLConnection.getConnection(); // This method must be declared to throw SQLException
            if (connection != null) {
                UserAuthentication auth = new UserAuthentication(connection); // Pass the connection to the constructor
                LoginScreen loginScreen = new LoginScreen();
                new LoginController(loginScreen, auth); // Attach the login controller to the login screen
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to update best-selling products for the dashboard
    private void updateBestSellingProductsForDashboard() {
        bestSellingProductUpdater.updateBestSellingProductsFromDB(); // Update Redis with new data
        List<Map.Entry<String, Integer>> bestSellingProducts = getBestSellingProducts(10); // Get top 10 best-selling
                                                                                           // products
        view.updateBestSellingProducts(bestSellingProducts); // Update the dashboard view
    }

    // Get top N best-selling products from Redis (sorted by sales count)
    public List<Map.Entry<String, Integer>> getBestSellingProducts(int topN) {
        // Use zrevrangeWithScores which now returns a List<Tuple> in Jedis 4.x
        List<Tuple> bestSellingProductTuples = jedis.zrevrangeWithScores("best_selling_products", 0, topN - 1);

        List<Map.Entry<String, Integer>> bestSellingProducts = new ArrayList<>();

        // Iterate through the Tuples
        for (Tuple tuple : bestSellingProductTuples) {
            String productName = tuple.getElement(); // Get the product name
            int salesCount = (int) tuple.getScore(); // Get the sales count (score)
            bestSellingProducts.add(new AbstractMap.SimpleEntry<>(productName, salesCount));
        }

        return bestSellingProducts;
    }

    // Method to update recent customers for the dashboard
    private void updateRecentCustomersForDashboard() {
        recentCustomerUpdater.updateRecentCustomersFromDB(); // Update Redis with new data
        List<Map<String, String>> recentCustomers = getRecentCustomers(10); // Get the 10 most recent customers from
                                                                            // Redis
        view.updateRecentCustomers(recentCustomers); // Update the dashboard view
    }

    // Get recent customers (with detailed information)
    public List<Map<String, String>> getRecentCustomers(int limit) {
        List<String> customerIds = jedis.lrange("recent_customers", 0, limit - 1);
        List<Map<String, String>> customers = new ArrayList<>();

        // Retrieve detailed customer info from Redis
        for (String customerId : customerIds) {
            Map<String, String> customerDetails = jedis.hgetAll("recent_customer:" + customerId);
            customers.add(customerDetails);
        }

        return customers;
    }

    // Add a new customer to the recent customers list
    public void addRecentCustomer(String customerId) {
        jedis.lpush("recent_customers", customerId); // Add customer ID to the front of the list
        jedis.ltrim("recent_customers", 0, 49); // Keep only the 50 most recent customers
    }
}
