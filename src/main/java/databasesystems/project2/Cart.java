package databasesystems.project2;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    // Add an item to the cart
    public void addItem(CartItem newItem) {
        // Check if the item already exists in the cart
        for (CartItem item : items) {
            if (item.getProductName().equals(newItem.getProductName())) {
                // If it exists, increase the quantity and return
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        // If it doesn't exist, add the new item
        items.add(newItem);
    }

    // Remove an item from the cart
    public void removeItem(CartItem item) {
        items.remove(item);
    }

    // Clear all items from the cart
    public void clear() {
        items.clear(); // Clear all items in the cart
    }

    // Get all items in the cart
    public List<CartItem> getItems() {
        return items;
    }

    // Calculate the total price of items in the cart
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubTotal();
        }
        return total;
    }
}
