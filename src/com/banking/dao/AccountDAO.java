package com.banking.dao;

import com.banking.model.Account;
import com.banking.model.Bank;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountDAO {

    public String generateAccountNumber(String bankCode) {
        int randomNum = 1000 + new Random().nextInt(9000);
        return bankCode.toUpperCase() + randomNum;
    }

    public boolean hasAccountInBank(int userId, int bankId) {
        String sql = "SELECT account_id FROM accounts WHERE user_id = ? AND bank_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, bankId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return false;
    }

    public Account createAccount(int userId, int bankId, double initialDeposit) {
        BankDAO bankDAO = new BankDAO();
        Bank bank = bankDAO.getBankById(bankId);
        if (bank == null) {
            return null;
        }

        if (hasAccountInBank(userId, bankId)) {
            return null; // One account per bank per user
        }

        String accountNumber = generateAccountNumber(bank.getBankCode());
        String sql = "INSERT INTO accounts (account_number, user_id, bank_id, balance, account_type) VALUES (?, ?, ?, ?, 'SAVINGS')";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, accountNumber);
            ps.setInt(2, userId);
            ps.setInt(3, bankId);
            ps.setDouble(4, initialDeposit > 0 ? initialDeposit : 0.00);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                rs = ps.getGeneratedKeys();
                int accountId = 0;
                if (rs.next()) {
                    accountId = rs.getInt(1);
                }

                if (initialDeposit > 0) {
                    String txnSql = "INSERT INTO transactions (transaction_ref, from_account, to_account, amount, type, description) VALUES (?, ?, ?, ?, 'DEPOSIT', ?)";
                    PreparedStatement txnPs = conn.prepareStatement(txnSql);
                    txnPs.setString(1, "TXN-DEP-" + System.currentTimeMillis());
                    txnPs.setString(2, accountNumber);
                    txnPs.setString(3, accountNumber);
                    txnPs.setDouble(4, initialDeposit);
                    txnPs.setString(5, "Initial Account Opening Deposit");
                    txnPs.executeUpdate();
                    txnPs.close();
                }

                conn.commit();

                Account account = new Account();
                account.setAccountId(accountId);
                account.setAccountNumber(accountNumber);
                account.setUserId(userId);
                account.setBankId(bankId);
                account.setBankName(bank.getBankName());
                account.setBankCode(bank.getBankCode());
                account.setIfscPrefix(bank.getIfscPrefix());
                account.setBalance(initialDeposit > 0 ? initialDeposit : 0.00);
                account.setAccountType("SAVINGS");
                return account;
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
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }

    public List<Account> getAccountsByUser(int userId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.*, b.bank_name, b.bank_code, b.ifsc_prefix, u.name as user_name, u.email as user_email " +
                     "FROM accounts a " +
                     "JOIN banks b ON a.bank_id = b.bank_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ? ORDER BY a.account_id ASC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Account acc = mapAccount(rs);
                list.add(acc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT a.*, b.bank_name, b.bank_code, b.ifsc_prefix, u.name as user_name, u.email as user_email " +
                     "FROM accounts a " +
                     "JOIN banks b ON a.bank_id = b.bank_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.account_number = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, accountNumber.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }

    public Account getAccountByUserAndBank(int userId, int bankId) {
        String sql = "SELECT a.*, b.bank_name, b.bank_code, b.ifsc_prefix, u.name as user_name, u.email as user_email " +
                     "FROM accounts a " +
                     "JOIN banks b ON a.bank_id = b.bank_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ? AND a.bank_id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, bankId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }

    public double getTotalBalance(int userId) {
        String sql = "SELECT SUM(balance) as total FROM accounts WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return 0.00;
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setAccountId(rs.getInt("account_id"));
        acc.setAccountNumber(rs.getString("account_number"));
        acc.setUserId(rs.getInt("user_id"));
        acc.setUserName(rs.getString("user_name"));
        acc.setUserEmail(rs.getString("user_email"));
        acc.setBankId(rs.getInt("bank_id"));
        acc.setBankName(rs.getString("bank_name"));
        acc.setBankCode(rs.getString("bank_code"));
        acc.setIfscPrefix(rs.getString("ifsc_prefix"));
        acc.setBalance(rs.getDouble("balance"));
        acc.setAccountType(rs.getString("account_type"));
        acc.setCreatedAt(rs.getTimestamp("created_at"));
        return acc;
    }
}
