package databasesystems.project2;

public class Inventory {
    private int id;
    private int productId;
    private int stock;
    private String location;
    private int minimumStock;

    public Inventory(int id, int productId, int stock, String location, int minimumStock) {
        this.id = id;
        this.productId = productId;
        this.stock = stock;
        this.location = location;
        this.minimumStock = minimumStock;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getStock() {
        return stock;
    }

    public String getLocation() {
        return location;
    }

    public int getMinimumStock() {
        return minimumStock;
    }
}
