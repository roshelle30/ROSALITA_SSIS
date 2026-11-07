package org.example.dao;

import org.example.model.Products;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {
    public boolean addProduct(String productName, String category, double unitPrice, int stockQuantity, int reorderLevel, int clerkId) {
        String query = "INSERT INTO products (product_name, category, unit_price, stock_quantity, reorder_level) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, productName);
            pstmt.setString(2, category);
            pstmt.setDouble(3, unitPrice);
            pstmt.setInt(4, stockQuantity);
            pstmt.setInt(5, reorderLevel);
            pstmt.executeUpdate();

            try {
                ActivityLogDao logDao = new ActivityLogDao();
                logDao.logAction(clerkId, "PRODUCT_ADDED", "Added new product " + productName + " with initial stock " + stockQuantity);
            } catch (Exception e) {
                System.out.println("Failed to record activity log: " + e.getMessage());
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(int productId, String productName, String category, double unitPrice, int reorderLevel, int clerkId) {
        String query = "UPDATE products SET product_name = ?, category = ?, unit_price = ?, reorder_level = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, productName);
            pstmt.setString(2, category);
            pstmt.setDouble(3, unitPrice);
            pstmt.setInt(4, reorderLevel);
            pstmt.setInt(5, productId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try {
                    ActivityLogDao logDao = new ActivityLogDao();
                    logDao.logAction(clerkId, "PRODUCT_UPDATED", "Updated details for Product ID " + productId + " (" + productName + ")");
                } catch (Exception e) {
                    System.out.println("Failed to record activity log: " + e.getMessage());
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
        return false;
    }
    public boolean adjustStock(int productId, int quantityChange, int clerkId, String remarks) {
        String checkQuery = "SELECT stock_quantity, product_name FROM products WHERE product_id = ?";
        String updateQuery = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        String insertAdjustmentQuery = "INSERT INTO StockAdjustments (product_id, clerk_id, adjustment_type, quantity_changed, adjustment_date) VALUES (?, ?, ?, ?, datetime('now'))";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int currentStock = 0;
            String productName = "";

            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkQuery)) {
                pstmtCheck.setInt(1, productId);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        currentStock = rs.getInt("stock_quantity");
                        productName = rs.getString("product_name");
                    } else {
                        System.out.println("Error: Product ID not found.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (currentStock + quantityChange < 0) {
                System.out.println("Error: Adjustment results in negative stock! Current stock is only " + currentStock);
                conn.rollback();
                return false;
            }

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuery)) {
                pstmtUpdate.setInt(1, quantityChange);
                pstmtUpdate.setInt(2, productId);
                pstmtUpdate.executeUpdate();
            }

            try (PreparedStatement pstmtAdj = conn.prepareStatement(insertAdjustmentQuery)) {
                pstmtAdj.setInt(1, productId);
                pstmtAdj.setInt(2, clerkId);
                pstmtAdj.setString(3, remarks);
                pstmtAdj.setInt(4, quantityChange);
                pstmtAdj.executeUpdate();
            }

            conn.commit();

            try {
                ActivityLogDao logDao = new ActivityLogDao();
                logDao.logAction(clerkId, "STOCK_ADJUSTED", "Adjusted stock for " + productName + " by " + quantityChange + " (" + remarks + ")");
            } catch (Exception e) {
                System.out.println("Failed to record activity log: " + e.getMessage());
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Stock adjustment failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteProduct(int productId, int clerkId) {
        String getQuery = "SELECT product_name FROM products WHERE product_id = ?";
        String deleteQuery = "DELETE FROM products WHERE product_id = ?";

        String productName = "";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmtGet = conn.prepareStatement(getQuery)) {
                pstmtGet.setInt(1, productId);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    if (rs.next()) {
                        productName = rs.getString("product_name");
                    } else {
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, productId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    try {
                        ActivityLogDao logDao = new ActivityLogDao();
                        logDao.logAction(clerkId, "PRODUCT_DELETED", "Deleted product ID " + productId + " (" + productName + ")");
                    } catch (Exception e) {
                        System.out.println("Failed to record activity log: " + e.getMessage());
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
        return false;
    }
    public List<Products> getAllInventory() {
        List<Products> inventoryList = new ArrayList<>();
        String query = "SELECT product_id, product_name, category, unit_price, stock_quantity, reorder_level FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                inventoryList.add(new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching inventory: " + e.getMessage());
        }
        return inventoryList;
    }
}