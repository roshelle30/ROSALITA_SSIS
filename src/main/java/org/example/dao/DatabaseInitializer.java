package org.example.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "status TEXT DEFAULT 'PENDING'" +
                ");";

        String createProductsTable = "CREATE TABLE IF NOT EXISTS Products (" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_name TEXT NOT NULL, " +
                "category TEXT, " +
                "unit_price REAL NOT NULL, " +
                "stock_quantity INTEGER DEFAULT 0, " +
                "reorder_level INTEGER DEFAULT 10" +
                ");";

        String createSalesTransactionsTable = "CREATE TABLE IF NOT EXISTS SalesTransactions (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cashier_id INTEGER, " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "total_amount REAL NOT NULL, " +
                "payment_method TEXT, " +
                "FOREIGN KEY (cashier_id) REFERENCES users(user_id)" +
                ");";

        String createSalesItemsTable = "CREATE TABLE IF NOT EXISTS SalesItems (" +
                "sales_item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "transaction_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY (transaction_id) REFERENCES SalesTransactions(transaction_id), " +
                "FOREIGN KEY (product_id) REFERENCES Products(product_id)" +
                ");";

        String createStockAdjustmentsTable = "CREATE TABLE IF NOT EXISTS StockAdjustments (" +
                "adjustment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER, " +
                "clerk_id INTEGER, " +
                "adjustment_type TEXT, " +
                "quantity_changed INTEGER NOT NULL, " +
                "adjustment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (product_id) REFERENCES Products(product_id), " +
                "FOREIGN KEY (clerk_id) REFERENCES users(user_id)" +
                ");";

        String createActivityLogsTable = "CREATE TABLE IF NOT EXISTS Activity_Logs (" +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "action TEXT NOT NULL, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createProductsTable);
            stmt.execute(createSalesTransactionsTable);
            stmt.execute(createSalesItemsTable);
            stmt.execute(createStockAdjustmentsTable);
            stmt.execute(createActivityLogsTable);

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}