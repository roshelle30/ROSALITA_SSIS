package org.example.dao;

import org.example.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final ActivityLogDao logDao = new ActivityLogDao();

    public boolean registerUser(String full_name, String username, String password_hash, String role){
        String sql = "INSERT INTO users(full_name, username, password_hash, role, status) VALUES (?, ?, ?, ?, 'PENDING')";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, full_name);
            pstmt.setString(2, username);
            pstmt.setString(3, password_hash);
            pstmt.setString(4, role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("\nSuccessful Registration, Waiting for Admin Approval! ");
            return true;

        }catch (SQLException e){
            System.err.println("Failed: "+ e.getMessage());
            return false;
        }
    }

    public String loginUser(String username, String password_hash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        int userId = -1;
        String role = null;
        String status = null;
        String fullName = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password_hash);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                    role = rs.getString("role");
                    status = rs.getString("status");
                    fullName = rs.getString("full_name");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            return null;
        }

        if (userId == -1) {
            System.out.println("Login Failed: Invalid username or password.");
            return null;
        }

        if ("PENDING".equalsIgnoreCase(status)) {
            System.out.println("Login Failed: Your account is PENDING for Admin approval.");
            return null;
        }

        logDao.logAction(userId, "USER_LOGIN", "User " + username + " logged in with role " + role.toUpperCase());

        System.out.println("\nLogin Successful! Welcome, " + fullName);
        return role;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> getPendingUsers() {
        List<User> pendingUsers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                pendingUsers.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingUsers;
    }

    public boolean approveUser(int userId, int adminId) {
        String query = "UPDATE users SET status = 'APPROVED' WHERE user_id = ?";
        boolean updated = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                updated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (updated) {
            logDao.logAction(adminId, "USER_APPROVED", "Admin " + adminId + " approved account for User ID " + userId);
            return true;
        }
        return false;
    }

    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus, int adminId) {
        String query = "UPDATE users SET role = ?, status = ? WHERE user_id = ?";
        boolean updated = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newRole.toUpperCase());
            pstmt.setString(2, newStatus.toUpperCase());
            pstmt.setInt(3, userId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                updated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (updated) {
            logDao.logAction(adminId, "USER_UPDATED", "Admin " + adminId + " changed role of User " + userId + " to " + newRole);
            return true;
        }
        return false;
    }

    public boolean deleteUser(int userId, int adminId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        boolean deleted = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                deleted = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (deleted) {
            logDao.logAction(adminId, "USER_DELETED", "Admin " + adminId + " deleted User ID " + userId);
            return true;
        }
        return false;
    }
}