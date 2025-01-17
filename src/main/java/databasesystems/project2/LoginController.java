package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginController {
    private LoginScreen loginView;
    private UserAuthentication auth;

    public LoginController(LoginScreen loginView, UserAuthentication auth) {
        this.loginView = loginView;
        this.auth = auth;

        this.loginView.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser(); // Attempt to log in the user
            }
        });

        // Add listener for sign-up button (optional)
        this.loginView.addSignUpListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle sign-up logic (optional)
            }
        });
    }

    private void loginUser() {
        // Retrieve entered username and password from the login screen
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        try {
            // Attempt to authenticate the user
            User user = auth.login(username, password);

            // Close the login screen after successful login
            loginView.dispose();

            // Establish a connection for future database interactions
            Connection connection = MySQLConnection.getConnection();

            // Check the user's role and open the appropriate dashboard
            if ("buyer".equals(user.getRole())) {
                // Open Buyer Dashboard
                ProductService productService = new ProductService();
                Cart cart = new Cart(); // Create an empty cart for the buyer
                OrderService orderService = new OrderService(connection);

                BuyerDashboardView buyerDashboardView = new BuyerDashboardView(orderService);
                new BuyerDashboardController(buyerDashboardView, productService, cart, orderService, connection);

            } else if ("seller".equals(user.getRole())) {
                // Open Seller Dashboard
                SellerDashboardView sellerDashboardView = new SellerDashboardView(); // Initialize the Seller View
                SellerDashboardController sellerDashboardController = new SellerDashboardController(
                        sellerDashboardView); // Initialize the Seller Controller
            } else if ("manager".equals(user.getRole())) {
                // Open Manager Dashboard
                ManagerDashboardView managerDashboardView = new ManagerDashboardView();
                ManagerDashboardController managerDashboardController = new ManagerDashboardController(
                        managerDashboardView); // Initialize the Manager Controller
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Display error message on failed login (optional)
        }
    }
}
