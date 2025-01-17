package databasesystems.project2;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;

public class SellerDashboardView extends JFrame {
    private JComboBox<String> actionDropdown;
    private JComboBox<String> tableDropdown;
    private JButton viewTableButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JPanel formPanel;
    private JButton backToLoginButton;

    private JTextField supplierNameField;
    private JTextField phoneNumberField;
    private JTextField addressField;
    private JTextField emailField;
    private JTextField contactPersonField;
    private JTextField quantityNewField;
    private JTextField locationNewField;
    private JTextField minimumStockField;
    private JTextField maximumStockField;

    private JTextField orderIdField;
    private JComboBox<String> orderStatusComboBox;
    private JButton updateOrderStatusButton;

    private JTextField productNameField;
    private JTextField categoryField;
    private JTextField sellingPriceField;
    private JTextField supplyPriceField;
    private JTextField supplierField;

    private JTextField productField;
    private JTextField quantityField;

    public SellerDashboardView() {
        // Initialize the frame
        setTitle("Seller Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel layout divided into two parts: left for actions (form), right for
        // table
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Action panel (left side)
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(new GridLayout(10, 2, 10, 10)); // Placeholders for consistent layout
        JPanel optionsPanel = new JPanel(); // For selecting actions

        // Dropdown to select different actions for form updates
        String[] actions = { "Select Action", "Manage Products", "Add New Products", "Update Products", "Add Suppliers",
                "Process Orders" };
        actionDropdown = new JComboBox<>(actions);
        actionDropdown.addActionListener(e -> onActionSelected((String) actionDropdown.getSelectedItem(), formPanel)); // Handle
                                                                                                                       // dynamic
                                                                                                                       // form
                                                                                                                       // update
        optionsPanel.add(new JLabel("Select Action:"));
        optionsPanel.add(actionDropdown);

        // Add "Back to Login" button
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.addActionListener(e -> backToLogin()); // Handle back to login functionality
        optionsPanel.add(backToLoginButton);

        actionPanel.add(optionsPanel, BorderLayout.NORTH);
        actionPanel.add(formPanel, BorderLayout.CENTER);

        // Table panel (right side)
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tableModel = new DefaultTableModel(); // Initialize table model
        dataTable = new JTable(tableModel); // Set the model to the table
        JScrollPane scrollPane = new JScrollPane(dataTable);

        // Dropdown for selecting and viewing tables
        JPanel tableOptionsPanel = new JPanel();
        String[] tables = { "Select Table", "Products", "Category", "Inventory", "Suppliers", "Orders", "Order Details",
                "Customers", "Payment Details" };
        tableDropdown = new JComboBox<>(tables);
        viewTableButton = new JButton("View Table");

        // Ensure this only updates the table and does not affect the form
        viewTableButton.addActionListener(e -> {
            String selectedTable = (String) tableDropdown.getSelectedItem();
            if (selectedTable != null && !selectedTable.equals("Select Table")) {
                viewSelectedTable(selectedTable); // This will only update the right-hand table
            }
        });

        // Add the dropdown and button to the options panel for table selection
        tableOptionsPanel.add(new JLabel("Select Table:"));
        tableOptionsPanel.add(tableDropdown);
        tableOptionsPanel.add(viewTableButton);

        // Add the tableOptionsPanel to the tablePanel
        tablePanel.add(tableOptionsPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to the main frame
        mainPanel.add(actionPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    // Method to add action listeners to the view
    public void addActionListener(ActionListener listener) {
        viewTableButton.addActionListener(listener); // Attach listener to the button
    }

    // Method to return selected action from the dropdown
    public String getSelectedAction() {
        return (String) actionDropdown.getSelectedItem();
    }

    // Method to return selected table from the dropdown
    public String getSelectedTable() {
        return (String) tableDropdown.getSelectedItem();
    }

    // Make viewSelectedTable method public so the controller can access it
    public void viewSelectedTable(String selectedTable) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = MySQLConnection.getConnection(); // Establish database connection
            stmt = connection.createStatement();

            switch (selectedTable) {
                case "Products":
                    rs = stmt.executeQuery("SELECT * FROM Product");
                    displayTable(rs, new String[] { "Product ID", "Product Name", "Selling Price", "Quantity In Stock",
                            "Supplier ID", "Supply Price", "Category ID" });
                    break;
                case "Category":
                    rs = stmt.executeQuery("SELECT * FROM Category");
                    displayTable(rs, new String[] { "Category ID", "Description" });
                    break;
                case "Inventory":
                    rs = stmt.executeQuery("SELECT * FROM Inventory");
                    displayTable(rs, new String[] { "Inventory ID", "Product ID", "Quantity", "Location",
                            "Last Restock Date", "Minimum Stock", "Maximum Stock" });
                    break;
                case "Suppliers":
                    rs = stmt.executeQuery("SELECT * FROM Supplier");
                    displayTable(rs, new String[] { "Supplier ID", "Supplier Name", "Phone Number", "Address", "Email",
                            "Contact Person" });
                    break;
                case "Orders":
                    rs = stmt.executeQuery("SELECT * FROM Orders");
                    displayTable(rs, new String[] { "Order ID", "Customer ID", "Order Date", "Total Cost",
                            "Payment Detail ID", "Payment Date", "Order Status" });
                    break;
                case "Order Details":
                    rs = stmt.executeQuery("SELECT * FROM OrderDetail");
                    displayTable(rs, new String[] { "Order ID", "Product ID", "Quantity" });
                    break;
                case "Customers":
                    rs = stmt.executeQuery("SELECT * FROM Customer");
                    displayTable(rs,
                            new String[] { "Customer ID", "Customer Name", "Address", "Phone Number", "Email" });
                    break;
                case "Payment Details":
                    rs = stmt.executeQuery("SELECT * FROM PaymentDetails");
                    displayTable(rs, new String[] { "Payment Detail ID", "Payment Method", "PayPal Transaction ID",
                            "Credit Card Number", "Credit Card Type", "Payment Date", "Payment Status" });
                    break;
                default:
                    clearTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to populate the JTable with data from ResultSet
    private void displayTable(ResultSet rs, String[] columnNames) throws SQLException {
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0); // Clear existing rows

        // Populate rows from ResultSet
        while (rs.next()) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                rowData[i] = rs.getObject(i + 1);
            }
            tableModel.addRow(rowData);
        }
    }

    // Clear the JTable content
    public void clearTable() {
        tableModel.setRowCount(0); // Clears the table content only
    }

    // Method to return formPanel to the controller
    public JPanel getFormPanel() {
        return formPanel;
    }

    // Method to handle the action selected and update the form panel accordingly.
    public void onActionSelected(String selectedAction, JPanel formPanel) {
        // Clear the existing form components
        formPanel.removeAll();

        // Update form components based on selected action
        switch (selectedAction) {
            case "Manage Products":
                showManageProductsForm(formPanel);
                break;

            case "Add New Products":
                showAddNewProductsForm(formPanel);
                break;

            case "Update Products":
                showUpdateProductsForm(formPanel);
                break;

            case "Add Suppliers":
                showAddSuppliersForm(formPanel);
                break;

            case "Process Orders":
                showProcessOrdersForm(formPanel);
                break;

            default:
                formPanel.add(new JLabel("Please select an action."));
                break;
        }

        formPanel.revalidate(); // Refresh to show changes
        formPanel.repaint();
    }

    // Method to handle "Manage Products"
    private void showManageProductsForm(JPanel formPanel) {
        // First row - Category
        JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.add(new JLabel("Category:"));
        JComboBox<String> categoryComboBox = new JComboBox<>(
                new String[] { "All", "Electronics", "Mobile Devices", "Accessories" });
        categoryPanel.add(categoryComboBox);
        firstRowPanel.add(categoryPanel);
        formPanel.add(firstRowPanel);

        // Second row - Price Range
        JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.add(new JLabel("Price Range:"));
        JTextField minPriceField = new JTextField(5);
        JTextField maxPriceField = new JTextField(5);
        pricePanel.add(minPriceField);
        pricePanel.add(new JLabel(" to "));
        pricePanel.add(maxPriceField);
        secondRowPanel.add(pricePanel);
        formPanel.add(secondRowPanel);

        // Third row - Keyword Search
        JPanel thirdRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel keywordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keywordPanel.add(new JLabel("Keywords:"));
        JTextField searchField = new JTextField(10);
        keywordPanel.add(searchField);
        thirdRowPanel.add(keywordPanel);
        formPanel.add(thirdRowPanel);

        // Third row - Search Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton searchButton = new JButton("Search");
        buttonPanel.add(searchButton);
        formPanel.add(buttonPanel);

        // Add search button functionality
        searchButton.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String minPrice = minPriceField.getText().trim();
            String maxPrice = maxPriceField.getText().trim();
            String keywords = searchField.getText().trim();

            // Call the search method with the provided filters
            searchProducts(selectedCategory, minPrice, maxPrice, keywords);
        });
    }

