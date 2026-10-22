package com.banking.dao;

import com.banking.model.Account;
import com.banking.model.Bank;
import com.banking.model.Transaction;
import com.banking.model.User;
import java.util.List;

public class BankService {

    private UserDAO userDAO = new UserDAO();
    private BankDAO bankDAO = new BankDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    public boolean registerUser(String name, String email, String password) {
        if (userDAO.emailExists(email)) {
            return false;
        }
        User user = new User(0, name, email, password);
        return userDAO.register(user);
    }

    public boolean login(String email, String password) {
        User user = userDAO.login(email, password);
        return user != null;
    }

    public User authenticateUser(String email, String password) {
        return userDAO.login(email, password);
    }

    public boolean createAccount(String email, Bank bank) {
        User user = userDAO.getUserByEmail(email);
        if (user == null || bank == null) {
            return false;
        }
        Account account = accountDAO.createAccount(user.getUserId(), bank.getBankId(), 0.0);
        return account != null;
    }

    public Account getAccount(String email, String bankName) {
        User user = userDAO.getUserByEmail(email);
        Bank bank = bankDAO.getBankByName(bankName);
        if (user == null || bank == null) {
            return null;
        }
        return accountDAO.getAccountByUserAndBank(user.getUserId(), bank.getBankId());
    }

    public boolean deposit(String email, String bankName, double amount) {
        Account account = getAccount(email, bankName);
        if (account == null) {
            return false;
        }
        return transactionDAO.deposit(account.getAccountNumber(), amount, "Deposit into " + bankName);
    }

    public boolean withdraw(String email, String bankName, double amount) {
        Account account = getAccount(email, bankName);
        if (account == null) {
            return false;
        }
        return transactionDAO.withdraw(account.getAccountNumber(), amount, "Withdrawal from " + bankName);
    }

    public boolean transferFunds(String fromEmail, String fromBank, String toEmail, String toBank, double amount) {
        Account sender = getAccount(fromEmail, fromBank);
        Account receiver = getAccount(toEmail, toBank);

        if (sender == null || receiver == null) {
            return false;
        }

        return transactionDAO.transferFunds(sender.getAccountNumber(), receiver.getAccountNumber(), amount, 
            "Transfer from " + fromBank + " to " + toBank);
    }

    public double checkBalance(String email, String bankName) {
        Account account = getAccount(email, bankName);
        if (account == null) {
            return -1;
        }
        return account.getBalance();
    }

    public List<Transaction> showTransactions() {
        return transactionDAO.getAllTransactions();
    }
}
