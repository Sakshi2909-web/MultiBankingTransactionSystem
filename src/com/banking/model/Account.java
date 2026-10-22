package com.banking.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private int accountId;
    private String accountNumber;
    private int userId;
    private String userName;
    private String userEmail;
    private int bankId;
    private String bankName;
    private String bankCode;
    private String ifscPrefix;
    private double balance;
    private String accountType;
    private Timestamp createdAt;

    // References for object graph compatibility
    private User user;
    private Bank bank;

    public Account() {
        this.balance = 0.0;
        this.accountType = "SAVINGS";
    }

    public Account(String accountNumber, User user, Bank bank) {
        this.accountNumber = accountNumber;
        this.user = user;
        this.bank = bank;
        if (user != null) {
            this.userId = user.getUserId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
        }
        if (bank != null) {
            this.bankId = bank.getBankId();
            this.bankName = bank.getBankName();
            this.bankCode = bank.getBankCode();
            this.ifscPrefix = bank.getIfscPrefix();
        }
        this.balance = 0.0;
        this.accountType = "SAVINGS";
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getIfscPrefix() {
        return ifscPrefix;
    }

    public void setIfscPrefix(String ifscPrefix) {
        this.ifscPrefix = ifscPrefix;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
        }
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
        if (bank != null) {
            this.bankId = bank.getBankId();
            this.bankName = bank.getBankName();
            this.bankCode = bank.getBankCode();
            this.ifscPrefix = bank.getIfscPrefix();
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Account [" + accountNumber + " | Bank=" + bankName + " | Balance=₹" + balance + "]";
    }
}
