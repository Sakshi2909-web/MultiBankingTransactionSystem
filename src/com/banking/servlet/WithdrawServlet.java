package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Account;
import com.banking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/withdraw")
public class WithdrawServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/withdraw.jsp");
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
        String accountNumber = request.getParameter("accountNumber");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");

        if (accountNumber == null || accountNumber.trim().isEmpty() || amountStr == null || amountStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Please select an account and specify an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Withdrawal amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Invalid amount entered.");
            return;
        }

        Account account = accountDAO.getAccountByNumber(accountNumber.trim());
        if (account == null || account.getUserId() != currentUser.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Unauthorized or invalid bank account.");
            return;
        }

        if (account.getBalance() < amount) {
            response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Insufficient balance! Available balance is ₹" + 
                String.format("%.2f", account.getBalance()));
            return;
        }

        boolean success = transactionDAO.withdraw(account.getAccountNumber(), amount, 
            description != null && !description.trim().isEmpty() ? description.trim() : "Withdrawal from " + account.getBankName());

        if (success) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp?success=Successfully withdrew ₹" + 
                String.format("%.2f", amount) + " from account " + account.getAccountNumber() + " (" + account.getBankName() + ")!");
        } else {
            response.sendRedirect(request.getContextPath() + "/withdraw.jsp?error=Withdrawal failed. Insufficient funds or database error.");
        }
    }
}
