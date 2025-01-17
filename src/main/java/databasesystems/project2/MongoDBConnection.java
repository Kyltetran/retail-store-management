package databasesystems.project2;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    // MongoDB URI, you can find this in your MongoDB Atlas or local instance
    private static final String uri = "mongodb+srv://kylte:kylte@cluster.1qnhg.mongodb.net/?retryWrites=true&w=majority&appName=Cluster";

    // Method to connect to MongoDB
    public static void connect() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase("Project2");
            System.out.println("Connected to MongoDB Database!");
        }
    }

    // Method to get the database
    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect(); // Ensure connection is made before returning the database
        }
        return database;
    }
}
