package databasesystems.project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductCatalogView extends JFrame {
    private JTable productTable;
    private JButton mainScreenButton;
    private JButton viewCartButton;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JButton searchButton;
    private JButton viewReviewsButton;

    private ReviewController reviewController;
    private List<Product> products;

    public ProductCatalogView(List<Product> products) {
        this.products = products;
        setTitle("Product Catalog");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize ReviewController
        reviewController = new ReviewController();

        // Create a main panel for all components
        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Create and set up the search/filter panel with a GridLayout (3 rows, 1
        // column)
        JPanel filterPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // 3 rows, 1 column for compact layout
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // First row - Category and Price Range in one panel
        JPanel firstRowPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 1 row, 2 columns for Category and Price
                                                                         // Range

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>(new String[] { "All", "Electronics", "Mobile Devices", "Accessories" });
        categoryPanel.add(categoryComboBox);
        firstRowPanel.add(categoryPanel);

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.add(new JLabel("Price Range:"));
        minPriceField = new JTextField(5);
        maxPriceField = new JTextField(5);
        pricePanel.add(minPriceField);
        pricePanel.add(new JLabel(" to "));
        pricePanel.add(maxPriceField);
        firstRowPanel.add(pricePanel);

        filterPanel.add(firstRowPanel);

        // Second row - Keyword Search in one line
        JPanel keywordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keywordPanel.add(new JLabel("Keywords:"));
        searchField = new JTextField(10);
        keywordPanel.add(searchField);
        filterPanel.add(keywordPanel);

        // Third row - Search Button centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button in its own panel
        searchButton = new JButton("Search");
        buttonPanel.add(searchButton);
        filterPanel.add(buttonPanel);

        // Add the filter panel to the main panel
        panel.add(filterPanel, BorderLayout.NORTH);

        // Table model for product table
        String[] columnNames = { "Product", "Price", "Stock", "Category", "Add to Cart" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Populate table model with product data (excluding ProductID)
        for (Product product : products) {
            Object[] rowData = {
                    product.getName(),
                    String.format("%.2f", product.getPrice()),
                    product.getStock(),
                    product.getCategory(),
                    "Add to Cart" // Placeholder for buttons
            };
            tableModel.addRow(rowData);
        }

        // Create JTable for displaying products
        productTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only allow the "Add to Cart" column to be editable
            }
        };

        // Add buttons to "Add to Cart" column
        ActionListener addToCartListener = e -> {
            int row = productTable.getSelectedRow();
            String productName = (String) productTable.getValueAt(row, 0);
            JOptionPane.showMessageDialog(null, "Added " + productName + " to cart.");
        };

        productTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        productTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), addToCartListener));

        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add buttons for View Reviews
        // viewReviewsButton = new JButton("View Reviews");

        // Add buttons for main screen and view cart
        JPanel bottomPanel = new JPanel();
        mainScreenButton = new JButton("Main Screen");
        viewReviewsButton = new JButton("View Reviews");
        viewCartButton = new JButton("View Cart");
        bottomPanel.add(mainScreenButton);
        bottomPanel.add(viewReviewsButton);
        bottomPanel.add(viewCartButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // View Reviews button action listener
        viewReviewsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected product ID, assuming you're using the product table
                int selectedRow = productTable.getSelectedRow();

                if (selectedRow != -1) {
                    // Assuming ProductID is the first column in your table
                    String productId = productTable.getValueAt(selectedRow, 0).toString(); // This should be the ID, not
                                                                                           // the name
                    displayProductReviews(productId);
                } else {
                    JOptionPane.showMessageDialog(ProductCatalogView.this, "Please select a product first.",
                            "No Product Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    // Method to display reviews for the selected product
    private void displayProductReviews(String productName) {
        // Query the products collection to get the ProductID for the given ProductName
        int productId = getProductIdByName(productName);

        if (productId != -1) {
            // Instantiate the ReviewController to fetch reviews
            ReviewController reviewController = new ReviewController();
            List<Review> reviews = reviewController.getReviewsForProduct(productId);

            if (reviews.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No reviews found for this product.", "No Reviews",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Show the reviews in a new view or dialog
                new ReviewView(reviews, productId).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Product not found.", "Invalid Product", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to find ProductID based on Product Name
    private int getProductIdByName(String productName) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(productName)) {
                return product.getProductId(); // Return the ProductID for the matching product
            }
        }
        return -1; // Return -1 if product is not found
    }

    // Getter methods for components
    public String getSearchKeyword() {
        return searchField.getText();
    }

    public String getSelectedCategory() {
        return (String) categoryComboBox.getSelectedItem();
    }

    public String getMinPrice() {
        return minPriceField.getText();
    }

    public String getMaxPrice() {
        return maxPriceField.getText();
    }

    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addMainScreenListener(ActionListener listener) {
        mainScreenButton.addActionListener(listener);
    }

    public void addViewCartListener(ActionListener listener) {
        viewCartButton.addActionListener(listener);
    }

    // Populate table with product data
    public void populateTable(List<Product> products, DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows

        for (Product product : products) {
            String categoryName = getCategoryName(product.getCategory()); // Convert category ID to name
            Object[] rowData = {
                    product.getName(), // Product name
                    String.format("%.2f", product.getPrice()), // Product price
                    product.getStock(), // Stock quantity
                    categoryName, // Category name (converted)
                    "Add to Cart" // Placeholder for button
            };
            model.addRow(rowData);
        }
    }

    // Method to get the category name based on category ID
    private String getCategoryName(int categoryID) {
        switch (categoryID) {
            case 1:
                return "Electronics";
            case 2:
                return "Mobile Devices";
            case 3:
                return "Accessories";
            default:
                return "Unknown"; // Fallback for unknown categories
        }
    }

    public JTable getProductTable() {
        return productTable;
    }
}
