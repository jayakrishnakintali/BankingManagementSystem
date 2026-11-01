package com.banking.dao;

import com.banking.config.DBUtil;
import com.banking.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Banking operations are done inside MySQL stored procedures and called through
 * CallableStatement. Each procedure returns a status string through an OUT parameter.
 */
public class TransactionDAO {

    public String deposit(long accountId, BigDecimal amount) throws SQLException {
        return call("{call sp_deposit(?, ?, ?)}", accountId, amount);
    }

    public String withdraw(long accountId, BigDecimal amount) throws SQLException {
        return call("{call sp_withdraw(?, ?, ?)}", accountId, amount);
    }

    public String transfer(long fromAccountId, String toAccountNumber, BigDecimal amount) throws SQLException {
        return call("{call sp_transfer(?, ?, ?, ?)}", fromAccountId, toAccountNumber, amount);
    }

    /** Sets the IN parameters in order, registers the last parameter as the OUT status. */
    private String call(String sql, Object... in) throws SQLException {
        try (Connection c = DBUtil.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            int i = 1;
            for (Object p : in) {
                if (p instanceof Long l) cs.setLong(i++, l);
                else if (p instanceof BigDecimal b) cs.setBigDecimal(i++, b);
                else cs.setString(i++, (String) p);
            }
            cs.registerOutParameter(i, Types.VARCHAR);
            cs.execute();
            return cs.getString(i);
        }
    }

    /** Customer's own history, newest first. */
    public List<Transaction> findByAccount(long accountId, int limit) throws SQLException {
        String sql = "SELECT transaction_id, account_id, transaction_type, amount, balance_after, reference_account, "
                   + "description, transaction_date, status FROM transactions WHERE account_id = ? "
                   + "ORDER BY transaction_date DESC, transaction_id DESC LIMIT ?";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setTransactionId(rs.getLong("transaction_id"));
                    t.setAccountId(rs.getLong("account_id"));
                    t.setTransactionType(rs.getString("transaction_type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    t.setReferenceAccount(rs.getString("reference_account"));
                    t.setDescription(rs.getString("description"));
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    t.setStatus(rs.getString("status"));
                    list.add(t);
                }
            }
        }
        return list;
    }

    /** Admin view: latest 200 transactions of all accounts. */
    public List<Map<String, Object>> findAllForAdmin() throws SQLException {
        String sql = "SELECT t.transaction_id, a.account_number, t.transaction_type, t.amount, t.balance_after, "
                   + "t.reference_account, t.description, t.transaction_date, t.status "
                   + "FROM transactions t JOIN accounts a ON t.account_id = a.account_id "
                   + "ORDER BY t.transaction_id DESC LIMIT 200";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return DBUtil.toList(rs);
        }
    }

    /** Admin view: latest 200 audit log rows (written by the trigger). */
    public List<Map<String, Object>> findAuditLogs() throws SQLException {
        String sql = "SELECT l.audit_id, a.account_number, l.action_type, l.amount, l.old_balance, l.new_balance, "
                   + "l.description, l.created_at FROM audit_logs l JOIN accounts a ON l.account_id = a.account_id "
                   + "ORDER BY l.audit_id DESC LIMIT 200";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return DBUtil.toList(rs);
        }
    }

    public int count() throws SQLException { return DBUtil.countRows("transactions"); }
}
