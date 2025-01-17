package databasesystems.project2;

public class CartItem {
    private Product product; // Store the entire product object
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getProductId() {
        return this.product.getProductId(); // Get productId from the Product object
    }

    public String getProductName() {
        return this.product.getName();
    }

    public double getPrice() {
        return this.product.getPrice();
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        return this.product.getPrice() * this.quantity;
    }

    public Product getProduct() {
        return this.product;
    }
}
