package com.banking.dao;

import com.banking.config.DBUtil;
import com.banking.model.Account;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class AccountDAO {
    public static final int DEFAULT_BRANCH_ID = 1;

    public Account findByCustomerId(long customerId) throws SQLException {
        String sql = "SELECT a.account_id, a.customer_id, a.branch_id, a.account_number, a.account_type, a.balance, "
                   + "a.status, a.created_at, b.branch_name, b.ifsc_code "
                   + "FROM accounts a JOIN branches b ON a.branch_id = b.branch_id WHERE a.customer_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Account a = new Account();
                a.setAccountId(rs.getLong("account_id"));
                a.setCustomerId(rs.getLong("customer_id"));
                a.setBranchId(rs.getInt("branch_id"));
                a.setAccountNumber(rs.getString("account_number"));
                a.setAccountType(rs.getString("account_type"));
                a.setBalance(rs.getBigDecimal("balance"));
                a.setStatus(rs.getString("status"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                a.setBranchName(rs.getString("branch_name"));
                a.setIfscCode(rs.getString("ifsc_code"));
                return a;
            }
        }
    }

    /** Next 12-digit account number (starts at 100000000001). */
    public String nextAccountNumber(Connection c) throws SQLException {
        String sql = "SELECT IFNULL(MAX(CAST(account_number AS UNSIGNED)), 100000000000) + 1 FROM accounts";
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return String.valueOf(rs.getLong(1));
        }
    }

    /** New accounts start with a zero balance. */
    public void insert(Connection c, long customerId, int branchId, String accountNumber, String type) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, branch_id, account_number, account_type, balance) VALUES (?, ?, ?, ?, 0.00)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setInt(2, branchId);
            ps.setString(3, accountNumber);
            ps.setString(4, type);
            ps.executeUpdate();
        }
    }

    /** Admin view: every account with its owner. */
    public List<Map<String, Object>> findAllWithOwner() throws SQLException {
        String sql = "SELECT a.account_id, a.account_number, c.full_name, a.account_type, a.balance, a.status, b.branch_name "
                   + "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id "
                   + "JOIN branches b ON a.branch_id = b.branch_id ORDER BY a.account_id";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return DBUtil.toList(rs);
        }
    }

    public int count() throws SQLException { return DBUtil.countRows("accounts"); }
}
