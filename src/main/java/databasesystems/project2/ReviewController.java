package databasesystems.project2;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ReviewController {

    private MongoCollection<Document> reviewsCollection;
    private MongoCollection<Document> productsCollection;

    public ReviewController() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        reviewsCollection = database.getCollection("Reviews"); // Collection for reviews (case-sensitive)
        productsCollection = database.getCollection("Products"); // Collection for products (case-sensitive)
    }

    // Fetch reviews for a given product along with product details
    public List<Review> getReviewsForProduct(Integer productId) {
        List<Review> reviews = new ArrayList<>();

        try (MongoCursor<Document> cursor = reviewsCollection
                .find(new Document("ProductID", productId)).iterator()) {

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Review review = new Review();
                review.setCustomerId(doc.getInteger("CustomerID"));
                review.setRating(doc.getInteger("Rating"));
                review.setComment(doc.getString("Comment"));
                review.setReviewDate(doc.getDate("ReviewDate"));
                reviews.add(review);
            }
        } catch (NumberFormatException e) {
            // Handle error in case of an invalid format for productId
            System.err.println("Invalid ProductID format: " + productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Add a new review for a product
    public boolean addReview(Review review) {
        try {
            // Insert the review into the MongoDB Reviews collection
            Document reviewDoc = new Document("CustomerID", review.getCustomerId())
                    .append("ProductID", review.getProductId()) // Use review.getProductId() here
                    .append("Rating", review.getRating())
                    .append("Comment", review.getComment())
                    .append("ReviewDate", review.getReviewDate());

            reviewsCollection.insertOne(reviewDoc); // Insert the document into MongoDB
            return true;
        } catch (Exception e) {
            System.err.println("Error adding review: " + e.getMessage());
            return false;
        }
    }

}
