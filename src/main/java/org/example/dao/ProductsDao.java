package org.example.dao;

import org.example.model.Products;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductsDao {
    private final ActivityLogDao logDao = new ActivityLogDao();

    public boolean addProduct(Products product, int adminId) {
        String sql = "INSERT INTO Products (product_name, category, unit_price, stock_quantity, reorder_level) VALUES (?, ?, ?, ?, ?)";
        boolean isAdded = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.productName());
            pstmt.setString(2, product.category());
            pstmt.setDouble(3, product.unitPrice());
            pstmt.setInt(4, product.stockQuantity());
            pstmt.setInt(5, product.reorderLevel());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                isAdded = true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
        if (isAdded) {
            logDao.logAction(adminId, "PRODUCT_ADDED: Admin " + adminId + " added product: " + product.productName());
            return true;
        }
        return false;
    }
    public List<Products> getAllProducts() {
        List<Products> productsList = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productsList.add(new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return productsList;
    }

    public boolean updateProduct(Products product, int adminId) {
        String sql = "UPDATE Products SET product_name = ?, category = ?, unit_price = ?, stock_quantity = ?, reorder_level = ? WHERE product_id = ?";
        boolean isUpdated = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.productName());
            pstmt.setString(2, product.category());
            pstmt.setDouble(3, product.unitPrice());
            pstmt.setInt(4, product.stockQuantity());
            pstmt.setInt(5, product.reorderLevel());
            pstmt.setInt(6, product.productId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                isUpdated = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }

        if (isUpdated) {
            logDao.logAction(adminId, "PRODUCT_UPDATED: Admin " + adminId + " updated product ID " + product.productId() + " (" + product.productName() + ")");
            return true;
        }
        return false;
    }

    public boolean deleteProduct(int productId, String productName, int adminId) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        boolean isDeleted = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                isDeleted = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }

        if (isDeleted) {
            logDao.logAction(adminId, "PRODUCT_DELETED: Admin " + adminId + " deleted product ID " + productId + " (" + productName + ")");
            return true;
        }
        return false;
    }
}