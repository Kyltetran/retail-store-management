package databasesystems.project2;

import java.util.Date;

public class Review {
    private int customerId;
    private int rating;
    private String comment;
    private Date reviewDate;
    private int productId; // Add this field to represent the product ID

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getProductId() {
        return productId; // Getter for product ID
    }

    public void setProductId(int productId) {
        this.productId = productId; // Setter for product ID
    }
}
