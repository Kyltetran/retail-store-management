package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class CheckoutPageController {
    private CheckoutPageView view;
    private Cart cart;
    private BuyerDashboardView buyerDashboardView;
    private Connection connection;
    private Order confirmedOrder;
    private OrderService orderService;

    public CheckoutPageController(CheckoutPageView view, Cart cart, BuyerDashboardView buyerDashboardView,
            Connection connection, OrderService orderService) {
        this.view = view;
        this.cart = cart;
        this.buyerDashboardView = buyerDashboardView;
        this.connection = connection;
        this.orderService = orderService;

        // Add listener for confirm purchase button
        this.view.addConfirmListener(e -> confirmPurchase());

        // Add listener for cancel button
        this.view.addCancelListener(e -> cancelCheckout());
    }

    // Confirm purchase logic
    private void confirmPurchase() {
        try {
            // Ensure the connection is open before proceeding
            if (connection == null || connection.isClosed()) {
                connection = MySQLConnection.getConnection();
                orderService = new OrderService(connection); // Update OrderService with the new connection
            }

            // Start transaction
            connection.setAutoCommit(false);

            // Get customer and payment details from the view
            String paymentMethod = view.getSelectedPaymentMethod();
            String customerName = view.getCustomerName();
            String customerAddress = view.getCustomerAddress();
            String customerPhone = view.getCustomerPhone();
            String customerEmail = view.getCustomerEmail();

            if (customerName.isEmpty() || customerAddress.isEmpty() || customerPhone.isEmpty()
                    || customerEmail.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please fill out all customer details.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perform the transaction steps
            int customerId = insertCustomer(customerName, customerAddress, customerPhone, customerEmail);
            int paymentDetailId = insertPaymentDetails(paymentMethod);
            int orderId = insertOrder(customerId, paymentDetailId);
            insertOrderDetails(orderId);

            // Commit the transaction if all steps succeed
            connection.commit();

            confirmedOrder = new Order(orderId, customerName, "Confirmed", cart.getTotal(),
                    new java.sql.Date(System.currentTimeMillis()));

            JOptionPane.showMessageDialog(view, "Purchase successful! Your order has been placed.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            cart.clear(); // Clear the cart after purchase
            view.dispose();

            // Open OrderTrackingView with the confirmed order
            List<Order> confirmedOrders = new ArrayList<>();
            confirmedOrders.add(confirmedOrder);
            OrderTrackingController controller = new OrderTrackingController(confirmedOrders, buyerDashboardView, cart,
                    new ProductService(), connection, orderService);
            OrderTrackingView orderTrackingView = new OrderTrackingView(confirmedOrders, controller);
            controller.initializeView(orderTrackingView);
            orderTrackingView.setVisible(true);

        } catch (SQLException ex) {
            // Rollback the transaction if any error occurs
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            JOptionPane.showMessageDialog(view, "An error occurred while processing your order: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Restore auto-commit mode to its default state
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                System.err.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
    }

    // Insert or update customer based on whether the customer details have changed
    private int insertCustomer(String name, String address, String phone, String email) throws SQLException {
        System.out.println("Attempting to insert/update customer with email: " + email);

        // First, check if the customer exists using the email
        String selectQuery = "SELECT CustomerID, CustomerName, Address, PhoneNumber, Email FROM Customer WHERE Email = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);

            System.out.println("Executing select query: " + selectStmt.toString()); // Debug log
            ResultSet rs = selectStmt.executeQuery();

            // Log ResultSet Metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Columns in ResultSet:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("Column name: " + metaData.getColumnName(i));
            }

            // Check if the customer exists in the ResultSet
            if (rs.next()) {
                int customerId = rs.getInt("CustomerID");
                System.out.println("Found existing customer with ID: " + customerId);

                // Fetch CustomerName with error handling and logging
                try {
                    String customerName = rs.getString("CustomerName");
                    System.out.println("CustomerName: " + customerName);
                } catch (SQLException e) {
                    System.err.println("Error accessing CustomerName: " + e.getMessage());
                    throw new SQLException("Error accessing CustomerName", e);
                }

                // Check if the customer details need updating
                boolean needsUpdate = !rs.getString("CustomerName").equals(name) ||
                        !rs.getString("Address").equals(address) ||
                        !rs.getString("PhoneNumber").equals(phone);

                if (needsUpdate) {
                    System.out.println("Updating existing customer details");
                    String updateQuery = "UPDATE Customer SET CustomerName = ?, Address = ?, PhoneNumber = ? WHERE CustomerID = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, name);
                        updateStmt.setString(2, address);
                        updateStmt.setString(3, phone);
                        updateStmt.setInt(4, customerId);
                        updateStmt.executeUpdate();
                        System.out.println("Customer details updated successfully");
                    }
                }
                return customerId; // Return the existing customer's ID
            } else {
                // Insert new customer if not found
                System.out.println("Inserting new customer");
                String insertQuery = "INSERT INTO Customer (CustomerName, Address, PhoneNumber, Email) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, name);
                    insertStmt.setString(2, address);
                    insertStmt.setString(3, phone);
                    insertStmt.setString(4, email);

                    System.out.println("Executing insert query: " + insertStmt.toString()); // Debug log
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        System.out.println("New customer inserted with ID: " + newId);
                        return newId; // Return the new customer ID
                    } else {
                        throw new SQLException("Failed to insert customer, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            // Log and re-throw any SQL errors
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            throw new SQLException("Error occurred while processing customer data", e); // Ensure proper exception
                                                                                        // propagation
        }
    }

    // Insert payment details into PaymentDetails table
    private int insertPaymentDetails(String paymentMethod) throws SQLException {
        String insertQuery = "INSERT INTO PaymentDetails (PaymentMethod, PayPalTransactionID, CreditCardNumber, CreditCardType, CreditCardExpirationDate, PaymentStatus, PaymentDate) VALUES (?, ?, ?, ?, ?, 'Completed', CURDATE())";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

        if (paymentMethod.equals("PayPal")) {
            insertStmt.setString(1, "PayPal");
            insertStmt.setString(2, view.getPayPalTransactionId());
            insertStmt.setNull(3, Types.VARCHAR);
            insertStmt.setNull(4, Types.VARCHAR);
            insertStmt.setNull(5, Types.VARCHAR);
        } else if (paymentMethod.equals("Credit Card")) {
            insertStmt.setString(1, "Credit Card");
            insertStmt.setNull(2, Types.VARCHAR);
            insertStmt.setString(3, view.getCreditCardNumber());
            insertStmt.setString(4, view.getCreditCardType());
            insertStmt.setString(5, view.getCreditCardExpirationDate());
        } else {
            insertStmt.setString(1, "Cash");
            insertStmt.setNull(2, Types.VARCHAR);
            insertStmt.setNull(3, Types.VARCHAR);
            insertStmt.setNull(4, Types.VARCHAR);
            insertStmt.setNull(5, Types.VARCHAR);
        }

        insertStmt.executeUpdate();
        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1); // Return the generated PaymentDetailID
        } else {
            throw new SQLException("Failed to insert payment details, no ID obtained.");
        }
    }

    // Insert order into Orders table and get the generated OrderID
    private int insertOrder(int customerId, int paymentDetailId) throws SQLException {
        String insertQuery = "INSERT INTO Orders (CustomerID, OrderDate, TotalCost, PaymentDetailID, PaymentDate) VALUES (?, CURDATE(), ?, ?, CURDATE())";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        insertStmt.setInt(1, customerId);
        insertStmt.setDouble(2, cart.getTotal());
        insertStmt.setInt(3, paymentDetailId);
        insertStmt.executeUpdate();

        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1); // Return the generated OrderID
        } else {
            throw new SQLException("Failed to insert order, no ID obtained.");
        }
    }

    // Insert order details into OrderDetail table
    private void insertOrderDetails(int orderId) throws SQLException {
        String insertQuery = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);

        for (CartItem item : cart.getItems()) {
            insertStmt.setInt(1, orderId); // Use the generated OrderID here
            insertStmt.setInt(2, item.getProductId());
            insertStmt.setInt(3, item.getQuantity());
            insertStmt.addBatch();
        }

        insertStmt.executeBatch(); // Execute all the inserts at once
    }

    // Cancel checkout and return to the shopping cart
    private void cancelCheckout() {
        ShoppingCartView cartView = new ShoppingCartView(cart.getItems(), cart.getTotal());
        new ShoppingCartController(cartView, cart, new ProductService(), buyerDashboardView, connection, orderService);
        view.dispose();
    }
}
