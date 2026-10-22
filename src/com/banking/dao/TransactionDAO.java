package com.banking.dao;

import com.banking.model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDAO {

    public String generateTxnRef(String prefix) {
        return "TXN-" + prefix.toUpperCase() + "-" + System.currentTimeMillis();
    }

    public boolean deposit(String accountNumber, double amount, String description) {
        if (amount <= 0) return false;

        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (transaction_ref, from_account, to_account, amount, type, description) VALUES (?, ?, ?, ?, 'DEPOSIT', ?)";

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psTxn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setDouble(1, amount);
            psUpdate.setString(2, accountNumber);
            int updated = psUpdate.executeUpdate();

            if (updated > 0) {
                psTxn = conn.prepareStatement(txnSql);
                psTxn.setString(1, generateTxnRef("DEP"));
                psTxn.setString(2, accountNumber);
                psTxn.setString(3, accountNumber);
                psTxn.setDouble(4, amount);
                psTxn.setString(5, (description != null && !description.trim().isEmpty()) ? description : "Cash Deposit");
                psTxn.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            if (psTxn != null) { try { psTxn.close(); } catch (SQLException ignored) {} }
            DBConnection.close(conn, psUpdate);
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount, String description) {
        if (amount <= 0) return false;

        String checkSql = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (transaction_ref, from_account, to_account, amount, type, description) VALUES (?, ?, ?, ?, 'WITHDRAW', ?)";

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psTxn = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, accountNumber);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < amount) {
                    conn.rollback();
                    return false; // Insufficient balance
                }
            } else {
                conn.rollback();
                return false; // Account not found
            }

            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setDouble(1, amount);
            psUpdate.setString(2, accountNumber);
            int updated = psUpdate.executeUpdate();

            if (updated > 0) {
                psTxn = conn.prepareStatement(txnSql);
                psTxn.setString(1, generateTxnRef("WTH"));
                psTxn.setString(2, accountNumber);
                psTxn.setString(3, accountNumber);
                psTxn.setDouble(4, amount);
                psTxn.setString(5, (description != null && !description.trim().isEmpty()) ? description : "Cash Withdrawal");
                psTxn.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
            if (psCheck != null) { try { psCheck.close(); } catch (SQLException ignored) {} }
            if (psTxn != null) { try { psTxn.close(); } catch (SQLException ignored) {} }
            DBConnection.close(conn, psUpdate);
        }
        return false;
    }

    public boolean transferFunds(String fromAccount, String toAccount, double amount, String description) {
        if (amount <= 0 || fromAccount.equalsIgnoreCase(toAccount)) {
            return false;
        }

        String checkSenderSql = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String checkReceiverSql = "SELECT account_id FROM accounts WHERE account_number = ?";
        String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (transaction_ref, from_account, to_account, amount, type, description) VALUES (?, ?, ?, ?, 'TRANSFER', ?)";

        Connection conn = null;
        PreparedStatement psCheckSender = null;
        PreparedStatement psCheckReceiver = null;
        PreparedStatement psDeduct = null;
        PreparedStatement psCredit = null;
        PreparedStatement psTxn = null;
        ResultSet rsSender = null;
        ResultSet rsReceiver = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check sender
            psCheckSender = conn.prepareStatement(checkSenderSql);
            psCheckSender.setString(1, fromAccount);
            rsSender = psCheckSender.executeQuery();
            if (rsSender.next()) {
                double senderBal = rsSender.getDouble("balance");
                if (senderBal < amount) {
                    conn.rollback();
                    return false; // Insufficient balance
                }
            } else {
                conn.rollback();
                return false; // Sender account not found
            }

            // 2. Check receiver
            psCheckReceiver = conn.prepareStatement(checkReceiverSql);
            psCheckReceiver.setString(1, toAccount);
            rsReceiver = psCheckReceiver.executeQuery();
            if (!rsReceiver.next()) {
                conn.rollback();
                return false; // Receiver account not found
            }

            // 3. Deduct from sender
            psDeduct = conn.prepareStatement(deductSql);
            psDeduct.setDouble(1, amount);
            psDeduct.setString(2, fromAccount);
            psDeduct.executeUpdate();

            // 4. Credit to receiver
            psCredit = conn.prepareStatement(creditSql);
            psCredit.setDouble(1, amount);
            psCredit.setString(2, toAccount);
            psCredit.executeUpdate();

            // 5. Record transaction
            psTxn = conn.prepareStatement(txnSql);
            psTxn.setString(1, generateTxnRef("TRF"));
            psTxn.setString(2, fromAccount);
            psTxn.setString(3, toAccount);
            psTxn.setDouble(4, amount);
            psTxn.setString(5, (description != null && !description.trim().isEmpty()) ? description : "Fund Transfer from " + fromAccount + " to " + toAccount);
            psTxn.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            if (rsSender != null) { try { rsSender.close(); } catch (SQLException ignored) {} }
            if (rsReceiver != null) { try { rsReceiver.close(); } catch (SQLException ignored) {} }
            if (psCheckSender != null) { try { psCheckSender.close(); } catch (SQLException ignored) {} }
            if (psCheckReceiver != null) { try { psCheckReceiver.close(); } catch (SQLException ignored) {} }
            if (psDeduct != null) { try { psDeduct.close(); } catch (SQLException ignored) {} }
            if (psCredit != null) { try { psCredit.close(); } catch (SQLException ignored) {} }
            if (psTxn != null) { try { psTxn.close(); } catch (SQLException ignored) {} }
            DBConnection.close(conn, null);
        }
        return false;
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY timestamp DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, accountNumber);
            ps.setString(2, accountNumber);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT DISTINCT t.* FROM transactions t " +
                     "JOIN accounts a ON (t.from_account = a.account_number OR t.to_account = a.account_number) " +
                     "WHERE a.user_id = ? " +
                     "ORDER BY t.timestamp DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setTransactionRef(rs.getString("transaction_ref"));
        t.setFromAccount(rs.getString("from_account"));
        t.setToAccount(rs.getString("to_account"));
        t.setAmount(rs.getDouble("amount"));
        t.setType(rs.getString("type"));
        t.setDescription(rs.getString("description"));
        t.setTimestamp(rs.getTimestamp("timestamp"));
        return t;
    }
}
