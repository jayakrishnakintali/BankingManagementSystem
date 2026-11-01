package com.banking.dao;

import com.banking.config.DBUtil;
import com.banking.model.User;

import java.sql.*;

public class UserDAO {

    /** SELECT with PreparedStatement (placeholders prevent SQL injection). */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT u.user_id, u.customer_id, u.username, u.password_hash, u.role, u.is_active, "
                   + "COALESCE(c.full_name, 'Administrator') AS full_name "
                   + "FROM users u LEFT JOIN customers c ON u.customer_id = c.customer_id WHERE u.username = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                long cid = rs.getLong("customer_id");
                Long customerId = rs.wasNull() ? null : cid;
                return new User(rs.getLong("user_id"), customerId, rs.getString("username"),
                        rs.getString("password_hash"), rs.getString("role"),
                        rs.getBoolean("is_active"), rs.getString("full_name"));
            }
        }
    }

    /** INSERT; uses the caller's Connection so registration can be one transaction. */
    public void insertCustomerUser(Connection c, long customerId, String username, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (customer_id, username, password_hash, role) VALUES (?, ?, ?, 'CUSTOMER')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setString(2, username);
            ps.setString(3, passwordHash);
            ps.executeUpdate();
        }
    }
}
