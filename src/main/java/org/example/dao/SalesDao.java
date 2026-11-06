package org.example.dao;

import org.example.model.Products;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesDao {
    public List<Products> getAllProducts() {
        List<Products> products = new ArrayList<>();
        String query = "SELECT product_id, product_name, category, unit_price, stock_quantity, reorder_level FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(new Products(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }
    public boolean processCheckout(int productId, int quantitySold, int cashierId, String paymentMethod, double totalAmount) {
        String checkStockQuery = "SELECT stock_quantity, product_name FROM products WHERE product_id = ?";
        String updateStockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        String insertTransactionQuery = "INSERT INTO SalesTransactions (cashier_id, transaction_date, total_amount, payment_method) VALUES (?, DATETIME('now'), ?, ?)";
        String insertItemQuery = "INSERT INTO SalesItems (transaction_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        String productName = "";
        double unitPrice = 0;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int currentStock = 0;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkStockQuery)) {
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

            String priceQuery = "SELECT unit_price FROM products WHERE product_id = ?";
            try (PreparedStatement pstmtPrice = conn.prepareStatement(priceQuery)) {
                pstmtPrice.setInt(1, productId);
                try (ResultSet rs = pstmtPrice.executeQuery()) {
                    if (rs.next()) {
                        unitPrice = rs.getDouble("unit_price");
                    }
                }
            }

            if (currentStock < quantitySold) {
                System.out.println("Error: Insufficient stock! Available stock is only " + currentStock);
                conn.rollback();
                return false;
            }
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateStockQuery)) {
                pstmtUpdate.setInt(1, quantitySold);
                pstmtUpdate.setInt(2, productId);
                pstmtUpdate.executeUpdate();
            }

            int transactionId = -1;
            try (PreparedStatement pstmtTrans = conn.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmtTrans.setInt(1, cashierId);
                pstmtTrans.setDouble(2, totalAmount);
                pstmtTrans.setString(3, paymentMethod);
                pstmtTrans.executeUpdate();

                try (ResultSet generatedKeys = pstmtTrans.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transactionId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating transaction failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement pstmtItem = conn.prepareStatement(insertItemQuery)) {
                pstmtItem.setInt(1, transactionId);
                pstmtItem.setInt(2, productId);
                pstmtItem.setInt(3, quantitySold);
                pstmtItem.setDouble(4, unitPrice);
                pstmtItem.setDouble(5, totalAmount);
                pstmtItem.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Checkout failed: " + e.getMessage());
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

        try {
            ActivityLogDao logDao = new ActivityLogDao();
            logDao.logAction(cashierId, "SALES_CHECKOUT: Sold " + quantitySold + " of " + productName + " for PHP " + totalAmount + " via " + paymentMethod);
        } catch (Exception e) {
            System.out.println("Failed to record activity log: " + e.getMessage());
        }

        return true;
    }

    public List<String> getSalesHistory() {
        List<String> historyList = new ArrayList<>();
        String query = "SELECT t.transaction_id, t.transaction_date, t.total_amount, t.payment_method, " +
                "p.product_name, i.quantity, i.unit_price " +
                "FROM SalesTransactions t " +
                "JOIN SalesItems i ON t.transaction_id = i.transaction_id " +
                "JOIN products p ON i.product_id = p.product_id " +
                "ORDER BY t.transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            String format = "| %-3d | %-19s | %-18s | %-5d | PHP %-9.2f | %-5s |";

            while (rs.next()) {
                String record = String.format(format,
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_date"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method")
                );
                historyList.add(record);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sales history: " + e.getMessage());
        }
        return historyList;
    }

    public boolean voidTransaction(int transactionId, int cashierId) {
        String selectItemQuery = "SELECT product_id, quantity FROM SalesItems WHERE transaction_id = ?";
        String restoreStockQuery = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        String deleteItemsQuery = "DELETE FROM SalesItems WHERE transaction_id = ?";
        String deleteTransQuery = "DELETE FROM SalesTransactions WHERE transaction_id = ?";

        Connection conn = null;
        int productId = 0;
        int quantity = 0;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtSelect = conn.prepareStatement(selectItemQuery)) {
                pstmtSelect.setInt(1, transactionId);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        productId = rs.getInt("product_id");
                        quantity = rs.getInt("quantity");
                    } else {
                        System.out.println("Error: Transaction ID not found.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmtRestore = conn.prepareStatement(restoreStockQuery)) {
                pstmtRestore.setInt(1, quantity);
                pstmtRestore.setInt(2, productId);
                pstmtRestore.executeUpdate();
            }

            try (PreparedStatement pstmtDelItems = conn.prepareStatement(deleteItemsQuery)) {
                pstmtDelItems.setInt(1, transactionId);
                pstmtDelItems.executeUpdate();
            }

            try (PreparedStatement pstmtDelTrans = conn.prepareStatement(deleteTransQuery)) {
                pstmtDelTrans.setInt(1, transactionId);
                pstmtDelTrans.executeUpdate();
            }

            conn.commit();

            try {
                ActivityLogDao logDao = new ActivityLogDao();
                logDao.logAction(cashierId, "VOID_TRANSACTION: Voided Transaction ID " + transactionId + " and restored " + quantity + " units to Product ID " + productId);
            } catch (Exception e) {
                System.out.println("Failed to record activity log: " + e.getMessage());
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Void transaction failed: " + e.getMessage());
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
}