    // Method to handle "Add New Products"
    private void showAddNewProductsForm(JPanel formPanel) {
        // Initialize fields only if they are null, so they don't get reset
        if (productNameField == null) {
            productNameField = new JTextField(10);
            categoryField = new JTextField(10);
            sellingPriceField = new JTextField(10);
            supplyPriceField = new JTextField(10);
            supplierField = new JTextField(10);
            quantityNewField = new JTextField(10);
            locationNewField = new JTextField(10);
            minimumStockField = new JTextField(10);
            maximumStockField = new JTextField(10);
        }

        // Add the fields to the form panel (same as before)
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(productNameField);

        formPanel.add(new JLabel("Category ID/ Description:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Selling Price:"));
        formPanel.add(sellingPriceField);

        formPanel.add(new JLabel("Supply Price:"));
        formPanel.add(supplyPriceField);

        formPanel.add(new JLabel("Supplier Name/ ID:"));
        formPanel.add(supplierField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityNewField);

        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationNewField);

        formPanel.add(new JLabel("Minimum Stock:"));
        formPanel.add(minimumStockField);

        formPanel.add(new JLabel("Maximum Stock:"));
        formPanel.add(maximumStockField);

        JButton addProductButton = new JButton("Add Product");
        formPanel.add(addProductButton);

