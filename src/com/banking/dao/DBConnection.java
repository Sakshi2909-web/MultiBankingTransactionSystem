package com.banking.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {

    private static String DB_URL = "jdbc:mysql://localhost:3306/multibanking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String DB_USER = "root";
    private static String DB_PASSWORD = "root";

    private static boolean useEmbedded = false;
    private static final String H2_URL = "jdbc:h2:./database/multibanking_db;MODE=MySQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;AUTO_SERVER=TRUE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    static {
        try {
            InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                if (props.getProperty("db.url") != null) DB_URL = props.getProperty("db.url");
                if (props.getProperty("db.user") != null) DB_USER = props.getProperty("db.user");
                if (props.getProperty("db.password") != null) DB_PASSWORD = props.getProperty("db.password");
            }
        } catch (Exception ignored) {}

        // Load drivers
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ignored) {}

        try {
            Class.forName("org.h2.Driver");
        } catch (Exception ignored) {}

        // Test initial connectivity
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✅ [Database] Successfully connected to live MySQL Server!");
            useEmbedded = false;
        } catch (Exception e1) {
            try (Connection c2 = DriverManager.getConnection(DB_URL, DB_USER, "")) {
                System.out.println("✅ [Database] Successfully connected to live MySQL Server (blank password)!");
                DB_PASSWORD = "";
                useEmbedded = false;
            } catch (Exception e2) {
                System.out.println("ℹ️ [Database] MySQL Server not reachable. Seamlessly switching to Embedded Database Engine!");
                useEmbedded = true;
                initEmbeddedDatabase();
            }
        }
    }

    private static synchronized void initEmbeddedDatabase() {
        try (Connection conn = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
             Statement st = conn.createStatement()) {

            // Check if tables already exist
            ResultSet rs = conn.getMetaData().getTables(null, null, "USERS", null);
            if (!rs.next()) {
                rs = conn.getMetaData().getTables(null, null, "users", null);
            }
            if (rs.next()) {
                return; // Already initialized
            }

            System.out.println("⚡ [Database] Initializing embedded schema and seed data...");

            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "phone VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.execute("CREATE TABLE IF NOT EXISTS banks (" +
                    "bank_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "bank_name VARCHAR(100) NOT NULL UNIQUE, " +
                    "bank_code VARCHAR(20) NOT NULL UNIQUE, " +
                    "ifsc_prefix VARCHAR(10) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "account_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_number VARCHAR(25) NOT NULL UNIQUE, " +
                    "user_id INT NOT NULL, " +
                    "bank_id INT NOT NULL, " +
                    "balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00, " +
                    "account_type VARCHAR(20) DEFAULT 'SAVINGS', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT unique_user_bank UNIQUE (user_id, bank_id))");

            st.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "transaction_ref VARCHAR(36) NOT NULL UNIQUE, " +
                    "from_account VARCHAR(25) NOT NULL, " +
                    "to_account VARCHAR(25) NOT NULL, " +
                    "amount DECIMAL(15, 2) NOT NULL, " +
                    "type VARCHAR(20) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Seed Banks
            st.execute("MERGE INTO banks (bank_id, bank_name, bank_code, ifsc_prefix) KEY(bank_id) VALUES " +
                    "(1, 'State Bank of India', 'SBI', 'SBIN0001'), " +
                    "(2, 'HDFC Bank', 'HDFC', 'HDFC0002'), " +
                    "(3, 'ICICI Bank', 'ICICI', 'ICIC0003'), " +
                    "(4, 'Axis Bank', 'AXIS', 'UTIB0004'), " +
                    "(5, 'Punjab National Bank', 'PNB', 'PUNB0005')");

            // Seed Users
            st.execute("MERGE INTO users (user_id, name, email, password, phone) KEY(user_id) VALUES " +
                    "(1, 'Rahul Sharma', 'rahul@gmail.com', '123456', '9876543210'), " +
                    "(2, 'Priya Patel', 'priya@gmail.com', '123456', '9812345678')");

            // Seed Accounts
            st.execute("MERGE INTO accounts (account_id, account_number, user_id, bank_id, balance, account_type) KEY(account_id) VALUES " +
                    "(1, 'SBIN1001', 1, 1, 25000.00, 'SAVINGS'), " +
                    "(2, 'HDFC1002', 1, 2, 15000.00, 'SAVINGS'), " +
                    "(3, 'ICIC2001', 2, 3, 35000.00, 'SAVINGS'), " +
                    "(4, 'SBIN2002', 2, 1, 12000.00, 'SAVINGS')");

            // Seed Transactions
            st.execute("MERGE INTO transactions (transaction_ref, from_account, to_account, amount, type, description) KEY(transaction_ref) VALUES " +
                    "('TXN-INIT-001', 'SBIN1001', 'SBIN1001', 25000.00, 'DEPOSIT', 'Initial Opening Deposit'), " +
                    "('TXN-INIT-002', 'HDFC1002', 'HDFC1002', 15000.00, 'DEPOSIT', 'Initial Opening Deposit'), " +
                    "('TXN-INIT-003', 'ICIC2001', 'ICIC2001', 40000.00, 'DEPOSIT', 'Initial Opening Deposit'), " +
                    "('TXN-INIT-004', 'ICIC2001', 'ICIC2001', 5000.00, 'WITHDRAW', 'ATM Cash Withdrawal'), " +
                    "('TXN-INIT-005', 'SBIN2002', 'SBIN2002', 12000.00, 'DEPOSIT', 'Salary Credit')");

            System.out.println("✅ [Database] Embedded Database Engine initialized with full demo dataset!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (useEmbedded) {
            return DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }

        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            if (!DB_PASSWORD.isEmpty()) {
                try {
                    return DriverManager.getConnection(DB_URL, DB_USER, "");
                } catch (SQLException ignored) {}
            }
            // Fallback to embedded if MySQL went down
            useEmbedded = true;
            initEmbeddedDatabase();
            return DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (ps != null) {
            try { ps.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, PreparedStatement ps) {
        close(conn, ps, null);
    }
}
