package databasesystems.project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckoutPageView extends JFrame {
    private JTable productTable;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel totalLabel;
    private DefaultTableModel tableModel;

    private JTextField nameField, addressField, phoneField, emailField;
    private JRadioButton creditCardOption, paypalOption, cashOption;

    // PayPal related fields
    private JTextField paypalTransactionIdField;

    // Credit Card related fields
    private JTextField creditCardNumberField;
    private JTextField creditCardTypeField;
    private JComboBox<String> creditCardExpirationMonth;
    private JComboBox<String> creditCardExpirationYear;

    private JPanel paymentFormPanel;

    public CheckoutPageView(List<CartItem> cartItems, double totalAmount) {
        setTitle("Checkout");
        setSize(900, 700); // Increased height for better visibility
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Customer Details
        JPanel customerDetailsPanel = new JPanel();
        customerDetailsPanel.setLayout(new BoxLayout(customerDetailsPanel, BoxLayout.Y_AXIS));
        customerDetailsPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        namePanel.add(nameField);
        customerDetailsPanel.add(namePanel);

        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressPanel.add(new JLabel("Address:"));
        addressField = new JTextField(20);
        addressPanel.add(addressField);
        customerDetailsPanel.add(addressPanel);

        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        phonePanel.add(new JLabel("Phone number:"));
        phoneField = new JTextField(20);
        phonePanel.add(phoneField);
        customerDetailsPanel.add(phonePanel);

        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        emailPanel.add(emailField);
        customerDetailsPanel.add(emailPanel);

        // Payment method selection
        JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentMethodPanel.add(new JLabel("Payment method:"));
        creditCardOption = new JRadioButton("Credit card");
        paypalOption = new JRadioButton("PayPal");
        cashOption = new JRadioButton("Cash");
        ButtonGroup paymentMethodGroup = new ButtonGroup();
        paymentMethodGroup.add(creditCardOption);
        paymentMethodGroup.add(paypalOption);
        paymentMethodGroup.add(cashOption);
        paymentMethodPanel.add(creditCardOption);
        paymentMethodPanel.add(paypalOption);
        paymentMethodPanel.add(cashOption);
        customerDetailsPanel.add(paymentMethodPanel);

        // Payment details form
        paymentFormPanel = new JPanel(new CardLayout());
        paymentFormPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        JPanel paymentDetailsContainer = new JPanel();
        paymentDetailsContainer.setLayout(new BoxLayout(paymentDetailsContainer, BoxLayout.Y_AXIS));

        // PayPal Form (small size)
        JPanel paypalForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paypalForm.add(new JLabel("PayPal Transaction ID:"));
        paypalTransactionIdField = new JTextField(15); // Limit width
        paypalForm.add(paypalTransactionIdField);
        paymentDetailsContainer.add(paypalForm);

        // Credit Card Form (small size)
        JPanel creditCardForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        creditCardForm.add(new JLabel("Credit Card Number:"));
        creditCardNumberField = new JTextField(15); // Limit width
        creditCardForm.add(creditCardNumberField);

        creditCardForm.add(new JLabel("Card Type:"));
        creditCardTypeField = new JTextField(10); // Limit width
        creditCardForm.add(creditCardTypeField);

        creditCardForm.add(new JLabel("Expiration Date:"));

        // Expiration month dropdown
        String[] months = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        creditCardExpirationMonth = new JComboBox<>(months);
        creditCardForm.add(creditCardExpirationMonth);

        // Expiration year dropdown
        String[] years = { "2023", "2024", "2025", "2026", "2027", "2028" };
        creditCardExpirationYear = new JComboBox<>(years);
        creditCardForm.add(creditCardExpirationYear);

        paymentDetailsContainer.add(creditCardForm);

        // Add forms to the card layout
        paymentFormPanel.add(new JPanel(), "None"); // Empty panel for Cash option
        paymentFormPanel.add(paypalForm, "PayPal");
        paymentFormPanel.add(creditCardForm, "CreditCard");

        customerDetailsPanel.add(paymentFormPanel); // Add payment form to customer panel
        mainPanel.add(customerDetailsPanel, BorderLayout.NORTH); // Customer details at the top

        // Product table (always visible)
        String[] columnNames = { "Product", "Quantity", "Price", "SubTotal" };
        tableModel = new DefaultTableModel(columnNames, 0);

        for (CartItem item : cartItems) {
            Object[] rowData = {
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getPrice()),
                    String.format("%.2f", item.getSubTotal())
            };
            tableModel.addRow(rowData);
        }

        productTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Table below customer details

        // Total and button panel
        JPanel totalAndButtonPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: " + String.format("%.2f", totalAmount));
        totalAndButtonPanel.add(totalLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmButton = new JButton("Confirm Purchase");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        totalAndButtonPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(totalAndButtonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        // Add action listeners for payment method radio buttons
        creditCardOption.addActionListener(e -> showPaymentForm("CreditCard"));
        paypalOption.addActionListener(e -> showPaymentForm("PayPal"));
        cashOption.addActionListener(e -> showPaymentForm("None")); // No additional form for cash
    }

    // Method to switch the displayed payment form
    private void showPaymentForm(String paymentMethod) {
        CardLayout cl = (CardLayout) (paymentFormPanel.getLayout());
        cl.show(paymentFormPanel, paymentMethod);
    }

    // Action listeners for buttons
    public void addConfirmListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public String getSelectedPaymentMethod() {
        if (creditCardOption.isSelected()) {
            return "Credit Card";
        } else if (paypalOption.isSelected()) {
            return "PayPal";
        } else {
            return "Cash";
        }
    }

    // Getter methods for payment details
    public String getPayPalTransactionId() {
        return paypalTransactionIdField.getText();
    }

    public String getCreditCardNumber() {
        return creditCardNumberField.getText();
    }

    public String getCreditCardType() {
        return creditCardTypeField.getText();
    }

    public String getCreditCardExpirationDate() {
        return creditCardExpirationMonth.getSelectedItem() + "/" + creditCardExpirationYear.getSelectedItem();
    }

    public JTable getProductTable() {
        return productTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void updateTotal(double newTotal) {
        totalLabel.setText("Total: " + String.format("%.2f", newTotal));
    }

    public String getCustomerName() {
        return nameField.getText();
    }

    public String getCustomerAddress() {
        return addressField.getText();
    }

    public String getCustomerPhone() {
        return phoneField.getText();
    }

    public String getCustomerEmail() {
        return emailField.getText();
    }

}
