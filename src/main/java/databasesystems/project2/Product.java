package databasesystems.project2;

public class Product {
    private int productID;
    private String productName;
    private double sellingPrice;
    private int quantityInStock;
    private int categoryID;

    public Product(int productID, String productName, double sellingPrice, int quantityInStock, int categoryID) {
        this.productID = productID;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.quantityInStock = quantityInStock;
        this.categoryID = categoryID;
    }

    // Getter for Product Name
    public String getName() {
        return productName;
    }

    // Getter for Price
    public double getPrice() {
        return sellingPrice;
    }

    // Getter for Stock
    public int getStock() {
        return quantityInStock;
    }

    // Getter for Category
    public int getCategory() {
        return categoryID;
    }

    public int getProductId() {
        return this.productID;
    }
}