        // Add button functionality (no changes here)
        addProductButton.addActionListener(e -> {
            String productName = productNameField.getText().trim();
            String categoryInput = categoryField.getText().trim();
            int categoryId = categoryInput.matches("\\d+") ? Integer.parseInt(categoryInput)
                    : fetchCategoryIdByName(categoryInput);
            double sellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
            double supplyPrice = Double.parseDouble(supplyPriceField.getText().trim());
            String supplierInput = supplierField.getText().trim();
            int supplierId = supplierInput.matches("\\d+") ? Integer.parseInt(supplierInput)
                    : fetchSupplierIdByName(supplierInput);
            int quantity = Integer.parseInt(quantityNewField.getText().trim());
            String location = locationNewField.getText().trim();
            int minStock = Integer.parseInt(minimumStockField.getText().trim());
            int maxStock = Integer.parseInt(maximumStockField.getText().trim());

            if (categoryId != -1 && supplierId != -1) {
                addNewProduct(productName, categoryId, sellingPrice, supplyPrice, supplierId, quantity, location,
                        minStock, maxStock);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid category or supplier.");
            }
        });
    }

    // Method to handle "Update Products"
    private void showUpdateProductsForm(JPanel formPanel) {
        // Initialize fields only if they are null
        if (productField == null) {
            productField = new JTextField(10);
            quantityField = new JTextField(5);
        }

        formPanel.add(new JLabel("Product ID/Name:"));
        formPanel.add(productField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);

        JButton updateButton = new JButton("Update Inventory");
        formPanel.add(updateButton);

        // Add ActionListener for update functionality
        updateButton.addActionListener(e -> {
            String productIDOrName = productField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            updateProductAndInventory(productIDOrName, quantity);
        });
    }

    // Method to handle "Add Suppliers"
    private void showAddSuppliersForm(JPanel formPanel) {
        // Initialize fields only if they are null
        if (supplierNameField == null) {
            supplierNameField = new JTextField(10);
            phoneNumberField = new JTextField(10);
            addressField = new JTextField(10);
            emailField = new JTextField(10);
            contactPersonField = new JTextField(10);
        }

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);

        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneNumberField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(contactPersonField);

        JButton addSupplierButton = new JButton("Add Supplier");
        formPanel.add(addSupplierButton);

        // Add button functionality
        addSupplierButton.addActionListener(e -> {
            String supplierName = supplierNameField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();
            String contactPerson = contactPersonField.getText().trim();
            addSupplierToDatabase(supplierName, phoneNumber, address, email, contactPerson);
        });
    }

    // Method to handle "Process Orders"
    private void showProcessOrdersForm(JPanel formPanel) {
        // Initialize fields only if they are null
        if (orderIdField == null) {
            orderIdField = new JTextField(10);
            String[] orderStatusOptions = { "Pending", "Preparing", "Delivering", "Delivered" };
            orderStatusComboBox = new JComboBox<>(orderStatusOptions);
        }

        formPanel.add(new JLabel("Order ID:"));
        formPanel.add(orderIdField);

        formPanel.add(new JLabel("Order Status:"));
        formPanel.add(orderStatusComboBox);

        JButton updateOrderStatusButton = new JButton("Update Order Status");
        formPanel.add(updateOrderStatusButton);

        // Add button functionality
        updateOrderStatusButton.addActionListener(e -> {
            try {
                int orderId = Integer.parseInt(orderIdField.getText().trim());
                String newStatus = (String) orderStatusComboBox.getSelectedItem();
                updateOrderStatus(orderId, newStatus);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid Order ID.");
            }
        });
    }

    public void searchProducts(String category, String minPrice, String maxPrice, String keywords) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = MySQLConnection.getConnection();
            stmt = connection.createStatement();

            // Build the SQL query dynamically based on the filters
            String query = "SELECT ProductID, ProductName, SellingPrice, QuantityInStock, SupplierID FROM Product WHERE 1=1";

            if (!category.equals("All")) {
                query += " AND CategoryID = (SELECT CategoryID FROM Category WHERE Description = '" + category + "')";
            }

            if (!minPrice.isEmpty()) {
                query += " AND SellingPrice >= " + minPrice;
            }

            if (!maxPrice.isEmpty()) {
                query += " AND SellingPrice <= " + maxPrice;
            }

            if (!keywords.isEmpty()) {
                query += " AND ProductName LIKE '%" + keywords + "%'";
            }

            rs = stmt.executeQuery(query);

            // Display the filtered products in the table
            displayTable(rs,
                    new String[] { "Product ID", "Product Name", "Selling Price", "Quantity In Stock", "Supplier ID" });

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNewProduct(String productName, int categoryId, double sellingPrice, double supplyPrice,
            int supplierId, int quantity, String location, int minStock, int maxStock) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = MySQLConnection.getConnection(); // Establish database connection
            String insertSQL = "INSERT INTO Product (ProductName, CategoryID, SellingPrice, SupplyPrice, SupplierID, QuantityInStock) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)"; // Also insert initial stock

            pstmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, productName);
            pstmt.setInt(2, categoryId); // Insert the CategoryID here
            pstmt.setDouble(3, sellingPrice);
            pstmt.setDouble(4, supplyPrice);
            pstmt.setInt(5, supplierId); // Insert the SupplierID here
            pstmt.setInt(6, quantity); // Set initial quantity in stock

            int rowsAffected = pstmt.executeUpdate(); // Insert the product

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1);

                    // Now insert into the Inventory table for the new product
                    String insertInventorySQL = "INSERT INTO Inventory (ProductID, Quantity, Location, MinimumStock, MaximumStock, LastRestockDate) "
                            +
                            "VALUES (?, ?, ?, ?, ?, CURDATE())"; // Set restock date to today
                    PreparedStatement inventoryStmt = connection.prepareStatement(insertInventorySQL);
                    inventoryStmt.setInt(1, productId);
                    inventoryStmt.setInt(2, quantity);
                    inventoryStmt.setString(3, location);
                    inventoryStmt.setInt(4, minStock);
                    inventoryStmt.setInt(5, maxStock);

                    inventoryStmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product and inventory added successfully!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProductAndInventory(String productIDOrName, int quantity) {
        Connection connection = null;
        PreparedStatement updateProductStmt = null;
        PreparedStatement insertInventoryStmt = null;
        PreparedStatement productIDStmt = null;
        ResultSet rs = null;
        int productID = -1;

        try {
            connection = MySQLConnection.getConnection(); // Establish database connection

            // Check if input is numeric (ProductID) or a string (ProductName)
            boolean isNumeric = productIDOrName.matches("\\d+");

            // If the input is not numeric, we need to find the ProductID using the
            // ProductName
            if (!isNumeric) {
                String productIDQuery = "SELECT ProductID FROM Product WHERE ProductName = ?";
                productIDStmt = connection.prepareStatement(productIDQuery);
                productIDStmt.setString(1, productIDOrName); // Set the ProductName
                rs = productIDStmt.executeQuery();

                if (rs.next()) {
                    productID = rs.getInt("ProductID"); // Retrieve the ProductID
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found!");
                    return;
                }
            } else {
                // If the input is numeric, it's the ProductID
                productID = Integer.parseInt(productIDOrName);
            }

            // 1. Update the QuantityInStock in the Product table
            String updateProductSQL = "UPDATE Product SET QuantityInStock = QuantityInStock + ? WHERE ProductID = ?";
            updateProductStmt = connection.prepareStatement(updateProductSQL);
            updateProductStmt.setInt(1, quantity); // Set the quantity to increase
            updateProductStmt.setInt(2, productID); // Set the ProductID

            int rowsAffected = updateProductStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Product stock updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found!");
                return;
            }

            // 2. Insert a new row into the Inventory table for the restock with today's
            // date
            String insertInventorySQL = "INSERT INTO Inventory (ProductID, Quantity, Location, LastRestockDate, MinimumStock, MaximumStock) "
                    +
                    "SELECT ?, ?, Location, CURDATE(), MinimumStock, MaximumStock FROM Inventory WHERE ProductID = ? LIMIT 1";

            insertInventoryStmt = connection.prepareStatement(insertInventorySQL);
            insertInventoryStmt.setInt(1, productID); // Set the ProductID
            insertInventoryStmt.setInt(2, quantity); // Set the quantity for restock
            insertInventoryStmt.setInt(3, productID); // Use ProductID for selecting location, min/max stock

            rowsAffected = insertInventoryStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Inventory updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update inventory!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (productIDStmt != null)
                    productIDStmt.close();
                if (updateProductStmt != null)
                    updateProductStmt.close();
                if (insertInventoryStmt != null)
                    insertInventoryStmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addSupplierToDatabase(String supplierName, String phoneNumber, String email, String address,
            String contactPerson) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = MySQLConnection.getConnection(); // Establish database connection
            String insertSQL = "INSERT INTO Supplier (SupplierName, PhoneNumber, Address, Email, ContactPerson) VALUES (?, ?, ?, ?, ?)";

            pstmt = connection.prepareStatement(insertSQL);
            pstmt.setString(1, supplierName);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, address);
            pstmt.setString(4, email);
            pstmt.setString(5, contactPerson);

            int rowsAffected = pstmt.executeUpdate(); // Execute the query
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add supplier. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int fetchCategoryIdByName(String categoryName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int categoryId = -1; // Default to -1 if not found

        try {
            connection = MySQLConnection.getConnection();
            String query = "SELECT CategoryID FROM Category WHERE Description = ?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, categoryName); // Use the category name

            rs = pstmt.executeQuery();
            if (rs.next()) {
                categoryId = rs.getInt("CategoryID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return categoryId; // Return the fetched CategoryID or -1 if not found
    }

    public int fetchSupplierIdByName(String supplierName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int supplierId = -1; // Default to -1 if not found

        try {
            connection = MySQLConnection.getConnection();
            String query = "SELECT SupplierID FROM Supplier WHERE SupplierName = ?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, supplierName);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                supplierId = rs.getInt("SupplierID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return supplierId; // Return the fetched SupplierID or -1 if not found
    }

    private void updateOrderStatus(int orderId, String newStatus) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = MySQLConnection.getConnection(); // Establish database connection
            String updateSQL = "UPDATE Orders SET OrderStatus = ? WHERE OrderID = ?";
            pstmt = connection.prepareStatement(updateSQL);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Order status updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Order not found. Please check the Order ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order status: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to handle "Back to Login" functionality
    private void backToLogin() {
        // Close the current seller dashboard window
        this.dispose();

        Connection connection = null;
        try {
            // Attempt to establish a database connection
            connection = MySQLConnection.getConnection();
            if (connection != null) {
                // Create an instance of UserAuthentication using the database connection
                UserAuthentication auth = new UserAuthentication(connection);

                // Initialize the login screen
                LoginScreen loginScreen = new LoginScreen();

                // Attach the LoginController to the login screen and pass the authentication
                // object
                new LoginController(loginScreen, auth);

                // Make the login screen visible
                loginScreen.setVisible(true);
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            // Handle SQL exception, display an error message, and print the stack trace for
            // debugging
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure the connection is closed to avoid resource leaks
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close the connection: " + e.getMessage());
                }
            }
        }
    }

}
