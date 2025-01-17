package databasesystems.project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ShoppingCartView extends JFrame {
    private JTable cartTable;
    private JButton mainScreenButton;
    private JButton browseProductsButton;
    private JButton checkoutButton;
    private JButton viewOrdersButton;
    private JButton removeAllButton;
    private JLabel totalLabel;
    private DefaultTableModel tableModel;

    public ShoppingCartView(List<CartItem> cartItems, double totalAmount) {
        setTitle("Shopping Cart");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Table for displaying cart items
        String[] columnNames = { "Product", "Quantity", "Price", "SubTotal", "Remove Item" };
        tableModel = new DefaultTableModel(columnNames, 0);

        // Populate table with cart items
        for (CartItem item : cartItems) {
            Object[] rowData = {
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getPrice()),
                    String.format("%.2f", item.getSubTotal()),
                    "Remove" // Placeholder for button
            };
            tableModel.addRow(rowData);
        }

        cartTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the "Remove" column should be editable
            }
        };

        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), e -> {
            // Get the selected row and remove the corresponding item
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
                // Optionally, update total here after removal
                System.out.println("Item removed from row: " + selectedRow);
            }
        }));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Total price label and Remove All button in the same row, right after items
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: " + String.format("%.2f", totalAmount));
        removeAllButton = new JButton("Remove All");

        totalPanel.add(totalLabel); // Add total label
        totalPanel.add(removeAllButton); // Add remove all button
        panel.add(totalPanel, BorderLayout.NORTH); // Add total panel right after the items (North of button panel)

        // Buttons panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center aligned buttons
        mainScreenButton = new JButton("Main Screen");
        browseProductsButton = new JButton("Browse Products");
        checkoutButton = new JButton("Checkout");
        viewOrdersButton = new JButton("View Orders");

        buttonPanel.add(mainScreenButton);
        buttonPanel.add(browseProductsButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(viewOrdersButton);

        panel.add(buttonPanel, BorderLayout.SOUTH); // Place the buttons at the bottom

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    // Methods to add action listeners for buttons
    public void addMainScreenListener(ActionListener listener) {
        mainScreenButton.addActionListener(listener);
    }

    public void addBrowseProductsListener(ActionListener listener) {
        browseProductsButton.addActionListener(listener);
    }

    public void addCheckoutListener(ActionListener listener) {
        checkoutButton.addActionListener(listener);
    }

    public void addViewOrdersListener(ActionListener listener) {
        viewOrdersButton.addActionListener(listener);
    }

    public void addRemoveAllListener(ActionListener listener) {
        removeAllButton.addActionListener(listener); // Add listener for "Remove All" button
    }

    // Getter methods to expose cartTable and tableModel
    public JTable getCartTable() {
        return cartTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void updateTotal(double newTotal) {
        totalLabel.setText("Total: " + String.format("%.2f", newTotal));
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refreshCart(List<CartItem> cartItems, double totalAmount) {
        tableModel.setRowCount(0); // Clear the table

        // Re-populate the table with updated cart items
        for (CartItem item : cartItems) {
            Object[] rowData = {
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getPrice()),
                    String.format("%.2f", item.getSubTotal()),
                    "Remove" // Placeholder for button
            };
            tableModel.addRow(rowData);
        }

        updateTotal(totalAmount); // Update the total price
    }
}