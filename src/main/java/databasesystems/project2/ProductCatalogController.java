package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;

public class ProductCatalogController {
    private ProductCatalogView view;
    private Cart cart; // Cart to store selected products
    private ProductService productService;
    private BuyerDashboardView buyerDashboardView; // Reference to BuyerDashboardView
    private List<Product> products; // Available products
    private Connection connection;
    private OrderService orderService;

    public ProductCatalogController(ProductCatalogView view, Cart cart, ProductService productService,
            BuyerDashboardView buyerDashboardView, OrderService orderService) {
        this.view = view;
        this.cart = cart;
        this.buyerDashboardView = buyerDashboardView;
        this.productService = productService;
        this.orderService = orderService;
        this.products = productService.getAllProducts();
        view.populateTable(this.products, (DefaultTableModel) view.getProductTable().getModel());

        // Add listener for the "Add to Cart" button on each product
        view.getProductTable().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), e -> {
            int selectedRow = view.getProductTable().getSelectedRow();
            if (selectedRow >= 0) {
                Product product = products.get(selectedRow);
                cart.addItem(new CartItem(product, 1)); // Add 1 quantity of the selected product to cart

                // Show success message
                JOptionPane.showMessageDialog(null, product.getName() + " added to cart.");
            }
        }));

        // Search button action listener
        view.addSearchListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });

        // Listener for "View Cart" button
        this.view.addViewCartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCart();
            }
        });

        // Listener for "Main Screen" button
        this.view.addMainScreenListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMainScreen();
            }
        });
    }

    // Filter products based on user input, including ProductID
    private void filterProducts() {
        String selectedCategory = view.getSelectedCategory(); // Get selected category
        String minPrice = view.getMinPrice(); // Get min price
        String maxPrice = view.getMaxPrice(); // Get max price
        String keyword = view.getSearchKeyword(); // Get search keyword

        // Map category names back to category IDs for filtering
        int categoryId = 0;
        switch (selectedCategory) {
            case "Electronics":
                categoryId = 1;
                break;
            case "Mobile Devices":
                categoryId = 2;
                break;
            case "Accessories":
                categoryId = 3;
                break;
            default:
                categoryId = 0; // 0 means all categories
        }

        // Get the filtered products from the ProductService
        List<Product> filteredProducts = productService.getFilteredProducts(categoryId, minPrice, maxPrice, keyword);

        // Update the table with the filtered products
        view.populateTable(filteredProducts, (DefaultTableModel) view.getProductTable().getModel());
    }

    // Open the shopping cart view
    private void openCart() {
        ShoppingCartView cartView = new ShoppingCartView(cart.getItems(), cart.getTotal());
        new ShoppingCartController(cartView, cart, productService, buyerDashboardView, connection, orderService);
        view.dispose(); // Close the product catalog view
    }

    private void backToMainScreen() {
        // Close the current catalog view and go back to the buyer dashboard
        view.dispose();
        buyerDashboardView.setVisible(true);
    }
}
