package org.example.dao;

import org.example.model.Activity_Logs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    public boolean logAction(int userId, String action) {
        String description = "Executed " + action + " operation in the system.";
        return logAction(userId, action, description);
    }

    public boolean logAction(int userId, String action, String description) {
        String sql = "INSERT INTO Activity_Logs (user_id, action, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, description != null ? description : action);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to record activity log: " + e.getMessage());
            return false;
        }
    }

    public List<Activity_Logs> getAllLogs() {
        List<Activity_Logs> logs = new ArrayList<>();
        String sql = "SELECT * FROM Activity_Logs ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new Activity_Logs(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
        return logs;
    }

    public List<Activity_Logs> getLogsByUserId(int userId) {
        List<Activity_Logs> logs = new ArrayList<>();
        String sql = "SELECT * FROM Activity_Logs WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new Activity_Logs(
                            rs.getInt("log_id"),
                            rs.getInt("user_id"),
                            rs.getString("action"),
                            rs.getTimestamp("timestamp"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs by user ID: " + e.getMessage());
        }
        return logs;
    }
}