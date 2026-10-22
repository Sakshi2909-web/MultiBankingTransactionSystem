package com.banking.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionId;
    private String transactionRef;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String type; // 'DEPOSIT', 'WITHDRAW', 'TRANSFER'
    private String description;
    private Timestamp timestamp;

    public Transaction() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Transaction(String fromAccount, String toAccount, double amount, String type) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Transaction(int transactionId, String transactionRef, String fromAccount, String toAccount,
                       double amount, String type, String description, Timestamp timestamp) {
        this.transactionId = transactionId;
        this.transactionRef = transactionRef;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return type + " | Amount: ₹" + amount + " | From: " + fromAccount + " | To: " + toAccount + " | Time: " + timestamp;
    }
}
