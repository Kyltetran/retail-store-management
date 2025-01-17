package databasesystems.project2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection()) {
            String query = "SELECT * FROM Product";
            PreparedStatement stmt = connection.prepareStatement(query);
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

    // Method to filter products with ProductID
    public List<Product> getFilteredProducts(String category, String minPrice, String maxPrice, String keyword) {
        List<Product> products = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Product WHERE 1=1");

            int index = 1; // Initialize index
            PreparedStatement stmt = connection.prepareStatement(query.toString()); // Initialize stmt
            if (!category.equals("All")) {
                stmt.setInt(index++, Integer.parseInt(category));
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

            if (!category.equals("All")) {
                stmt.setInt(index++, Integer.parseInt(category));
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
}