package com.banking.model;

import java.io.Serializable;

public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bankId;
    private String bankName;
    private String bankCode;
    private String ifscPrefix;

    public Bank() {}

    public Bank(int bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }

    public Bank(int bankId, String bankName, String bankCode, String ifscPrefix) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.ifscPrefix = ifscPrefix;
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

    @Override
    public String toString() {
        return bankName + " (" + bankCode + ")";
    }
}
