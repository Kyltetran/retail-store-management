package databasesystems.project2;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO(); // Initialize DAO
    }

    // Filter products based on category, price range, and keyword
    public List<Product> getFilteredProducts(int categoryId, String minPrice, String maxPrice, String keyword) {
        List<Product> products = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Product WHERE 1=1");

            if (categoryId != 0) { // If category ID is not 0 (which means 'All')
                query.append(" AND CategoryID = ?");
            }
            if (!minPrice.isEmpty()) {
                query.append(" AND SellingPrice >= ?");
            }
            if (!maxPrice.isEmpty()) {
                query.append(" AND SellingPrice <= ?");
            }
            if (!keyword.isEmpty()) {
                query.append(" AND ProductName LIKE ?");
            }

            PreparedStatement stmt = connection.prepareStatement(query.toString());

            int index = 1;
            if (categoryId != 0) {
                stmt.setInt(index++, categoryId);
            }
            if (!minPrice.isEmpty()) {
                stmt.setDouble(index++, Double.parseDouble(minPrice));
            }
            if (!maxPrice.isEmpty()) {
                stmt.setDouble(index++, Double.parseDouble(maxPrice));
            }
            if (!keyword.isEmpty()) {
                stmt.setString(index++, "%" + keyword + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("SellingPrice"),
                        rs.getInt("QuantityInStock"),
                        rs.getInt("CategoryID"));
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    // Retrieve all products
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts(); // Fetch all products from DAO
    }

    // Retrieve a product by ID
    public Product getProductById(int productId) {
        Product product = null;
        try (Connection connection = MySQLConnection.getConnection()) {
            String query = "SELECT * FROM Product WHERE ProductID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("SellingPrice"),
                        rs.getInt("QuantityInStock"),
                        rs.getInt("CategoryID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }
    // You can add more business methods as needed, such as:
    // - Check stock availability
    // - Apply business rules (e.g., discounts, categories)
    // - Filter products by category
}
