package com.banking.config;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * JDBC helper: loads db.properties once and hands out Connections.
 * Run main() to test the JDBC connection (Phase 2).
 */
public final class DBUtil {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IllegalStateException("db.properties not found on classpath");
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"), PROPS.getProperty("db.user"), PROPS.getProperty("db.password"));
    }

    /** Counts rows of a table. Only ever called with fixed table names from our own DAOs. */
    public static int countRows(String table) throws SQLException {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Converts a ResultSet into a list of column-label -> value maps (handy for admin tables in JSP). */
    public static List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int n = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= n; i++) {
                Object v = rs.getObject(i);
                if (v instanceof java.time.LocalDateTime ldt) v = Timestamp.valueOf(ldt); // so <fmt:formatDate> works
                row.put(md.getColumnLabel(i), v);
            }
            list.add(row);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        try (Connection c = getConnection()) {
            System.out.println("JDBC connection OK -> " + c.getMetaData().getURL());
        }
    }
}
