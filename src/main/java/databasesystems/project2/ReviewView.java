package databasesystems.project2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Date;

public class ReviewView extends JFrame {
    private JTable reviewTable;

    // Add the necessary form fields for submitting a review
    private JTextField customerIdField;
    private JTextField ratingField;
    private JTextArea commentArea;
    private JButton submitReviewButton;

    private ReviewController reviewController;
    private Integer productId;

    // Constructor with a list of reviews and the product ID
    public ReviewView(List<Review> reviews, Integer productId) {
        setTitle("Product Reviews");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.productId = productId;
        reviewController = new ReviewController();

        // Create panel to hold the table and form
        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Columns for review table
        String[] columnNames = { "Customer ID", "Rating", "Comment", "Review Date" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Add rows to the table
        for (Review review : reviews) {
            model.addRow(new Object[] {
                    review.getCustomerId(),
                    review.getRating(),
                    review.getComment(),
                    review.getReviewDate()
            });
        }

        // Create the table to display reviews
        reviewTable = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(reviewTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Form panel to add a new review
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for the form

        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdField = new JTextField();
        formPanel.add(customerIdLabel);
        formPanel.add(customerIdField);

        JLabel ratingLabel = new JLabel("Rating:");
        ratingField = new JTextField();
        formPanel.add(ratingLabel);
        formPanel.add(ratingField);

        JLabel commentLabel = new JLabel("Comment:");
        commentArea = new JTextArea();
        formPanel.add(commentLabel);
        formPanel.add(new JScrollPane(commentArea));

        // Submit button to add the review
        submitReviewButton = new JButton("Submit Review");
        submitReviewButton.addActionListener(e -> {
            try {
                Integer customerId = Integer.parseInt(customerIdField.getText()); // Parse customerId
                int rating = Integer.parseInt(ratingField.getText()); // Parse rating
                String comment = commentArea.getText(); // Get comment text

                // Create a new Review object
                Review review = new Review();
                review.setCustomerId(customerId);
                review.setRating(rating);
                review.setComment(comment);
                review.setProductId(productId); // Set the productId for this review
                review.setReviewDate(new Date()); // Set review date

                // Add the review using the controller
                boolean success = reviewController.addReview(review);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Review added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add review!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid values.");
            }
        });

        formPanel.add(submitReviewButton);
        panel.add(formPanel, BorderLayout.SOUTH);

        // Center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        setVisible(true);
        setVisible(true);
    }

    // Method to handle the review submission
    private void submitReview() {
        // Get values from the form fields
        String customerIdStr = customerIdField.getText();
        String ratingStr = ratingField.getText();
        String comment = commentArea.getText();

        // Validate input fields
        if (customerIdStr.isEmpty() || ratingStr.isEmpty() || comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Integer customerId = Integer.parseInt(customerIdStr);
            Integer rating = Integer.parseInt(ratingStr);

            // Validate rating
            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a new review and add it to the database
            Review newReview = new Review();
            newReview.setCustomerId(customerId);
            newReview.setRating(rating);
            newReview.setComment(comment);
            newReview.setReviewDate(new java.util.Date()); // Current date

            // Add review to the database
            reviewController.addReview(newReview);

            // Refresh the reviews table (you could implement the method to update reviews
            // dynamically)
            JOptionPane.showMessageDialog(this, "Review submitted successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Close the form
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Customer ID and Rating.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
