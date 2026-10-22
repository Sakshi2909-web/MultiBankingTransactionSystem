package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.dao.BankDAO;
import com.banking.dao.TransactionDAO;
import com.banking.dao.UserDAO;
import com.banking.model.Account;
import com.banking.model.Bank;
import com.banking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AccountDAO accountDAO = new AccountDAO();
    private BankDAO bankDAO = new BankDAO();
    private UserDAO userDAO = new UserDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/transfer.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired. Please login again.");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String fromAccount = request.getParameter("fromAccount");
        String transferMode = request.getParameter("transferMode"); // "byAccount" or "byEmail"
        String toAccount = request.getParameter("toAccount");
        String receiverEmail = request.getParameter("receiverEmail");
        String receiverBankIdStr = request.getParameter("receiverBankId");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");

        if (fromAccount == null || fromAccount.trim().isEmpty() || amountStr == null || amountStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Source account and amount are required!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Transfer amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Invalid amount format entered.");
            return;
        }

        // Validate sender account
        Account senderAccount = accountDAO.getAccountByNumber(fromAccount.trim());
        if (senderAccount == null || senderAccount.getUserId() != currentUser.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Invalid or unauthorized sender account.");
            return;
        }

        if (senderAccount.getBalance() < amount) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Insufficient balance! Your available balance in " + 
                senderAccount.getBankName() + " is ₹" + String.format("%.2f", senderAccount.getBalance()));
            return;
        }

        // Determine target account
        String resolvedTargetAccount = null;
        if ("byEmail".equalsIgnoreCase(transferMode)) {
            if (receiverEmail == null || receiverEmail.trim().isEmpty() || receiverBankIdStr == null || receiverBankIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Please enter receiver's email and select target bank.");
                return;
            }
            User receiverUser = userDAO.getUserByEmail(receiverEmail.trim().toLowerCase());
            if (receiverUser == null) {
                response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Receiver with email " + receiverEmail + " not found!");
                return;
            }
            int receiverBankId = Integer.parseInt(receiverBankIdStr);
            Account receiverAccount = accountDAO.getAccountByUserAndBank(receiverUser.getUserId(), receiverBankId);
            if (receiverAccount == null) {
                response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Receiver does not have an active account in the selected bank!");
                return;
            }
            resolvedTargetAccount = receiverAccount.getAccountNumber();
        } else {
            if (toAccount == null || toAccount.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Please specify the beneficiary account number.");
                return;
            }
            resolvedTargetAccount = toAccount.trim();
        }

        // Cannot transfer to same account
        if (senderAccount.getAccountNumber().equalsIgnoreCase(resolvedTargetAccount)) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Sender and beneficiary accounts cannot be identical.");
            return;
        }

        // Verify beneficiary account exists
        Account targetAccount = accountDAO.getAccountByNumber(resolvedTargetAccount);
        if (targetAccount == null) {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Beneficiary account " + resolvedTargetAccount + " does not exist!");
            return;
        }

        String note = (description != null && !description.trim().isEmpty()) ? description.trim() : 
            "Transfer from " + senderAccount.getBankName() + " (" + senderAccount.getAccountNumber() + ") to " + 
            targetAccount.getBankName() + " (" + targetAccount.getAccountNumber() + ")";

        boolean success = transactionDAO.transferFunds(senderAccount.getAccountNumber(), targetAccount.getAccountNumber(), amount, note);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp?success=Successfully transferred ₹" + 
                String.format("%.2f", amount) + " to " + targetAccount.getUserName() + " (" + targetAccount.getBankName() + " - " + targetAccount.getAccountNumber() + ")!");
        } else {
            response.sendRedirect(request.getContextPath() + "/transfer.jsp?error=Fund transfer could not be completed. Please try again.");
        }
    }
}
