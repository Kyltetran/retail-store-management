package databasesystems.project2;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.ActionListener;

public class BuyerDashboardView extends JFrame {
    private JButton browseProductsButton;
    private JButton viewCartButton;
    private JButton trackOrdersButton;
    private JButton backToLoginButton; // New Button
    private Cart cart;
    private ProductService productService;
    private OrderService orderService;

    public BuyerDashboardView(OrderService orderService) {
        this.orderService = orderService;

        setTitle("Buyer Dashboard");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        // Center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        browseProductsButton = new JButton("Browse Products");
        browseProductsButton.setBounds(100, 50, 200, 25);
        panel.add(browseProductsButton);

        viewCartButton = new JButton("View Cart");
        viewCartButton.setBounds(100, 100, 200, 25);
        panel.add(viewCartButton);

        trackOrdersButton = new JButton("Track Orders");
        trackOrdersButton.setBounds(100, 150, 200, 25);
        panel.add(trackOrdersButton);

        // Add Back to Login Button
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setBounds(100, 200, 200, 25); // Position it after other buttons
        panel.add(backToLoginButton);
    }

    // Getter for OrderService
    public OrderService getOrderService() {
        return orderService;
    }

    // Event listeners
    public void addBrowseProductsListener(ActionListener listener) {
        browseProductsButton.addActionListener(listener);
    }

    public void addViewCartListener(ActionListener listener) {
        viewCartButton.addActionListener(listener);
    }

    public void addTrackOrdersListener(ActionListener listener) {
        trackOrdersButton.addActionListener(listener);
    }

    public void addBackToLoginListener(ActionListener listener) {
        backToLoginButton.addActionListener(listener);
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public Cart getCart() {
        return cart;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
