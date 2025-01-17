package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import javax.swing.JCheckBox;

public class ShoppingCartController {
    private ShoppingCartView view;
    private Cart cart;
    private ProductService productService; // Add reference to ProductService to fetch products
    private OrderService orderService;
    private BuyerDashboardView buyerDashboardView; // Reference to BuyerDashboardView
    private Connection connection;

    public ShoppingCartController(ShoppingCartView view, Cart cart, ProductService productService,
            BuyerDashboardView buyerDashboardView, Connection connection, OrderService orderService) {
        this.view = view;
        this.cart = cart;
        this.productService = productService;
        this.buyerDashboardView = buyerDashboardView;
        this.connection = connection;
        this.orderService = orderService;

        // Initialize the remove button functionality
        initializeRemoveButton();

        // Listener for main screen button
        this.view.addMainScreenListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMainScreen();
            }
        });

        // Listener for update cart button
        this.view.addBrowseProductsListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseProducts();
            }
        });

        // Listener for checkout button
        this.view.addCheckoutListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });

        // Listener for view orders button
        this.view.addViewOrdersListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Fetch orders
                    List<Order> orders = orderService.getAllOrders();

                    // Create the controller
                    OrderTrackingController controller = new OrderTrackingController(orders, buyerDashboardView, cart,
                            productService, connection, orderService);
                    // Create the view
                    OrderTrackingView orderTrackingView = new OrderTrackingView(orders, controller);
                    // Initialize the view in the controller
                    controller.initializeView(orderTrackingView);
                    // Show the view
                    orderTrackingView.setVisible(true);

                    // Dispose of the shopping cart view
                    view.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    view.showErrorMessage("Failed to retrieve orders: " + ex.getMessage());
                }
            }
        });

        // Listener for Remove All button
        this.view.addRemoveAllListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAllItems();
            }
        });
    }

    private void initializeRemoveButton() {
        view.getCartTable().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), e -> {
            int selectedRow = view.getCartTable().getSelectedRow();
            if (selectedRow >= 0) {
                // Stop the editing process to prevent ArrayIndexOutOfBoundsException
                if (view.getCartTable().isEditing()) {
                    view.getCartTable().getCellEditor().stopCellEditing();
                }

                CartItem item = cart.getItems().get(selectedRow);
                cart.removeItem(item); // Remove the item from the cart object
                view.getTableModel().removeRow(selectedRow); // Remove the item from the table

                // Update the total in the view
                view.updateTotal(cart.getTotal());

                // Reinitialize the ButtonEditor to ensure it works for the next row
                reinitializeRemoveButtonEditor();

                System.out.println("Item removed from row: " + selectedRow);
            }
        }));
    }

    // Reinitialize the ButtonEditor for all rows after removal to ensure proper
    // behavior
    private void reinitializeRemoveButtonEditor() {
        view.getCartTable().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), e -> {
            int selectedRow = view.getCartTable().getSelectedRow();
            if (selectedRow >= 0) {
                CartItem item = cart.getItems().get(selectedRow);
                cart.removeItem(item); // Remove the item from the cart object
                view.getTableModel().removeRow(selectedRow); // Remove the item from the table

                // Update the total in the view
                view.updateTotal(cart.getTotal());

                System.out.println("Item removed from row: " + selectedRow);
            }
        }));
    }

    // Logic for removing all items from the cart
    private void removeAllItems() {
        cart.clear(); // Clear all items in the cart
        view.getTableModel().setRowCount(0); // Clear all rows in the table
        view.updateTotal(0.00); // Reset the total to zero
        System.out.println("All items removed from the cart.");
    }

    private void backToMainScreen() {
        // Close the shopping cart view and go back to the buyer dashboard
        view.dispose();
        buyerDashboardView.setVisible(true);
    }

    private void browseProducts() {
        List<Product> products = productService.getAllProducts(); // Fetch products from ProductService
        ProductCatalogView catalogView = new ProductCatalogView(products);
        new ProductCatalogController(catalogView, cart, productService, buyerDashboardView, orderService);
        view.dispose(); // Close the Shopping Cart View
    }

    private void checkout() {
        if (cart.getItems().isEmpty()) {
            view.showErrorMessage("Your cart is empty!");
            return;
        }

        // Open the checkout page with the current items in the cart
        CheckoutPageView checkoutPageView = new CheckoutPageView(cart.getItems(), cart.getTotal());
        new CheckoutPageController(checkoutPageView, cart, buyerDashboardView, connection, orderService);
        view.dispose(); // Close the shopping cart view
    }

    private void viewOrders() {
        view.dispose();

        try {
            // Fetch orders using the OrderService
            List<Order> orders = orderService.getAllOrders();

            // Create the controller first
            OrderTrackingController controller = new OrderTrackingController(orders, buyerDashboardView, cart,
                    productService, connection, orderService);

            // Create the view, passing the controller to it
            OrderTrackingView orderTrackingView = new OrderTrackingView(orders, controller);

            // Initialize the view in the controller
            controller.initializeView(orderTrackingView);

            // Show the view
            orderTrackingView.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally show an error message to the user
            view.showErrorMessage("Failed to retrieve orders: " + e.getMessage());
        }
    }

}
