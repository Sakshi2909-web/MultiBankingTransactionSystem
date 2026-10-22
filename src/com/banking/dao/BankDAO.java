package com.banking.dao;

import com.banking.model.Bank;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {

    public List<Bank> getAllBanks() {
        List<Bank> banks = new ArrayList<>();
        String sql = "SELECT * FROM banks ORDER BY bank_id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Bank b = new Bank(
                    rs.getInt("bank_id"),
                    rs.getString("bank_name"),
                    rs.getString("bank_code"),
                    rs.getString("ifsc_prefix")
                );
                banks.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return banks;
    }

    public Bank getBankById(int bankId) {
        String sql = "SELECT * FROM banks WHERE bank_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bankId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Bank(
                    rs.getInt("bank_id"),
                    rs.getString("bank_name"),
                    rs.getString("bank_code"),
                    rs.getString("ifsc_prefix")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }

    public Bank getBankByName(String bankName) {
        String sql = "SELECT * FROM banks WHERE LOWER(bank_name) = LOWER(?) OR LOWER(bank_code) = LOWER(?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bankName.trim());
            ps.setString(2, bankName.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Bank(
                    rs.getInt("bank_id"),
                    rs.getString("bank_name"),
                    rs.getString("bank_code"),
                    rs.getString("ifsc_prefix")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }
}
