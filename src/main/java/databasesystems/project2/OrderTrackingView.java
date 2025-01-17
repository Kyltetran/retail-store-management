package databasesystems.project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderTrackingView extends JFrame {
    private JTable orderTable;
    private JButton mainScreenButton;
    private JButton backToCartButton;
    private DefaultTableModel tableModel;
    private OrderTrackingController controller;

    public OrderTrackingView(List<Order> confirmedOrders, OrderTrackingController controller) {
        this.controller = controller;
        setTitle("Order Tracking");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Table to display orders
        String[] columnNames = { "OrderID", "Date", "Status", "Total", "Reorder" };
        tableModel = new DefaultTableModel(columnNames, 0);

        // Populate the table with confirmed order data
        populateTable(confirmedOrders);

        orderTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the "Reorder" column is editable
            }
        };

        // Add reorder button functionality
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                int orderId = Integer.parseInt(orderTable.getValueAt(selectedRow, 0).toString());
                controller.reorder(orderId); // Call the reorder method in the controller
            }
        }));

        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainScreenButton = new JButton("Main Screen");
        backToCartButton = new JButton("Back to Cart");
        buttonPanel.add(mainScreenButton);
        buttonPanel.add(backToCartButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        setDefaultCloseOperation(HIDE_ON_CLOSE); // Set to hide on close instead of dispose

    }

    // Method to populate table with orders
    public void populateTable(List<Order> confirmedOrders) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Populate rows with order data
        for (Order order : confirmedOrders) {
            String displayStatus = order.getStatus(); // Get the original status

            // Convert "Confirmed" to "Pending" for display purposes
            if (displayStatus.equalsIgnoreCase("Confirmed")) {
                displayStatus = "Pending";
            }

            Object[] rowData = {
                    order.getOrderId(),
                    order.getOrderDate(),
                    displayStatus, // Use the converted status
                    order.getTotal(),
                    "Reorder" // Placeholder for reorder button
            };
            tableModel.addRow(rowData);
        }
    }

    // Methods to add action listeners
    public void addMainScreenListener(ActionListener listener) {
        mainScreenButton.addActionListener(listener);
    }

    public void addBackToCartListener(ActionListener listener) {
        backToCartButton.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
