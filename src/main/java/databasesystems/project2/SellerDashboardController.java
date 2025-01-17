package databasesystems.project2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SellerDashboardController implements ActionListener {
    private SellerDashboardView view;

    public SellerDashboardController(SellerDashboardView view) {
        this.view = view;
        this.view.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = view.getSelectedAction(); // Retrieve selected action from dropdown
        String table = view.getSelectedTable(); // Retrieve selected table from dropdown

        // Handle form updates based on the selected action
        switch (action) {
            case "Manage Products":
                handleProductManagement(); // Form and management logic for Product Management
                break;
            case "Add New Products":
                handleAddNewProducts();
                break;
            case "Update Products":
                handleInventoryManagement(); // Form and management logic for Inventory Management
                break;
            case "Add Suppliers":
                handleSupplierManagement(); // Form and management logic for Supplier Management
                break;
            case "Process Orders":
                handleOrderProcessing(); // Form and management logic for Order Processing
                break;
            default:
                view.clearTable();
        }

        // Handle table view updates based on the selected table
        if (table != null && !table.equals("Select Table")) {
            viewSelectedTable(table);
        }
    }

    /**
     * This method handles viewing and displaying the selected table in the view.
     */
    private void viewSelectedTable(String selectedTable) {
        switch (selectedTable) {
            case "Products":
                view.viewSelectedTable("Products");
            case "Category":
                view.viewSelectedTable("Category");
                break;
            case "Inventory":
                view.viewSelectedTable("Inventory");
                break;
            case "Suppliers":
                view.viewSelectedTable("Suppliers");
                break;
            case "Orders":
                view.viewSelectedTable("Orders");
                break;
            case "Order Details":
                view.viewSelectedTable("Order Details");
                break;
            case "Customers":
                view.viewSelectedTable("Customers");
                break;
            case "Payment Details":
                view.viewSelectedTable("Payment Details");
                break;
            default:
                view.clearTable(); // Clear table if no valid selection
                break;
        }
    }

    /**
     * Handles form display for Product Management.
     */
    private void handleProductManagement() {
        // Logic to update the form and handle product management actions
        System.out.println("Handling Product Management...");
        view.onActionSelected("Product Management", view.getFormPanel());
    }

    private void handleAddNewProducts() {
        view.onActionSelected("Add New Products", view.getFormPanel());
    }

    /**
     * Handles form display for Inventory Management.
     */
    private void handleInventoryManagement() {
        // Logic to update the form and handle inventory management actions
        System.out.println("Handling Inventory Management...");
        view.onActionSelected("Inventory Management", view.getFormPanel());
    }

    /**
     * Handles form display for Supplier Management.
     */
    private void handleSupplierManagement() {
        // Logic to update the form and handle supplier management actions
        System.out.println("Handling Supplier Management...");
        view.onActionSelected("Supplier Management", view.getFormPanel());
    }

    /**
     * Handles form display for Order Processing.
     */
    private void handleOrderProcessing() {
        // Logic to update the form and handle order processing actions
        System.out.println("Handling Order Processing...");
        view.onActionSelected("Order Processing", view.getFormPanel());
    }
}
