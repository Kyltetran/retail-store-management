package databasesystems.project2;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;

public class ManagerDashboardView extends JFrame {

    private JTable bestSellingTable;
    private JTable recentCustomersTable;
    private JButton backToLoginButton;

    public ManagerDashboardView() {
        setTitle("Manager Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Create a panel with GridLayout to divide the space into two equal parts
        JPanel gridPanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
        panel.add(gridPanel, BorderLayout.CENTER);

        // Panel for Best Selling Products (Top)
        JPanel topPanel = new JPanel(new BorderLayout());
        gridPanel.add(topPanel);

        JLabel bestSellingLabel = new JLabel("Best Selling Products", SwingConstants.CENTER);
        topPanel.add(bestSellingLabel, BorderLayout.NORTH);

        bestSellingTable = new JTable();
        JScrollPane bestSellingScroll = new JScrollPane(bestSellingTable);
        topPanel.add(bestSellingScroll, BorderLayout.CENTER);

        // Panel for Recent Customers (Bottom)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        gridPanel.add(bottomPanel);

        JLabel recentCustomersLabel = new JLabel("Recent Customers", SwingConstants.CENTER);
        bottomPanel.add(recentCustomersLabel, BorderLayout.NORTH);

        recentCustomersTable = new JTable();
        JScrollPane recentCustomersScroll = new JScrollPane(recentCustomersTable);
        bottomPanel.add(recentCustomersScroll, BorderLayout.CENTER);

        // Initialize the "Back to Login" button
        backToLoginButton = new JButton("Back to Login");

        // Panel for the "Back to Login" button at the bottom of the frame
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.add(backToLoginButton);
        panel.add(bottomButtonPanel, BorderLayout.SOUTH);

        // Center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        setVisible(true);
    }

    // Add action listener for Back to Login button
    public void addBackToLoginListener(ActionListener listener) {
        backToLoginButton.addActionListener(listener);
    }

    // Method to update Best Selling Products table
    public void updateBestSellingProducts(List<Map.Entry<String, Integer>> bestSellingProducts) {
        String[] columnNames = { "Product Name", "Sales Count" };
        Object[][] data = new Object[bestSellingProducts.size()][2];

        int i = 0;
        for (Map.Entry<String, Integer> entry : bestSellingProducts) {
            data[i][0] = entry.getKey(); // Product name
            data[i][1] = entry.getValue(); // Sales count
            i++;
        }

        bestSellingTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    // Method to update Recent Customers table with detailed information
    public void updateRecentCustomers(List<Map<String, String>> customers) {
        String[] columnNames = { "Customer Name", "Address", "Phone Number", "Email" };
        Object[][] data = new Object[customers.size()][4];

        int i = 0;
        for (Map<String, String> customer : customers) {
            data[i][0] = customer.get("CustomerName");
            data[i][1] = customer.get("Address");
            data[i][2] = customer.get("PhoneNumber");
            data[i][3] = customer.get("Email");
            i++;
        }

        recentCustomersTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
}
