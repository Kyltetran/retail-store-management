package databasesystems.project2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private Connection connection;

    public OrderService(Connection connection) {
        this.connection = connection;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = MySQLConnection.getConnection(); // Reconnect if connection is closed
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        ensureConnection();

        List<Order> orders = new ArrayList<>();

        // Fetch all orders placed by the customer
        String query = "SELECT Orders.OrderID, Customer.CustomerName, Orders.TotalCost, Orders.OrderDate " +
                "FROM Orders " +
                "JOIN Customer ON Orders.CustomerID = Customer.CustomerID";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int orderId = resultSet.getInt("OrderID");
                String customerName = resultSet.getString("CustomerName");
                String status = "Confirmed"; // Assuming all fetched orders are confirmed
                double totalCost = resultSet.getDouble("TotalCost");
                Date orderDate = resultSet.getDate("OrderDate");

                orders.add(new Order(orderId, customerName, status, totalCost, orderDate));
            }
        }

        return orders; // This will now return all orders without filtering by time or session
    }

    public List<OrderDetail> getOrderDetails(int orderId) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = MySQLConnection.getConnection(); // Ensure connection is active
        }

        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT ProductID, Quantity FROM OrderDetail WHERE OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                int quantity = rs.getInt("Quantity");
                orderDetails.add(new OrderDetail(orderId, productId, quantity));
            }
        }

        return orderDetails;
    }

}
