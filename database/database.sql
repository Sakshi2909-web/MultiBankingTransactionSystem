-- =========================================================
-- Multi Banking Transaction System Database Schema
-- Compatible with MySQL 5.7+ / 8.0+
-- =========================================================

CREATE DATABASE IF NOT EXISTS multibanking;
USE multibanking;

-- 1. Table: users
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS banks;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table: banks
CREATE TABLE banks (
    bank_id INT AUTO_INCREMENT PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL UNIQUE,
    bank_code VARCHAR(20) NOT NULL UNIQUE,
    ifsc_prefix VARCHAR(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table: accounts
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(25) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    bank_id INT NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(20) DEFAULT 'SAVINGS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_account_bank FOREIGN KEY (bank_id) REFERENCES banks(bank_id) ON DELETE RESTRICT,
    CONSTRAINT unique_user_bank UNIQUE (user_id, bank_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Table: transactions
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref VARCHAR(36) NOT NULL UNIQUE,
    from_account VARCHAR(25) NOT NULL,
    to_account VARCHAR(25) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'DEPOSIT', 'WITHDRAW', 'TRANSFER'
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- SEED DATA
-- =========================================================

-- Insert supported banks
INSERT INTO banks (bank_id, bank_name, bank_code, ifsc_prefix) VALUES
(1, 'State Bank of India', 'SBI', 'SBIN0001'),
(2, 'HDFC Bank', 'HDFC', 'HDFC0002'),
(3, 'ICICI Bank', 'ICICI', 'ICIC0003'),
(4, 'Axis Bank', 'AXIS', 'UTIB0004'),
(5, 'Punjab National Bank', 'PNB', 'PUNB0005');

-- Insert demo users (passwords are plain text '123456' for college project demo ease)
INSERT INTO users (user_id, name, email, password, phone) VALUES
(1, 'Rahul Sharma', 'rahul@gmail.com', '123456', '9876543210'),
(2, 'Priya Patel', 'priya@gmail.com', '123456', '9812345678');

-- Insert initial accounts for demo users
INSERT INTO accounts (account_id, account_number, user_id, bank_id, balance, account_type) VALUES
(1, 'SBIN1001', 1, 1, 25000.00, 'SAVINGS'),
(2, 'HDFC1002', 1, 2, 15000.00, 'SAVINGS'),
(3, 'ICIC2001', 2, 3, 35000.00, 'SAVINGS'),
(4, 'SBIN2002', 2, 1, 12000.00, 'SAVINGS');

-- Insert initial transaction records for demo
INSERT INTO transactions (transaction_ref, from_account, to_account, amount, type, description, timestamp) VALUES
('TXN-INIT-001', 'SBIN1001', 'SBIN1001', 25000.00, 'DEPOSIT', 'Initial Account Opening Deposit', NOW() - INTERVAL 5 DAY),
('TXN-INIT-002', 'HDFC1002', 'HDFC1002', 15000.00, 'DEPOSIT', 'Initial Account Opening Deposit', NOW() - INTERVAL 4 DAY),
('TXN-INIT-003', 'ICIC2001', 'ICIC2001', 40000.00, 'DEPOSIT', 'Initial Account Opening Deposit', NOW() - INTERVAL 3 DAY),
('TXN-INIT-004', 'ICIC2001', 'ICIC2001', 5000.00, 'WITHDRAW', 'ATM Cash Withdrawal', NOW() - INTERVAL 2 DAY),
('TXN-INIT-005', 'SBIN2002', 'SBIN2002', 12000.00, 'DEPOSIT', 'Salary Credit', NOW() - INTERVAL 1 DAY);
