package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.*;

public class OrderTrackingController {
    private OrderTrackingView view;
    private List<Order> confirmedOrders;
    private OrderService orderService;
    private BuyerDashboardView dashboardView;
    private Cart cart;
    private ProductService productService;
    private Connection connection;

    public OrderTrackingController(List<Order> confirmedOrders, BuyerDashboardView dashboardView, Cart cart,
            ProductService productService, Connection connection, OrderService orderService) {
        this.confirmedOrders = confirmedOrders;
        this.dashboardView = dashboardView;
        this.cart = cart;
        this.productService = productService;
        this.connection = connection;
        this.orderService = orderService;
    }

    // Initialize the view and set up listeners
    public void initializeView(OrderTrackingView view) {
        this.view = view;

        // Populate the table with orders
        this.view.populateTable(confirmedOrders);

        // Main screen button action
        this.view.addMainScreenListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainScreen();
            }
        });

        // Back to cart button action
        this.view.addBackToCartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCart();
            }
        });
    }

    private void showMainScreen() {
        dashboardView.setVisible(true);
        view.dispose(); // Close the order tracking view
    }

    private void showCart() {
        view.dispose();
        // Create a new ShoppingCartView and open it
        ShoppingCartView cartView = new ShoppingCartView(cart.getItems(), cart.getTotal());
        new ShoppingCartController(cartView, cart, productService, dashboardView, connection, orderService);
        // Make sure the new view is visible
        cartView.setVisible(true);
    }

    public void reorder(int orderId) {
        System.out.println("Reorder button clicked for OrderID: " + orderId);
        try {
            // Ensure the connection is open before performing any DB operations
            if (connection == null || connection.isClosed()) {
                connection = MySQLConnection.getConnection(); // Re-establish the connection if closed
            }

            // Fetch the order details for the selected order
            List<OrderDetail> orderDetails = orderService.getOrderDetails(orderId);

            // Add each item in the order back into the cart
            for (OrderDetail orderDetail : orderDetails) {
                Product product = productService.getProductById(orderDetail.getProductId());
                CartItem cartItem = new CartItem(product, orderDetail.getQuantity());
                cart.addItem(cartItem); // Add to the cart
            }

            // Optionally, show a message to the user
            System.out.println("Order has been added to the cart.");

            // Update the cart view if necessary
            ShoppingCartView cartView = new ShoppingCartView(cart.getItems(), cart.getTotal());
            new ShoppingCartController(cartView, cart, productService, dashboardView, connection, orderService);
            cartView.setVisible(true);
            view.dispose(); // Close the order tracking view

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Failed to reorder items: " + e.getMessage());
        }
    }

}