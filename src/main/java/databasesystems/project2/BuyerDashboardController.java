package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class BuyerDashboardController {
    private BuyerDashboardView view;
    private ProductService productService;
    private OrderService orderService;
    private Cart cart;
    private Connection connection;

    private OrderTrackingView orderTrackingView;

    public BuyerDashboardController(BuyerDashboardView view, ProductService productService, Cart cart,
            OrderService orderService, Connection connection) {
        this.view = view;
        this.productService = productService;
        this.cart = cart;
        this.orderService = orderService;
        this.connection = connection;

        // Adding action listeners for buttons
        this.view.addBrowseProductsListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProductCatalog();
            }
        });

        this.view.addViewCartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCart();
            }
        });

        this.view.addTrackOrdersListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Fetch recent orders (or none if none placed today)
                    List<Order> orders = orderService.getAllOrders();

                    // Check if the orders list is empty
                    if (orders.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "You have no new orders.", "No Orders",
                                JOptionPane.INFORMATION_MESSAGE);
                        return; // Do not proceed to OrderTrackingView
                    }

                    // If there are orders, proceed to the OrderTrackingView
                    OrderTrackingController controller = new OrderTrackingController(orders, view, cart, productService,
                            connection, orderService);
                    OrderTrackingView orderTrackingView = new OrderTrackingView(orders, controller);
                    controller.initializeView(orderTrackingView);
                    orderTrackingView.setVisible(true);
                    view.dispose();
                } catch (SQLException ex) {
                    System.err.println("SQL Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Handle "Back to Login" action
        this.view.addBackToLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin(); // Call the method to return to the login screen
            }
        });
    }

    private void showProductCatalog() {
        List<Product> products = productService.getAllProducts();
        ProductCatalogView catalogView = new ProductCatalogView(products);
        new ProductCatalogController(catalogView, cart, productService, view, orderService);
        view.dispose();
    }

    // Show the shopping cart
    private void showCart() {
        // Open the ShoppingCartView with the current items in the cart
        ShoppingCartView cartView = new ShoppingCartView(cart.getItems(), cart.getTotal());
        new ShoppingCartController(cartView, cart, productService, view, connection, orderService);
        view.dispose();
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

    public Cart getCart() {
        return this.cart; // assuming `cart` is a field in BuyerDashboardView
    }

    public ProductService getProductService() {
        return this.productService; // assuming `productService` is a field in BuyerDashboardView
    }

    private void viewOrders() {
        try {
            // Check if the view is already open or has been disposed of
            if (connection == null || connection.isClosed()) {
                connection = MySQLConnection.getConnection(); // Re-establish the connection
            }
            if (orderTrackingView == null || !orderTrackingView.isDisplayable()) {
                List<Order> orders = orderService.getAllOrders();

                // Create controller and view instances
                OrderTrackingController controller = new OrderTrackingController(orders, view, cart, productService,
                        connection, orderService);
                orderTrackingView = new OrderTrackingView(orders, controller);

                // Set a listener to reset the reference when the window closes
                orderTrackingView.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        orderTrackingView = null; // Reset the reference in the controller
                    }
                });

                controller.initializeView(orderTrackingView);
            }

            orderTrackingView.setVisible(true); // Show the existing or newly created view
            view.dispose(); // Optionally close or hide the buyer dashboard

        } catch (SQLException e) {
            view.showErrorMessage("Failed to retrieve orders: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
