package com.banking.dao;

import com.banking.config.DBUtil;
import com.banking.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public long insert(Connection c, Customer cu) throws SQLException {
        String sql = "INSERT INTO customers (full_name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cu.getFullName());
            ps.setString(2, cu.getEmail());
            ps.setString(3, cu.getPhone());
            ps.setString(4, cu.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM customers WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, email, phone, address, created_at FROM customers ORDER BY customer_id";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Customer cu = new Customer(rs.getString("full_name"), rs.getString("email"),
                        rs.getString("phone"), rs.getString("address"));
                cu.setCustomerId(rs.getLong("customer_id"));
                cu.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(cu);
            }
        }
        return list;
    }

    public int count() throws SQLException { return DBUtil.countRows("customers"); }
}
